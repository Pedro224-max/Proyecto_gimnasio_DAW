package dao;

import db.ConexionDB;
import model.Cliente;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public boolean registrarCliente(Cliente cliente) {
        // Consultas SQL con "?" para evitar Inyección SQL (Requisito de la rúbrica)
        String sqlUsuario = "INSERT INTO usuarios (username, password, email, nombre, apellidos, dni, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlCliente = "INSERT INTO clientes (id_usuario, peso, porcentaje_grasa) VALUES (?, ?, ?)";
        
        boolean exito = false;

        // try-with-resources NO se usa aquí con la Connection principal porque controlamos la transacción a mano,
        // pero SÍ lo usaremos con los PreparedStatement.
        try {
            ConexionDB.iniciarTransaccion(); // Desactivamos el auto-guardado
            Connection conn = ConexionDB.getConnection();

            int idGenerado = 0;

            // 1. Insertar en la tabla padre (usuarios)
            // Le decimos a Java que nos devuelva el ID que MySQL genere automáticamente
            try (PreparedStatement psUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                psUsuario.setString(1, cliente.getUsername());
                psUsuario.setString(2, cliente.getPassword());
                psUsuario.setString(3, cliente.getEmail());
                psUsuario.setString(4, cliente.getNombre());
                psUsuario.setString(5, cliente.getApellidos());
                psUsuario.setString(6, cliente.getDni());
                psUsuario.setString(7, "CLIENTE"); // Forzamos el rol
                
                int filasUsuario = psUsuario.executeUpdate();
                
                if (filasUsuario > 0) {
                    // Capturamos el ID que se acaba de crear
                    try (ResultSet rs = psUsuario.getGeneratedKeys()) {
                        if (rs.next()) {
                            idGenerado = rs.getInt(1);
                        }
                    }
                }
            }

            // 2. Si se creó el usuario, insertamos en la tabla hija (clientes)
            if (idGenerado > 0) {
                try (PreparedStatement psCliente = conn.prepareStatement(sqlCliente)) {
                    psCliente.setInt(1, idGenerado); // Usamos el ID del padre
                    psCliente.setDouble(2, cliente.getPeso());
                    psCliente.setDouble(3, cliente.getPorcentajeGrasa());
                    
                    psCliente.executeUpdate();
                }
                
                // Si llegamos hasta aquí sin errores, guardamos todo definitivamente
                ConexionDB.confirmarTransaccion();
                exito = true;
            } else {
                // Si no se generó el ID, cancelamos
                ConexionDB.cancelarTransaccion();
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar cliente: " + e.getMessage());
            try {
                // Si salta cualquier error (ej. DNI duplicado), deshacemos todo
                ConexionDB.cancelarTransaccion();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        
        return exito;
    }

    @Override
    public Usuario validarLogin(String username, String password) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        
        // Aquí SÍ usamos try-with-resources con la Connection porque es una consulta simple (SELECT),
        // no necesitamos transacciones manuales ni rollback.
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si hay coincidencia, creamos el objeto Usuario con los datos de la BD
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setRol(rs.getString("rol"));
                    // No guardamos la contraseña en el objeto por seguridad en la app
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en el login: " + e.getMessage());
        }
        
        return usuario; // Si devuelve null, es que el usuario o clave son incorrectos
    }

    @Override
    public java.util.List<Cliente> listarClientes() {
        java.util.List<Cliente> lista = new java.util.ArrayList<>();
        // El JOIN une la información básica del usuario con la física del cliente
        String sql = "SELECT u.id, u.username, u.nombre, u.apellidos, u.dni, c.peso, c.porcentaje_grasa " +
                     "FROM usuarios u INNER JOIN clientes c ON u.id = c.id_usuario " +
                     "WHERE u.rol = 'CLIENTE'";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt("id"));
                c.setUsername(rs.getString("username"));
                c.setNombre(rs.getString("nombre"));
                c.setApellidos(rs.getString("apellidos"));
                c.setDni(rs.getString("dni"));
                c.setPeso(rs.getDouble("peso"));
                c.setPorcentajeGrasa(rs.getDouble("porcentaje_grasa"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0; // Devuelve true si borró al menos 1 fila
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public java.util.List<dto.MatriculaDTO> listarMatriculas() {
        java.util.List<dto.MatriculaDTO> lista = new java.util.ArrayList<>();
        // Consulta SQL con múltiples JOIN para la tabla N:M
        String sql = "SELECT u.nombre, u.apellidos, cl.nombre AS clase, mc.fecha_inscripcion " +
                     "FROM matriculas_clases mc " +
                     "JOIN clientes c ON mc.id_cliente = c.id_usuario " +
                     "JOIN usuarios u ON c.id_usuario = u.id " +
                     "JOIN clases cl ON mc.id_clase = cl.id";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                dto.MatriculaDTO m = new dto.MatriculaDTO();
                m.setNombreCliente(rs.getString("nombre"));
                m.setApellidosCliente(rs.getString("apellidos"));
                m.setNombreClase(rs.getString("clase"));
                m.setFechaInscripcion(rs.getString("fecha_inscripcion")); // Asegúrate de que este setter reciba un String
                lista.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar matrículas: " + e.getMessage());
        }
        return lista;
    }
}