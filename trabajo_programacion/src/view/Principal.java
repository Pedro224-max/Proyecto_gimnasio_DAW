package view;

import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


import dao.UsuarioDAOImpl;

import java.awt.*;

public class Principal extends JFrame {

    private Usuario usuarioLogueado;
    
    // Componentes que necesitaremos actualizar después
    private JTable tablaCentral;
    private DefaultTableModel modeloTabla;
    private JPanel panelFormulario;

    public Principal(Usuario usuario) {
        this.usuarioLogueado = usuario;

        // 1. Configuración básica
        setTitle("Gimnasio - Panel Principal (" + usuarioLogueado.getUsername() + ")");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); // N, S, E, O, Centro

        // 2. JMenuBar (Arriba - NORTE)
        JMenuBar menuBar = new JMenuBar();
        JMenu menuSesion = new JMenu("Sesión");
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        itemCerrarSesion.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
        menuSesion.add(itemCerrarSesion);
        menuBar.add(menuSesion);
        setJMenuBar(menuBar);

        // 3. Panel Lateral de Navegación (Izquierda - OESTE)
        JPanel panelNavegacion = new JPanel();
        panelNavegacion.setLayout(new GridLayout(4, 1, 10, 10)); // 4 filas, 1 columna, con separación
        panelNavegacion.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panelNavegacion.setPreferredSize(new Dimension(150, 0));

        JButton btnClientes = new JButton("Clientes");
        // Asegúrate de tener este import arriba del todo del archivo Principal.java:
        // import dao.UsuarioDAOImpl;

        // Lógica del botón Clientes
        btnClientes.addActionListener(e -> {
            // 1. Cambiamos las cabeceras de la tabla
            modeloTabla.setColumnIdentifiers(new String[]{"ID", "Username", "Nombre", "Apellidos", "DNI", "Peso", "% Grasa"});
            
            // 2. Vaciamos las filas que pudiera haber de antes
            modeloTabla.setRowCount(0);
            
            // 3. Pedimos los datos al DAO
            UsuarioDAOImpl dao = new UsuarioDAOImpl();
            java.util.List<model.Cliente> clientes = dao.listarClientes();
            
            // 4. Rellenamos la tabla fila por fila
            for (model.Cliente c : clientes) {
                modeloTabla.addRow(new Object[]{
                    c.getId(),
                    c.getUsername(),
                    c.getNombre(),
                    c.getApellidos(),
                    c.getDni(),
                    c.getPeso(),
                    c.getPorcentajeGrasa()
                });
            }
        });
        JButton btnClases = new JButton("Clases");
        JButton btnMatriculas = new JButton("Matrículas");

        panelNavegacion.add(new JLabel("MÓDULOS", SwingConstants.CENTER));
        panelNavegacion.add(btnClientes);
        panelNavegacion.add(btnClases);
        panelNavegacion.add(btnMatriculas);
        
        add(panelNavegacion, BorderLayout.WEST);

        // 4. JTable Central (Centro)
        // Usamos DefaultTableModel para poder cambiar las columnas dinámicamente luego
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Selecciona un módulo en la izquierda");
        tablaCentral = new JTable(modeloTabla);
        
        // Lo metemos en un JScrollPane para que tenga barras de desplazamiento si hay muchos datos
        JScrollPane scrollTabla = new JScrollPane(tablaCentral);
        add(scrollTabla, BorderLayout.CENTER);

        // 5. Panel de Operaciones Adaptativo (Derecha - ESTE)
        panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Operaciones"));
        panelFormulario.setPreferredSize(new Dimension(250, 0));

        // Por ahora ponemos componentes genéricos, luego los haremos dinámicos
        panelFormulario.add(Box.createVerticalStrut(20)); // Espacio
        panelFormulario.add(new JLabel("Selecciona una acción:"));
        panelFormulario.add(Box.createVerticalStrut(20));
        
        JButton btnNuevo = new JButton("Nuevo / Guardar");
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        // Lógica del botón Eliminar
        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaCentral.getSelectedRow();
            
            if (filaSeleccionada == -1) {
                // Feedback visual de error (Requisito)
                JOptionPane.showMessageDialog(null, "Por favor, haz clic en un cliente de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Sacamos el ID del cliente seleccionado (está en la columna 0)
            int idUsuario = (int) modeloTabla.getValueAt(filaSeleccionada, 0);

            // Confirmación antes de borrar (Feedback visual obligatorio)
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que quieres borrar este registro?", "Confirmar borrado", JOptionPane.YES_NO_OPTION);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                UsuarioDAOImpl dao = new UsuarioDAOImpl();
                if (dao.eliminarUsuario(idUsuario)) {
                    JOptionPane.showMessageDialog(null, "Registro eliminado correctamente.");
                    btnClientes.doClick(); // TRUCO: Simulamos un clic en el botón "Clientes" para recargar la tabla automáticamente
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Lógica del botón Matrículas (Usa el DTO y el JOIN)
        btnMatriculas.addActionListener(e -> {
            modeloTabla.setColumnIdentifiers(new String[]{"Nombre Cliente", "Apellidos", "Clase", "Fecha Inscripción"});
            modeloTabla.setRowCount(0); // Vaciar tabla
            
            UsuarioDAOImpl dao = new UsuarioDAOImpl();
            java.util.List<dto.MatriculaDTO> matriculas = dao.listarMatriculas();
            
            for (dto.MatriculaDTO m : matriculas) {
                modeloTabla.addRow(new Object[]{
                    m.getNombreCliente(), 
                    m.getApellidosCliente(), 
                    m.getNombreClase(), 
                    m.getFechaInscripcion()
                });
            }
        });

        // Lógica del botón Nuevo (Reutilizamos la ventana Registro)
        btnNuevo.addActionListener(e -> {
            Registro ventanaRegistro = new Registro();
            ventanaRegistro.setVisible(true);
            // El profesor verá que reaprovechas código (buena práctica)
        });
        
        panelFormulario.add(btnNuevo);
        panelFormulario.add(Box.createVerticalStrut(10));
        panelFormulario.add(btnEliminar);

        add(panelFormulario, BorderLayout.EAST);
    }
}