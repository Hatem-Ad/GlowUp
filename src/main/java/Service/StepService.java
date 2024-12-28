package Service;

import Entite.Step;
import Entite.Challenge;
import Utils.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StepService {
    public void addStep(Step step) throws SQLException {
        Connection connection = Test.getInstance().getConnection();
        String query = "INSERT INTO steps (challenge_id, name, description, step_order) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, step.getChallengeId());
        preparedStatement.setString(2, step.getName());
        preparedStatement.setString(3, step.getDescription());
        preparedStatement.setInt(4, step.getStepOrder());
        preparedStatement.executeUpdate();
    }

    public List<Step> getStepsByChallengeId(int challengeId) throws SQLException {
        Connection connection = Test.getInstance().getConnection();
        String query = "SELECT * FROM steps WHERE challenge_id = ? ORDER BY step_order";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, challengeId);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<Step> steps = new ArrayList<>();
        while (resultSet.next()) {
            steps.add(new Step(
                    resultSet.getInt("id"),
                    resultSet.getInt("challenge_id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getInt("step_order")
            ));
        }
        return steps;
    }

    public List<Challenge> searchChallengesByName(String name) throws SQLException {
        Connection connection = Test.getInstance().getConnection();
        List<Challenge> challenges = new ArrayList<>();
        String sql = "SELECT * FROM challenges WHERE name LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Challenge challenge = new Challenge(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("categoryId")
                );
                challenges.add(challenge);
            }
        }
        return challenges;
    }

    public void updateStep(Step step) throws SQLException {
        Connection connection = Test.getInstance().getConnection();
        String sql = "UPDATE steps SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, step.getName());
            statement.setString(2, step.getDescription());
            statement.setInt(3, step.getId());
            statement.executeUpdate();
        }
    }

    public void deleteStep(int stepId) throws SQLException {
        Connection connection = Test.getInstance().getConnection();
        String sql = "DELETE FROM steps WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, stepId);
            statement.executeUpdate();
        }
    }


}
