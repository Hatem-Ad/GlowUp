package Controllers;

import Entite.Step;
import Service.StepService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UpdateStepController {

    @FXML
    private TextField txtStepName;
    @FXML
    private TextField txtStepDescription;
    @FXML
    private TextField txtStepOrder;

    private Step selectedStep;
    private StepService stepService = new StepService();

    // Root element pour appliquer le style CSS
    @FXML
    private VBox root;

    /*
    @FXML
    private void initialize() {
        // Initialize or perform any setup tasks before applying styles
        // Check if root is still null (it should not be if FXML is loaded correctly)
        if (root != null) {
            applyStyle();
        } else {
            System.out.println("Root is null, something went wrong with the FXML binding.");
        }
    }

    private void applyStyle() {
        if (root != null) {
            Scene scene = root.getScene();
            if (scene != null) {
                // Appliquer le fichier CSS
                scene.getStylesheets().add(getClass().getResource("/UpdateStep.css").toExternalForm());
            }
        } else {
            System.out.println("Le root est nul, impossible d'appliquer le style.");
        }
    }
*/


    // Initialisation des données de l'étape
    public void initData(Step step) {
        this.selectedStep = step;

        // Remplir les champs de texte avec les informations de l'étape
        txtStepName.setText(step.getName());
        txtStepDescription.setText(step.getDescription());
        txtStepOrder.setText(String.valueOf(step.getStepOrder()));
    }

    // Mise à jour de l'étape dans la base de données
    @FXML
    private void handleUpdateStep() {
        try {
            // Mettre à jour les informations de l'étape
            selectedStep.setName(txtStepName.getText());
            selectedStep.setDescription(txtStepDescription.getText());
            selectedStep.setStepOrder(Integer.parseInt(txtStepOrder.getText()));

            // Appel au service pour mettre à jour l'étape dans la base de données
            stepService.updateStep(selectedStep);

            // Affichage du message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Étape mise à jour avec succès !");

            // Fermer la fenêtre actuelle
            closeWindow();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour l'étape.");
        }
    }

    // Annuler la mise à jour et fermer la fenêtre
    @FXML
    private void handleCancel() {
        closeWindow();
    }

    // Fermer la fenêtre actuelle
    private void closeWindow() {
        Stage stage = (Stage) txtStepName.getScene().getWindow();
        stage.close();
    }

    // Afficher un message d'alerte
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
