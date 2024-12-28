package Controllers;

import Entite.Categorie;
import Service.CategorieService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.sql.SQLException;

public class ModifierCategorieController {

    @FXML
    private TextField txtIdCategorie;
    @FXML
    private TextField txtNomCategorie;
    @FXML
    private TextField txtDescriptionCategorie;

    private Categorie categorie;

    // Méthode pour initialiser les champs avec les données de la catégorie à modifier
    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
        // Pré-remplir les champs de la fenêtre modale avec les données existantes
        txtIdCategorie.setText(String.valueOf(categorie.getId()));
        txtNomCategorie.setText(categorie.getName());
        txtDescriptionCategorie.setText(categorie.getDescription());
    }

    @FXML
    void ModifierCategorie(ActionEvent event) {
        try {
            // Récupérer les données saisies dans les champs
            int idCategorie = Integer.parseInt(txtIdCategorie.getText());
            String nomCategorie = txtNomCategorie.getText();
            String descriptionCategorie = txtDescriptionCategorie.getText();

            // Créer un objet Categorie avec les nouvelles données
            Categorie updatedCategorie = new Categorie(idCategorie, nomCategorie, descriptionCategorie);

            // Appeler la méthode de mise à jour du service
            CategorieService serviceCategorie = new CategorieService();
            boolean success = serviceCategorie.update(updatedCategorie);

            // Afficher un message de succès ou d'erreur
            if (success) {
                System.out.println("Catégorie modifiée avec succès !");
                showAlert("Succès", "Catégorie modifié avec succès.", Alert.AlertType.INFORMATION);
            } else {
                System.out.println("Erreur lors de la modification de la catégorie.");
                showAlert("Erreur", "Erreur lors de la modification de la catégorie.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            System.out.println("Veuillez entrer un ID valide.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de la catégorie : " + e.getMessage());
        }
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}