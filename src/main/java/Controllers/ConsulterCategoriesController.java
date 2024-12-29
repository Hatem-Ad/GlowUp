package Controllers;

import Entite.Categorie;
import Service.CategorieService;
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

public class ConsulterCategoriesController {
    @FXML
    private TextField searchField; // Champ pour saisir le nom à rechercher
    @FXML
    private TableView<Categorie> categoriesTable;
    @FXML
    private TableColumn<Categorie, String> nameColumn;
    @FXML
    private TableColumn<Categorie, String> descriptionColumn;
    @FXML
    private TableColumn<Categorie, Void> actionsColumn;
    @FXML
    private ObservableList<Categorie> categoriesList = FXCollections.observableArrayList();
    private CategorieService categorieService = new CategorieService();

    @FXML
    private void initialize() {
        loadCategories();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Ajouter des boutons d'action dans chaque ligne
        actionsColumn.setCellFactory(new Callback<TableColumn<Categorie, Void>, TableCell<Categorie, Void>>() {
            @Override
            public TableCell<Categorie, Void> call(TableColumn<Categorie, Void> param) {
                return new TableCell<Categorie, Void>() {
                    private final Button modifyButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");

                    // Initialiser les styles des boutons dans le constructeur de la classe anonyme
                    {
                        // Style pour le bouton Modifier
                        modifyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; "
                                + "-fx-font-size: 14px; -fx-padding: 5px 10px; "
                                + "-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;");

                        // Style pour le bouton Supprimer
                        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; "
                                + "-fx-font-size: 14px; -fx-padding: 5px 10px; "
                                + "-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;");

                        // Actions pour les boutons
                        modifyButton.setOnAction(event -> update(getTableRow().getItem()));
                        deleteButton.setOnAction(event -> deleteCategory(getTableRow().getItem()));
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

    private void loadCategories() {
        try {
            categoriesList.setAll(categorieService.getAllCategories());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        categoriesTable.setItems(categoriesList);
    }

    private void update(Categorie category) {
        try {
            // Charger le fichier FXML de la fenêtre de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCategorie.fxml"));
            Parent root = loader.load();

            // Passer la catégorie au contrôleur de la fenêtre de modification
            ModifierCategorieController controller = loader.getController();
            controller.setCategorie(category);

            // Créer une nouvelle fenêtre modale pour la modification
            Stage stage = new Stage();
            stage.setTitle("Modifier Catégorie");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Fenêtre modale
            stage.showAndWait(); // Attendre la fermeture de la fenêtre avant de continuer

            // Recharger les catégories après modification
            loadCategories();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification.", Alert.AlertType.ERROR);
        }
    }

    private void deleteCategory(Categorie category) {
        // Confirmation avant suppression
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la catégorie : " + category.getName() + " ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = false;
                try {
                    success = categorieService.deleteCategory(category);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (success) {
                    categoriesList.remove(category);
                    showAlert("Succès", "Catégorie supprimée avec succès.", AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Erreur lors de la suppression de la catégorie.", AlertType.ERROR);
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
    private void searchCategory() {
        try {
            // Récupérer le texte du champ de recherche
            String categoryName = searchField.getText().trim();

            ObservableList<Categorie> results;

            if (categoryName.isEmpty()) {
                // Si le champ est vide, charger toutes les catégories
                results = FXCollections.observableArrayList(categorieService.getAllCategories());
            } else {
                // Sinon, rechercher par nom
                results = FXCollections.observableArrayList(categorieService.searchCategoryByName(categoryName));

                // Vérifier si des résultats sont trouvés
                if (results.isEmpty()) {
                    showAlert("Information", "Aucune catégorie trouvée pour le nom : " + categoryName, AlertType.INFORMATION);
                }
            }

            // Mettre à jour la table avec les résultats
            categoriesTable.setItems(results);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la recherche : " + e.getMessage(), AlertType.ERROR);
        }
    }

}
