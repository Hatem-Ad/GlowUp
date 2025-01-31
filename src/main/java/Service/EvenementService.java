package Service;

import Entite.Evenement;
import Utils.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvenementService implements EventServiceInterface<Evenement> {
    private Connection con = Test.getInstance().getCon();
    @Override

    public boolean ajouter(Evenement event) throws SQLException {

        PreparedStatement pre = con.prepareStatement("INSERT INTO evenement (nom, description) VALUES (?, ?);");
        pre.setString(1, event.getName());
        pre.setString(2, event.getDescription());


        int res = pre.executeUpdate();
        if (res > 0) {
            return true;
        }
        return false;
    }


    public boolean deleteevent(Evenement event) throws SQLException {

        String query = "DELETE FROM evenement WHERE id = ?";

        try (Connection connection = Test.getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, event.getId());

            return preparedStatement.executeUpdate() > 0;

        }

    }

    @Override

    public boolean update(Evenement Evenement) throws SQLException {

        // Requête SQL pour mettre à jour la catégorie

        String query = "UPDATE evenement SET nom = ?, description = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {

            // Remplir les paramètres de la requête

            ps.setString(1, Evenement.getName());

            ps.setString(2, Evenement.getDescription());

            ps.setInt(3, Evenement.getId());

            // Exécuter la mise à jour

            int result = ps.executeUpdate();

            // Si le nombre de lignes affectées est supérieur à 0, la mise à jour a réussi

            return result > 0;

        }

    }


    @Override

    public Evenement findById(int id) throws SQLException {

        String query = "SELECT id, nom, description FROM evenement WHERE id = ?";

        try (Connection connection = Test.getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                return new Evenement(

                        resultSet.getInt("id"),

                        resultSet.getString("nom"),

                        resultSet.getString("description")

                );

            }

        }

        return null;

    }



    public List<Evenement> getAllEvenements() throws SQLException {

        List<Evenement> Evenements = new ArrayList<>();

        String query = "SELECT id, nom, description FROM evenement";

        try (Connection connection = Test.getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(query);

             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {

                Evenements.add(new Evenement(

                        resultSet.getInt("id"),

                        resultSet.getString("nom"),

                        resultSet.getString("description")

                ));

            }

        }

        return Evenements;

    }
    public List<Evenement> searcheventByName(String nom) throws SQLException {
        List<Evenement> Evenements = new ArrayList<>();
        String query = "SELECT id, nom, description FROM evenement WHERE nom LIKE ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + nom + "%"); // Recherche floue
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Evenements.add(new Evenement(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("description")
                    ));
                }
            }
        }
        return Evenements;
    }

}

