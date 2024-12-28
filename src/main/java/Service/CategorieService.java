package Service;

import Entite.Categorie;
import Utils.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements CategorieServiceInterface<Categorie> {
    private Connection con = Test.getInstance().getCon();
    @Override

    public boolean ajouter(Categorie category) throws SQLException {

        PreparedStatement pre = con.prepareStatement("INSERT INTO categories (name, description) VALUES (?, ?);");
        pre.setString(1, category.getName());
        pre.setString(2, category.getDescription());


        int res = pre.executeUpdate();
        if (res > 0) {
            return true;
        }
        return false;
    }
    @Override

    public boolean deleteCategory(Categorie category) throws SQLException {

        String query = "DELETE FROM categories WHERE id = ?";

        try (Connection connection = Test.getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, category.getId());

            return preparedStatement.executeUpdate() > 0;

        }

    }

    @Override

    public boolean update(Categorie categorie) throws SQLException {

        // Requête SQL pour mettre à jour la catégorie

        String query = "UPDATE categories SET name = ?, description = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {

            // Remplir les paramètres de la requête

            ps.setString(1, categorie.getName());

            ps.setString(2, categorie.getDescription());

            ps.setInt(3, categorie.getId());

            // Exécuter la mise à jour

            int result = ps.executeUpdate();

            // Si le nombre de lignes affectées est supérieur à 0, la mise à jour a réussi

            return result > 0;

        }

    }


    @Override

    public Categorie findById(int id) throws SQLException {

        String query = "SELECT id, name, description FROM categories WHERE id = ?";

        try (Connection connection = Test.getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                return new Categorie(

                        resultSet.getInt("id"),

                        resultSet.getString("name"),

                        resultSet.getString("description")

                );

            }

        }

        return null;

    }

    @Override

    public List<Categorie> getAllCategories() throws SQLException {

        List<Categorie> categories = new ArrayList<>();

        String query = "SELECT id, name, description FROM categories";

        try (Connection connection = Test.getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(query);

             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {

                categories.add(new Categorie(

                        resultSet.getInt("id"),

                        resultSet.getString("name"),

                        resultSet.getString("description")

                ));

            }

        }

        return categories;

    }
    public List<Categorie> searchCategoryByName(String name) throws SQLException {
        List<Categorie> categories = new ArrayList<>();
        String query = "SELECT id, name, description FROM categories WHERE name LIKE ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + name + "%"); // Recherche floue
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    categories.add(new Categorie(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description")
                    ));
                }
            }
        }
        return categories;
    }

}

