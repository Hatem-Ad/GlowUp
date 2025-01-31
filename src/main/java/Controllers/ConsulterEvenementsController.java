package Controllers;

import Entite.Evenement;
import Service.EvenementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.SQLException;

public class ConsulterEvenementsController {
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Evenement> evenementsTable;
    @FXML
    private TableColumn<Evenement, String> nameColumn;
    @FXML
    private TableColumn<Evenement, String> descriptionColumn;
    @FXML
    private TableColumn<Evenement, Void> actionsColumn;

    private ObservableList<Evenement> evenementsList = FXCollections.observableArrayList();
    private EvenementService evenementsService = new EvenementService();

    @FXML
    private void initialize() {
        loadEvenements();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Message affiché si la table est vide
        evenementsTable.setPlaceholder(new Label("Aucun événement disponible"));

        actionsColumn.setCellFactory(new Callback<TableColumn<Evenement, Void>, TableCell<Evenement, Void>>() {
            @Override
            public TableCell<Evenement, Void> call(TableColumn<Evenement, Void> param) {
                return new TableCell<Evenement, Void>() {
                    private final Button modifyButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        modifyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; "
                                + "-fx-font-size: 14px; -fx-padding: 5px 10px; "
                                + "-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;");

                        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; "
                                + "-fx-font-size: 14px; -fx-padding: 5px 10px; "
                                + "-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;");

                        modifyButton.setOnAction(event -> update(getTableRow().getItem()));
                        deleteButton.setOnAction(event -> deleteEvent(getTableRow().getItem()));
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10, modifyButton, deleteButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    private void loadEvenements() {
        try {
            evenementsList.setAll(evenementsService.getAllEvenements());
            evenementsTable.refresh(); // Mettre à jour l'affichage du tableau
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les événements : " + e.getMessage(), AlertType.ERROR);
        }
        evenementsTable.setItems(evenementsList);
    }

    private void update(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvenements.fxml"));
            Parent root = loader.load();
            ModifierEvenementsController controller = loader.getController();
            controller.setEvent(evenement);

            Stage stage = new Stage();
            stage.setTitle("Modifier Événement");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadEvenements();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification.", AlertType.ERROR);
        }
    }

    private void deleteEvent(Evenement evenement) {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'événement : " + evenement.getName() + " ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (evenementsService.deleteevent(evenement)) {
                        evenementsList.remove(evenement);
                        showAlert("Succès", "Événement supprimé avec succès.", AlertType.INFORMATION);
                        loadEvenements();
                    } else {
                        showAlert("Erreur", "Erreur lors de la suppression de l'événement.", AlertType.ERROR);
                    }
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void searchEvent() {
        try {
            String eventName = searchField.getText().trim();
            ObservableList<Evenement> results;
            if (eventName.isEmpty()) {
                results = FXCollections.observableArrayList(evenementsService.getAllEvenements());
            } else {
                results = FXCollections.observableArrayList(evenementsService.searcheventByName(eventName));
                if (results.isEmpty()) {
                    showAlert("Information", "Aucun événement trouvé pour : " + eventName, AlertType.INFORMATION);
                }
            }
            evenementsTable.setItems(results);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la recherche : " + e.getMessage(), AlertType.ERROR);
        }
    }
}
