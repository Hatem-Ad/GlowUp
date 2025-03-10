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

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
        txtIdCategorie.setText(String.valueOf(categorie.getId()));
        txtNomCategorie.setText(categorie.getName());
        txtDescriptionCategorie.setText(categorie.getDescription());
    }

    @FXML
    void ModifierCategorie(ActionEvent event) {
        if (!isValidInput()) {
            return; // Arrête l'exécution si la saisie est invalide
        }

        try {
            int idCategorie = Integer.parseInt(txtIdCategorie.getText());
            String nomCategorie = txtNomCategorie.getText();
            String descriptionCategorie = txtDescriptionCategorie.getText();

            Categorie updatedCategorie = new Categorie(idCategorie, nomCategorie, descriptionCategorie);
            CategorieService serviceCategorie = new CategorieService();
            boolean success = serviceCategorie.update(updatedCategorie);

            if (success) {
                showAlert("Succès", "Catégorie modifiée avec succès.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Erreur lors de la modification de la catégorie.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur SQL : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean isValidInput() {
        String nomCategorie = txtNomCategorie.getText().trim();
        String descriptionCategorie = txtDescriptionCategorie.getText().trim();

        try {
            Integer.parseInt(txtIdCategorie.getText());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID doit être un nombre valide.", Alert.AlertType.ERROR);
            return false;
        }

        if (nomCategorie.isEmpty()) {
            showAlert("Erreur", "Le nom de la catégorie ne doit contenir que des lettres.", Alert.AlertType.ERROR);
            return false;
        }

        if (descriptionCategorie.isEmpty() ) {
            showAlert("Erreur", "La description doit contenir entre 1 et 255 caractères.", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
