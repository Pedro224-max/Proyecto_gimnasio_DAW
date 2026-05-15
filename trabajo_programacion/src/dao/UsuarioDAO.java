package dao;

import model.Cliente;
import model.Usuario;

public interface UsuarioDAO {
    // Método para registrar un nuevo cliente en la BD.
    boolean registrarCliente(Cliente cliente);    

    Usuario validarLogin(String username, String password);
    java.util.List<Cliente> listarClientes();
    boolean eliminarUsuario(int id);
    java.util.List<dto.MatriculaDTO> listarMatriculas();
}
