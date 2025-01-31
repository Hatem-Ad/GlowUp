package Controllers;

import Entite.User;
import Service.ResetEmailService;
import Service.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Service.UserService;
import java.net.URL;
import java.io.IOException;
import java.sql.SQLException;
import java.io.File;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private UserService userService = new UserService();

    private ResetEmailService resetEmailService;

    public LoginController() {
        resetEmailService = new ResetEmailService();
    }

    // Action de connexion
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Vérification des champs vides
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification de l'email valide
        if (!isValidEmail(email)) {
            showAlert("Erreur", "Veuillez entrer un email valide.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification de la longueur du mot de passe
        if (password.length() < 7) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 7 caractères.", Alert.AlertType.ERROR);
            return;
        }

        try {
            User user = userService.login(email, password);
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
                } else if ("client".equals(user.getRole())) {
                    // Rediriger vers l'interface Client
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientHome.fxml"));
                    Parent root = loader.load();
                    ClientHomeController clientController = loader.getController();
                    clientController.setUserInfo(user.getEmail());
                    Stage stage = new Stage();
                    stage.setTitle("Client Home");
                    stage.setScene(new Scene(root));
                    stage.show();
                }

                // Fermer la fenêtre de connexion
                ((Stage) emailField.getScene().getWindow()).close();
            } else {
                showAlert("Erreur", "Identifiants invalides", Alert.AlertType.ERROR);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la connexion.", Alert.AlertType.ERROR);
        }
    }

    // Méthode pour vérifier si l'email est valide
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    // Ouvrir le formulaire d'inscription
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

    // Afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Action de déconnexion
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

    // Fermer l'interface actuelle
    public void CloseFxml(AnchorPane mainpane) {
        Stage stage = (Stage) (mainpane.getScene().getWindow());
        stage.close();
    }

    // Action pour réinitialiser le mot de passe
    @FXML
    private void handleResetPassword(ActionEvent event) {
        // Demander à l'utilisateur d'entrer son email pour recevoir le code de réinitialisation
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Réinitialisation du mot de passe");
        dialog.setHeaderText("Entrez votre email pour recevoir le code de réinitialisation.");
        dialog.setContentText("Email:");

        // Lorsque l'utilisateur soumet son email
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String email = result.get();
            String resetCode = generateResetCode(); // Générez le code de réinitialisation
            try {
                // Envoi de l'email avec le code de réinitialisation
                resetEmailService.sendResetPasswordEmail(email, resetCode);

                // Affichage d'une alerte pour entrer le nouveau mot de passe et le code
                Alert resetAlert = new Alert(Alert.AlertType.INFORMATION);
                resetAlert.setTitle("Réinitialisation du mot de passe");
                resetAlert.setHeaderText("Entrez votre nouveau mot de passe et le code envoyé par email");

                // Création de la fenêtre avec les champs
                VBox vbox = new VBox(10);
                TextField newEmailField = new TextField();
                newEmailField.setPromptText("Email");
                PasswordField newPasswordField = new PasswordField();
                newPasswordField.setPromptText("Nouveau mot de passe");
                TextField resetCodeField = new TextField();
                resetCodeField.setPromptText("Entrez le code de réinitialisation");

                vbox.getChildren().addAll(newEmailField, newPasswordField, resetCodeField);

                resetAlert.getDialogPane().setContent(vbox);

                // Ajouter un bouton pour confirmer la réinitialisation
                ButtonType resetButton = new ButtonType("Réinitialiser", ButtonBar.ButtonData.OK_DONE);
                resetAlert.getButtonTypes().setAll(resetButton, ButtonType.CANCEL);

                // Afficher l'alerte et attendre la confirmation
                Optional<ButtonType> response = resetAlert.showAndWait();

                if (response.isPresent() && response.get() == resetButton) {
                    String enteredEmail = newEmailField.getText();
                    String newPassword = newPasswordField.getText();
                    String enteredCode = resetCodeField.getText();

                    // Vérification de l'email et du code
                    if (enteredEmail.equals(email) && enteredCode.equals(resetCode)) {
                        // Si le code et l'email sont corrects, réinitialiser le mot de passe
                        boolean success = userService.resetPassword(enteredEmail, newPassword);
                        if (success) {
                            showAlert("Succès", "Mot de passe réinitialisé avec succès.", Alert.AlertType.INFORMATION);
                        } else {
                            showAlert("Erreur", "Impossible de réinitialiser le mot de passe. Vérifiez l'email.", Alert.AlertType.ERROR);
                        }
                    } else {
                        // Si l'email ou le code est incorrect, afficher une erreur
                        showAlert("Erreur", "L'email ou le code de réinitialisation est incorrect.", Alert.AlertType.ERROR);
                    }
                }
            } catch (Exception e) {
                showAlert("Erreur", "Impossible d'envoyer l'email de réinitialisation.", Alert.AlertType.ERROR);
            }
        }
    }

    // Cette méthode génère un code aléatoire pour la réinitialisation
    private String generateResetCode() {
        return String.valueOf((int) (Math.random() * 1000000)); // Code aléatoire
    }

    // Affiche une alerte avec un message et un titre


}
