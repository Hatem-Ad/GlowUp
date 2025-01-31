package Controllers;

import Entite.Categorie;
import Entite.Evenement;
import Service.EvenementService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.sql.SQLException;

public class ModifierEvenementsController {

    @FXML
    private TextField txtIdEvent;
    @FXML
    private TextField txtNomEvent;
    @FXML
    private TextField txtDescriptionEvent;

    private Evenement evenement;

    // Méthode pour initialiser les champs avec les données de la catégorie à modifier
    public void setEvent(Evenement evenement) {
        this.evenement = evenement;
        // Pré-remplir les champs de la fenêtre modale avec les données existantes
        txtIdEvent.setText(String.valueOf(evenement.getId()));
        txtNomEvent.setText(evenement.getName());
        txtDescriptionEvent.setText(evenement.getDescription());
    }

    @FXML
    void ModifierEvent(ActionEvent event) {
        try {
            // Récupérer les données saisies dans les champs
            int idEvent = Integer.parseInt(txtIdEvent.getText());
            String nomEvent = txtNomEvent.getText();
            String descriptionEvent = txtDescriptionEvent.getText();

            // Créer un objet Event avec les nouvelles données
            Evenement updatedEvent = new Evenement(idEvent, nomEvent, descriptionEvent);

            // Appeler la méthode de mise à jour du service
            EvenementService serviceEvent = new EvenementService();
            boolean success = serviceEvent.update(updatedEvent);

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