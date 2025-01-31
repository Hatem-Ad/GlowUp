package Controllers;

import Entite.Evenement;
import Service.EvenementService;
import Service.EvenementService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

public class AjouterEvenementsController {

    @FXML
    private TextField txtNomEvent;

    @FXML
    private TextField txtDescriptionEvent;
    @FXML
    private AnchorPane mainpane;

    @FXML
    void AjouterEvenement(ActionEvent event) {
        String nomEvent = txtNomEvent.getText();
        String descriptionEvent = txtDescriptionEvent.getText();

        // Assurez-vous que vous avez une classe Categorie et un service pour gérer l'ajout
        Evenement evenement = new Evenement(nomEvent, descriptionEvent);
        EvenementService serviceEvent = new EvenementService();

        try {
            serviceEvent.ajouter(evenement); // Méthode pour ajouter une catégorie dans la base de données
            showAlert("Succès", "Event est ajouté avec success.", Alert.AlertType.INFORMATION);
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
