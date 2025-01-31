package Service;

import Entite.User;
import Utils.Test;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService implements user {
    private Connection con = Test.getInstance().getCon();

    @Override
    public User login(String email, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("email"), rs.getString("password"), rs.getString("role"));
            }
        }
        return null;
    }

    @Override
    public boolean register(String email, String password) throws SQLException {
        String checkQuery = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return false; // L'utilisateur existe déjà
            }

            String insertQuery = "INSERT INTO users (email, password, role) VALUES (?, ?, 'client')";
            try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
                insertStmt.setString(1, email);
                insertStmt.setString(2, password); // Utilisez un hash pour plus de sécurité
                insertStmt.executeUpdate();
                return true;
            }
        }
    }

    @Override
    public List<User> getAllUsersWithRoleAdmin() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, email, password FROM users WHERE role = 'admin'";
        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        "admin"
                ));
            }
        }
        return users;
    }

    @Override
    public boolean resetPassword(String emailReset, String newPassword) throws SQLException {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, emailReset);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<String> getEmailsByRoleClient() throws SQLException {
        List<String> emails = new ArrayList<>();
        String query = "SELECT email FROM users WHERE role = 'client'";
        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                emails.add(rs.getString("email"));
            }
        }
        return emails;
    }

    @Override
    public boolean deleteUser(User user) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, email, password, role FROM users";
        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        }
        return users;
    }

    @Override
    public User findById(int id) throws SQLException {
        String query = "SELECT id, email, password, role FROM users WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        }
        return null;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String query = "UPDATE users SET email = ?, password = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getId());
            return ps.executeUpdate() > 0;
        }
    }
}
