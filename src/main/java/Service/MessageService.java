package Service;

import Entite.Message;
import Utils.Test;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements MessageServiceInterface<Message> {

    private Connection connection = Test.getInstance().getCon();

    // Constructor to initialize the database connection
    public MessageService(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean ajouter(Message message) throws SQLException {
        String query = "INSERT INTO messages (userName, content) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, message.getUserName());
            pstmt.setString(2, message.getContent());
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Message message) throws SQLException {
        String query = "DELETE FROM messages WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, message.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Message message) throws SQLException {
        String query = "UPDATE messages SET content = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, message.getContent());
            pstmt.setInt(2, message.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public Message findById(int id) throws SQLException {
        String query = "SELECT * FROM messages WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(
                            rs.getInt("id"),
                            rs.getString("userName"),
                            rs.getString("content")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Message> getAllMessages() throws SQLException {
        String query = "SELECT * FROM messages";
        List<Message> messages = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("id"),
                        rs.getString("userName"),
                        rs.getString("content")
                ));
            }
        }
        return messages;
    }

    // 🔍 Récupérer un message par utilisateur et contenu
    public Message getMessageByContent(String userName, String content) throws SQLException {
        String query = "SELECT * FROM messages WHERE userName = ? AND content = ? LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, content);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(
                            rs.getInt("id"),
                            rs.getString("userName"),
                            rs.getString("content")
                    );
                }
            }
        }
        return null;
    }
}
