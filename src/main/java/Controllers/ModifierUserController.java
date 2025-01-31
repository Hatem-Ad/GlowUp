package Controllers;

import Entite.User;
import Service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class ModifierUserController {

    @FXML
    private TextField txtIduser;
    @FXML
    private TextField txtemailuser;
    @FXML
    private PasswordField txtPasswordUser;

    private User user;

    // Expression régulière pour valider l'email
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    // Méthode pour initialiser les champs avec les données de l'utilisateur à modifier
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            txtIduser.setText(String.valueOf(user.getId()));
            txtemailuser.setText(user.getEmail());
            txtPasswordUser.setText(user.getPassword());
        }
    }

    @FXML
    void Modifieruser(ActionEvent event) {
        try {
            // Vérifier si les champs sont vides
            if (txtIduser.getText().isEmpty() || txtemailuser.getText().isEmpty() || txtPasswordUser.getText().isEmpty()) {
                showAlert("Erreur", "Tous les champs sont obligatoires.", Alert.AlertType.ERROR);
                return;
            }

            // Vérifier que l'ID est un entier valide
            int id;
            try {
                id = Integer.parseInt(txtIduser.getText());
            } catch (NumberFormatException e) {
                showAlert("Erreur", "ID utilisateur invalide.", Alert.AlertType.ERROR);
                return;
            }

            // Vérifier la validité de l'email
            String email = txtemailuser.getText();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                showAlert("Erreur", "Email invalide.", Alert.AlertType.ERROR);
                return;
            }

            // Vérifier la force du mot de passe
            String password = txtPasswordUser.getText();
            if (password.length() < 6) {
                showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères.", Alert.AlertType.ERROR);
                return;
            }

            // Créer un objet User avec les nouvelles données
            User updatedUser = new User(id, email, password);

            // Appeler la méthode de mise à jour du service
            UserService userService = new UserService();
            boolean success = userService.update(updatedUser);

            // Afficher un message de succès ou d'échec
            if (success) {
                showAlert("Succès", "Utilisateur modifié avec succès.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Erreur lors de la modification de l'utilisateur.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur est survenue : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
