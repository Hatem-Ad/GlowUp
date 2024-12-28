package Service;

import Utils.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipationService {

    public boolean addParticipation(int userId, int challengeId) throws SQLException {
        Connection con = Test.getInstance().getConnection();

        // Vérifier si l'utilisateur participe déjà au challenge
        String checkQuery = "SELECT COUNT(*) FROM participations WHERE user_id = ? AND challenge_id = ?";
        PreparedStatement checkStmt = con.prepareStatement(checkQuery);
        checkStmt.setInt(1, userId);
        checkStmt.setInt(2, challengeId);
        ResultSet checkResult = checkStmt.executeQuery();

        if (checkResult.next() && checkResult.getInt(1) > 0) {
            // L'utilisateur participe déjà à ce challenge
            return false;
        }

        // Ajouter la participation
        String insertQuery = "INSERT INTO participations (user_id, challenge_id, progression) VALUES (?, ?, ?)";
        PreparedStatement insertStmt = con.prepareStatement(insertQuery);
        insertStmt.setInt(1, userId);
        insertStmt.setInt(2, challengeId);
        insertStmt.setInt(3, 0); // Initialiser la progression à 0

        int rowsInserted = insertStmt.executeUpdate();
        return rowsInserted > 0;
    }

    public int getUserParticipationCount(int userId, int categoryId) throws SQLException {
        Connection connection = Test.getInstance().getConnection();
        String query = """
        SELECT COUNT(*) 
        FROM participations p 
        JOIN challenges c ON p.challenge_id = c.id 
        WHERE p.user_id = ? 
        AND c.category_id = ?
    """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, categoryId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                System.out.println("Nombre de participations pour l'utilisateur ID " + userId + " dans la catégorie ID " + categoryId + " : " + count);
                return count;
            }
        }
        return 0;
    }


}


