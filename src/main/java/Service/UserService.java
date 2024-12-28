package Service;

import Entite.Categorie;
import Entite.User;
import Utils.Test;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    public User login(String email, String password) throws SQLException {
        Connection conn = Test.getConnection();
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return new User(rs.getInt("id"), rs.getString("email"), rs.getString("password"), rs.getString("role"));
        }
        return null;
    }

    public boolean register(String email, String password) throws SQLException {
        Connection conn = Test.getConnection();
        String checkQuery = "SELECT * FROM users WHERE email = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setString(1, email);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            return false; // L'utilisateur existe déjà
        }

        String insertQuery = "INSERT INTO users (email, password, role) VALUES (?, ?, 'client')";
        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
        insertStmt.setString(1, email);
        insertStmt.setString(2, password); // Utilisez un hash pour plus de sécurité
        insertStmt.executeUpdate();
        return true;
    }
    public List<User> getAllUsersWithRoleAdmin() throws SQLException {

        List<User> users = new ArrayList<>();

        String query = "SELECT id, email, password FROM users WHERE role = 'admin'";

        try (Connection connection = Test.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {

                users.add(new User(
                        resultSet.getInt("id"),

                        resultSet.getString("email"),

                        resultSet.getString("password")

                ));

            }

        }

        return users;
    }


    public boolean resetPassword(String emailReset, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = Test.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, emailReset);

            int rowsUpdated = stmt.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




}