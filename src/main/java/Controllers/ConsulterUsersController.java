package Controllers;

import Entite.User;
import Service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.sql.SQLException;

public class ConsulterUsersController {
    @FXML
    private TextField searchField;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> descriptionColumn;
    @FXML
    private TableColumn<User, Void> actionsColumn;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private UserService userService = new UserService();

    @FXML
    private void initialize() {
        loadUsers();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button modifyButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                modifyButton.setOnAction(event -> update(getTableRow().getItem()));
                deleteButton.setOnAction(event -> deleteUser(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10, modifyButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void loadUsers() {
        try {
            userList.setAll(userService.getAllUsers());
            usersTable.setItems(userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierUser.fxml"));
            Parent root = loader.load();

            ModifierUserController controller = loader.getController();
            controller.setUser(user);

            Stage stage = new Stage();
            stage.setTitle("Modifier utilisateur");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification.", Alert.AlertType.ERROR);
        }
    }

    private void deleteUser(User user) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (userService.deleteUser(user)) {
                        userList.remove(user);
                        showAlert("Succès", "Utilisateur supprimé avec succès.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Erreur", "Impossible de supprimer l'utilisateur.", Alert.AlertType.ERROR);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
