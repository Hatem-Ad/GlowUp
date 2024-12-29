package Controllers;

import Entite.Categorie;
import Service.CategorieService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

public class AjouterCategorieController {

    @FXML
    private TextField txtNomCategorie;

    @FXML
    private TextField txtDescriptionCategorie;
    @FXML
    private AnchorPane mainpane;

    @FXML
    void AjouterCategorie(ActionEvent event) {
        String nomCategorie = txtNomCategorie.getText();
        String descriptionCategorie = txtDescriptionCategorie.getText();

        // Assurez-vous que vous avez une classe Categorie et un service pour gérer l'ajout
        Categorie categorie = new Categorie(nomCategorie, descriptionCategorie);
        CategorieService serviceCategorie = new CategorieService();

        try {
            serviceCategorie.ajouter(categorie); // Méthode pour ajouter une catégorie dans la base de données
            showAlert("Succès", "Catégorie est ajouté avec success.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
