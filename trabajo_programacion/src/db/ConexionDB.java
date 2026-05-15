package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    private static final String URL = "jdbc:mysql://localhost:3307/gimnasio";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if(connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return connection;
    }

    public static void iniciarTransaccion() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public static void confirmarTransaccion() throws SQLException{
        getConnection().commit();
        getConnection().setAutoCommit(true);
    }

    public static void cancelarTransaccion() throws SQLException {
        getConnection().rollback();
        getConnection().setAutoCommit(true);
    }

}
