package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import model.Cliente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Registro extends JFrame {

    private JTextField txtUsername, txtEmail, txtNombre, txtApellidos, txtDni, txtPeso, txtGrasa;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRol;
    private JPanel panelCliente; // Panel que ocultaremos o mostraremos
    private JButton btnGuardar, btnVolver;

    public Registro() {
        setTitle("Gimnasio - Registro de Usuario");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra esta ventana, no todo el programa
        setLocationRelativeTo(null);
        setLayout(null);

        // --- CAMPOS COMUNES (Para la tabla usuarios) ---
        int y = 20; // Variable para controlar la altura (eje Y)
        
        add(new JLabel("Username:")).setBounds(30, y, 100, 25);
        txtUsername = new JTextField();
        txtUsername.setBounds(140, y, 200, 25);
        add(txtUsername);

        y += 40;
        add(new JLabel("Contraseña:")).setBounds(30, y, 100, 25);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, y, 200, 25);
        add(txtPassword);

        y += 40;
        add(new JLabel("Email:")).setBounds(30, y, 100, 25);
        txtEmail = new JTextField();
        txtEmail.setBounds(140, y, 200, 25);
        add(txtEmail);

        y += 40;
        add(new JLabel("Nombre:")).setBounds(30, y, 100, 25);
        txtNombre = new JTextField();
        txtNombre.setBounds(140, y, 200, 25);
        add(txtNombre);

        y += 40;
        add(new JLabel("Apellidos:")).setBounds(30, y, 100, 25);
        txtApellidos = new JTextField();
        txtApellidos.setBounds(140, y, 200, 25);
        add(txtApellidos);

        y += 40;
        add(new JLabel("DNI:")).setBounds(30, y, 100, 25);
        txtDni = new JTextField();
        txtDni.setBounds(140, y, 200, 25);
        add(txtDni);

        y += 40;
        add(new JLabel("Rol:")).setBounds(30, y, 100, 25);
        String[] roles = {"CLIENTE", "ENTRENADOR"};
        comboRol = new JComboBox<>(roles);
        comboRol.setBounds(140, y, 200, 25);
        add(comboRol);

        // --- PANEL DINÁMICO PARA CLIENTE ---
        y += 40;
        panelCliente = new JPanel();
        panelCliente.setLayout(null);
        panelCliente.setBounds(20, y, 350, 80);
        add(panelCliente);

        panelCliente.add(new JLabel("Peso (kg):")).setBounds(10, 10, 100, 25);
        txtPeso = new JTextField("0.0");
        txtPeso.setBounds(120, 10, 200, 25);
        panelCliente.add(txtPeso);

        panelCliente.add(new JLabel("% Grasa:")).setBounds(10, 50, 100, 25);
        txtGrasa = new JTextField("0.0");
        txtGrasa.setBounds(120, 50, 200, 25);
        panelCliente.add(txtGrasa);

        // --- BOTONES ---
        y += 90;
        btnGuardar = new JButton("Registrar");
        btnGuardar.setBounds(50, y, 130, 30);
        add(btnGuardar);

        btnVolver = new JButton("Volver");
        btnVolver.setBounds(200, y, 130, 30);
        add(btnVolver);

        // --- LÓGICA DINÁMICA DEL COMBOBOX (Lo que pide la rúbrica) ---
        comboRol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rolSeleccionado = (String) comboRol.getSelectedItem();
                if ("CLIENTE".equals(rolSeleccionado)) {
                    panelCliente.setVisible(true); // Mostramos los campos de cliente
                } else {
                    panelCliente.setVisible(false); // Ocultamos si es entrenador (por ahora)
                }
            }
        });

        // --- LÓGICA DEL BOTÓN REGISTRAR ---
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí recogemos los datos y llamamos a la transacción atómica
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setUsername(txtUsername.getText());
                nuevoCliente.setPassword(new String(txtPassword.getPassword()));
                nuevoCliente.setEmail(txtEmail.getText());
                nuevoCliente.setNombre(txtNombre.getText());
                nuevoCliente.setApellidos(txtApellidos.getText());
                nuevoCliente.setDni(txtDni.getText());
                nuevoCliente.setRol((String) comboRol.getSelectedItem());
                
                // Convertimos el texto a decimales
                try {
                    nuevoCliente.setPeso(Double.parseDouble(txtPeso.getText()));
                    nuevoCliente.setPorcentajeGrasa(Double.parseDouble(txtGrasa.getText()));
                    
                    UsuarioDAO dao = new UsuarioDAOImpl();
                    if (dao.registrarCliente(nuevoCliente)) {
                        JOptionPane.showMessageDialog(null, "¡Registro completado con éxito!");
                        dispose(); // Cerramos la ventana de registro
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al registrar. Revisa los datos (DNI o Username duplicado).", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "El peso y la grasa deben ser números (ej. 75.5)", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Lógica del botón volver
        btnVolver.addActionListener(e -> dispose());
    }
}