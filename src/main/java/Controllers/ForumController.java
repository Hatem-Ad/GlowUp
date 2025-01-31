package Controllers;

import Entite.Message;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Service.MessageService;
import Utils.Test;
import javafx.scene.input.MouseEvent;
import java.awt.Desktop;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ForumController {

    @FXML
    private ListView<String> discussionList;
    @FXML
    private TextField messageField;
    @FXML
    private TextField userNameField;
    @FXML
    private Button sendButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    private MessageService messageService;
    private Message selectedMessage;

    public void initialize() {
        messageService = new MessageService(Test.getInstance().getCon());
        loadMessages();
        sendButton.setOnAction(event -> sendMessageAndOpenRecaptcha());
        updateButton.setOnAction(event -> modifierMessage());
        deleteButton.setOnAction(event -> supprimerMessage());
        discussionList.setOnMouseClicked(this::selectMessage);
    }

    private void loadMessages() {
        try {
            List<Message> messages = messageService.getAllMessages();
            discussionList.getItems().clear();
            for (Message message : messages) {
                discussionList.getItems().add(message.getUserName() + ": " + message.getContent());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectMessage(MouseEvent event) {
        String selectedItem = discussionList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;
        int separatorIndex = selectedItem.indexOf(": ");
        if (separatorIndex != -1) {
            String userName = selectedItem.substring(0, separatorIndex);
            String content = selectedItem.substring(separatorIndex + 2);
            userNameField.setText(userName);
            messageField.setText(content);
            try {
                selectedMessage = messageService.getMessageByContent(userName, content);
            } catch (SQLException e) {
                e.printStackTrace();
                selectedMessage = null;
            }
        }
    }

    private void sendMessageAndOpenRecaptcha() {
        String userName = userNameField.getText().trim();
        String messageContent = messageField.getText().trim();

        // Vérifier si le nom est vide
        if (userName.isEmpty()) {
            showAlert("Erreur", "Le champ du nom ne peut pas être vide.");
            return;
        }

        // Vérifier si le nom contient uniquement des lettres
        if (!userName.matches("^[a-zA-Z]+$")) {
            showAlert("Erreur", "Le nom ne doit contenir que des lettres.");
            return;
        }

        // Vérifier si le message est vide
        if (messageContent.isEmpty()) {
            showAlert("Erreur", "Le champ du message ne peut pas être vide.");
            return;
        }

        // Si le nom et le message sont valides, afficher reCAPTCHA
        openRecaptchaPage();
    }

    private void openRecaptchaPage() {
        try {
            // Ouvrir la page du reCAPTCHA
            URI uri = new URI("http://localhost:63342/Project/sante/recaptcha.html?_ijt=g8j1q59fslhnos36lg4k6rg6uj&_ij_reload=RELOAD_ON_SAVE");
            Desktop.getDesktop().browse(uri);

            // Attendre quelques secondes pour que l'utilisateur complète le reCAPTCHA
            Thread.sleep(5000);
            sendMessage();
            /*if (isCaptchaVerified()) {
                sendMessage();  // Si le reCAPTCHA est validé, envoyer le message
            } else {
                showAlert("Erreur", "Le reCAPTCHA n'a pas été validé. Message non envoyé.");
            }*/
        } catch (Exception e) {
            sendMessage();
        }
    }

    private void sendMessage() {
        String userName = userNameField.getText().trim();
        String messageContent = messageField.getText().trim();

        try {
            // Créer un nouvel objet Message et l'ajouter à la base de données
            Message newMessage = new Message(userName, messageContent);
            if (messageService.ajouter(newMessage)) {
                loadMessages();  // Charger à nouveau les messages
                messageField.clear();
                userNameField.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isCaptchaVerified() {
        try {
            // Vérifier si le reCAPTCHA a été validé via le backend
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/verify_recaptcha").openConnection();
            connection.setRequestMethod("GET");

            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            return response.equals("success");  // Vérifier la réponse du backend
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void modifierMessage() {
        if (selectedMessage == null) return;
        String newContent = messageField.getText().trim();
        if (newContent.isEmpty()) return;
        try {
            selectedMessage.setContent(newContent);
            messageService.update(selectedMessage);
            loadMessages();
            messageField.clear();
            userNameField.clear();
            selectedMessage = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void supprimerMessage() {
        if (selectedMessage == null) return;
        try {
            messageService.delete(selectedMessage);
            loadMessages();
            messageField.clear();
            userNameField.clear();
            selectedMessage = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

