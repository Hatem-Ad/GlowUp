package Controllers;

import Entite.Categorie;
import Service.CategorieService;
import Service.EmailService;
import Service.UserService;
import com.mailjet.client.errors.MailjetException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.sql.SQLException;
import java.util.List;

public class AjouterCategorieController {

    @FXML
    private TextField txtNomCategorie;

    @FXML
    private TextField txtDescriptionCategorie;
    @FXML
    private AnchorPane mainpane;

    @FXML
    void AjouterCategorie(ActionEvent event) {
        if (!isValidInput()) {
            return;
        }

        String nomCategorie = txtNomCategorie.getText();
        String descriptionCategorie = txtDescriptionCategorie.getText();

        Categorie categorie = new Categorie(nomCategorie, descriptionCategorie);
        CategorieService serviceCategorie = new CategorieService();

        try {
            serviceCategorie.ajouter(categorie);
            showAlert("Succès", "Catégorie ajoutée avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de la catégorie.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean isValidInput() {
        String nomCategorie = txtNomCategorie.getText().trim();
        String descriptionCategorie = txtDescriptionCategorie.getText().trim();

        if (nomCategorie.isEmpty() ) {
            showAlert("Erreur", "Le nom de la catégorie ne doit contenir que des lettres et ne pas être vide.", Alert.AlertType.ERROR);
            return false;
        }

        if (descriptionCategorie.isEmpty()) {
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

    @FXML
    void envoyerEmail() {
        try {
            UserService userService = new UserService();
            List<String> clientEmails = userService.getEmailsByRoleClient();

            if (clientEmails.isEmpty()) {
                showAlert("Info", "Aucun client à notifier.", Alert.AlertType.INFORMATION);
                return;
            }

            EmailService emailService = new EmailService();
            String subject = "Nouvelle Catégorie Ajoutée!";
            String body = "Une nouvelle catégorie a été ajoutée à la plateforme. Venez la découvrir pour être en bonne santé!";
            emailService.sendEmail(clientEmails, subject, body);

            showAlert("Succès", "Email envoyé à tous les clients.", Alert.AlertType.INFORMATION);
        } catch (SQLException | MailjetException e) {
            showAlert("Erreur", "Erreur lors de l'envoi des emails.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
