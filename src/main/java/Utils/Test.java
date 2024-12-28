package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Test {
    private Connection con;
    private static Test data;
    private static final String URL = "jdbc:mysql://localhost:3306/reseau_sante";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    private Test() {
        try {

            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("connexion établie");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Connection getCon() {
        return con;
    }

    public static Test getInstance() {
        if (data == null)
            data = new Test();
        return data;
    }
}