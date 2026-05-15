import view.Login;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Esto asegura que la interfaz gráfica se ejecute en el hilo correcto (buenas prácticas)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Login ventanaLogin = new Login();
                ventanaLogin.setVisible(true); // Hace visible la ventana
            }
        });
    }
}