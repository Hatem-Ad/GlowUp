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
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Vérification des champs vides
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont obligatoires.");
            return;
        }

        // Vérification de l'email valide
        if (!isValidEmail(email)) {
            showAlert("Erreur", "Veuillez entrer une adresse email valide.");
            return;
        }

        // Vérification de la longueur du mot de passe (minimum 7 caractères)
        if (password.length() < 7) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 7 caractères.");
            return;
        }

        // Vérification de la correspondance des mots de passe
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

    // Méthode pour vérifier si l'email est valide
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}
