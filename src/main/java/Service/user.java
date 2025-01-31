package Service;

import Entite.User;
import java.sql.SQLException;
import java.util.List;

public interface user {
    // Authentification et inscription
    User login(String email, String password) throws SQLException;
    boolean register(String email, String password) throws SQLException;

    // Gestion des utilisateurs
    boolean deleteUser(User user) throws SQLException;
    boolean update(User user) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    User findById(int id) throws SQLException;

    // Récupération spécifique
    List<User> getAllUsersWithRoleAdmin() throws SQLException;
    List<String> getEmailsByRoleClient() throws SQLException;

    // Autres
    boolean resetPassword(String emailReset, String newPassword) throws SQLException;
}
