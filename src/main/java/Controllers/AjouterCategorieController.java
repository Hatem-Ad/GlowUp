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
      @FXML
    void envoyerEmail() {
        try {
            // Créer une instance de UserService pour appeler la méthode non statique
            UserService userService = new UserService();

            // Récupérer les emails des utilisateurs ayant le rôle "client"
            List<String> clientEmails = userService.getEmailsByRoleClient();  // Appel sur l'instance

            if (clientEmails.isEmpty()) {
                showAlert("Info", "Aucun client à notifier.", Alert.AlertType.INFORMATION);
                return;
            }

            // Créer une instance d'EmailService pour envoyer l'email
            EmailService emailService = new EmailService();

            // Envoyer un email à tous les clients
            String subject = "Nouvelle Catégorie Ajoutée!";
            String body = "Une nouvelle catégorie a été ajoutée à la plateforme. Venez la découvrir pour étre en bonne santé!";
            emailService.sendEmail(clientEmails, subject, body);  // Appel sur l'instance

            showAlert("Succès", "Email envoyé à tous les clients.", Alert.AlertType.INFORMATION);
        } catch (SQLException | MailjetException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'envoi des emails.", Alert.AlertType.ERROR);
        }
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
