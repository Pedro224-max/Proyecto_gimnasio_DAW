package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import model.Usuario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnEntrar;
    private JButton btnRegistro;

    public Login() {
        // 1. Configuración básica de la ventana
        setTitle("Gimnasio - Iniciar Sesión");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra el programa al darle a la X
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setLayout(null); // Posicionamiento manual de componentes

        // 2. Crear componentes
        JLabel lblUsername = new JLabel("Usuario:");
        lblUsername.setBounds(50, 30, 80, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(130, 30, 150, 25);
        add(txtUsername);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(50, 70, 80, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 70, 150, 25);
        add(txtPassword);

        btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(130, 110, 150, 30);
        add(btnEntrar);

        // Enlace/Botón a registro (Requisito de la rúbrica)
        btnRegistro = new JButton("Crear cuenta");
        btnRegistro.setBounds(130, 150, 150, 30);
        add(btnRegistro);

        btnRegistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Registro ventaRegistro = new Registro();
                ventaRegistro.setVisible(true);
            }
        });

        // 3. Lógica del botón Entrar (El controlador)
        btnEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUsername.getText();
                String pass = new String(txtPassword.getPassword()); // JPasswordField requiere esto

                UsuarioDAO dao = new UsuarioDAOImpl();
                Usuario usuarioLogueado = dao.validarLogin(user, pass);

                if (usuarioLogueado != null) {
                    // Abrimos la ventana Principal pasándole el usuario que acaba de entrar
                    Principal ventanaPrincipal = new Principal(usuarioLogueado);
                    ventanaPrincipal.setVisible(true);
                    
                    dispose(); // Cierra la ventana de login
                } else {
                    // Feedback visual de error
                    JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}