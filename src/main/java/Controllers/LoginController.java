package Controllers;

import Entite.User;
import Service.UserService;
import Service.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private MediaView mediaView;

    private MediaPlayer mediaPlayer;
    private UserService userService = new UserService();

    public static String loggedInUserEmail;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            User user = userService.login(email, password); // Vérifie les informations de connexion
            if (user != null) {
                // Stocker l'ID et l'email de l'utilisateur dans le SessionManager
                SessionManager.getInstance().setUserId(user.getId());
                SessionManager.getInstance().setUserEmail(user.getEmail());

                if ("admin".equals(user.getRole())) {
                    // Rediriger vers l'interface Admin
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminHome.fxml"));
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    stage.setTitle("Admin Home");
                    stage.setScene(new Scene(root));
                    stage.show();
                    ((Stage) emailField.getScene().getWindow()).close();
                } else if ("client".equals(user.getRole())) {
                    // Rediriger vers l'interface Client avec ID et nom
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientHome.fxml"));
                    Parent root = loader.load();
                    ClientHome clientController = loader.getController();
                    clientController.setUserInfo(user.getEmail());

                    Stage stage = new Stage();
                    stage.setTitle("Client Home");
                    stage.setScene(new Scene(root));
                    stage.show();
                    ((Stage) emailField.getScene().getWindow()).close();
                }
            } else {
                showAlert("Erreur", "Identifiants invalides", Alert.AlertType.ERROR);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la connexion.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void openRegistrationForm(ActionEvent event) {
        try {
            // Charger l'interface de registre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Inscription");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Problème lors de l'ouverture du formulaire d'inscription !", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void LogOut(AnchorPane mainpane) {
        try {
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) mainpane.getScene().getWindow();
            currentStage.close();

            // Charger la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de connexion.", Alert.AlertType.ERROR);
        }
    }

    public void CloseFxml(AnchorPane mainpane) {
        Stage stage = (Stage) (mainpane.getScene().getWindow());
        stage.close();
    }

    @FXML
    private void handleResetPassword(ActionEvent event) {
        // Créer une nouvelle fenêtre pour réinitialiser le mot de passe
        Stage resetStage = new Stage();
        resetStage.setTitle("Réinitialiser le mot de passe");

        // Champs pour email et nouveau mot de passe
        TextField emailField = new TextField();
        emailField.setPromptText("Entrez votre email");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Entrez un nouveau mot de passe");

        // Boutons de confirmation et d'annulation
        javafx.scene.control.Button confirmButton = new javafx.scene.control.Button("Confirmer");
        javafx.scene.control.Button cancelButton = new javafx.scene.control.Button("Annuler");

        // Layout pour afficher les champs et boutons
        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.getChildren().addAll(emailField, newPasswordField, confirmButton, cancelButton);
        vbox.setPadding(new javafx.geometry.Insets(10));

        // Actions des boutons
        confirmButton.setOnAction(e -> {
            String email = emailField.getText();
            String newPassword = newPasswordField.getText();

            if (email.isEmpty() || newPassword.isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis.", Alert.AlertType.ERROR);
                return;
            }

            // Appel au service pour réinitialiser le mot de passe
            boolean success = userService.resetPassword(email, newPassword);
            if (success) {
                showAlert("Succès", "Mot de passe réinitialisé avec succès.", Alert.AlertType.INFORMATION);
                resetStage.close();
            } else {
                showAlert("Erreur", "Impossible de réinitialiser le mot de passe. Vérifiez l'email.", Alert.AlertType.ERROR);
            }
        });

        cancelButton.setOnAction(e -> resetStage.close());

        // Configuration de la scène et affichage de la fenêtre
        Scene scene = new Scene(vbox);
        resetStage.setScene(scene);
        resetStage.showAndWait();
    }
}

