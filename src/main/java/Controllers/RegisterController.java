package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import Service.UserService;
import javafx.stage.Stage;

public class RegisterController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private UserService userService = new UserService();

    @FXML
    public void handleRegister(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont obligatoires.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        try {
            boolean isRegistered = userService.register(email, password);
            if (isRegistered) {
                showAlert("Succès", "Inscription réussie !");
            } else {
                showAlert("Erreur", "Un utilisateur avec cet email existe déjà.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Un problème est survenu lors de l'inscription.");
        }
    }
    @FXML
    public void openLoginForm(ActionEvent event) {
        try {
            // Charger l'interface de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Connexion");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Problème lors de l'ouverture de la page de connexion !");
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}

