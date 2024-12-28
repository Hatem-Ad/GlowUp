package Controllers;

import Entite.Categorie;
import Service.CategorieService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ClientHome {

    @FXML
    private TilePane cardsTilePane;

    @FXML
    private TextField searchField; // Champ de recherche

    private CategorieService categorieService = new CategorieService();
    @FXML
    private Hyperlink clientNameLink; // Lien où le nom sera affiché

    public void setUserInfo(String email) {
        clientNameLink.setText(email); // Mettre à jour le texte du lien
    }
    @FXML
    public void initialize() {
        // Par défaut, charger les catégories
        loadCategories();
    }

    /**
     * Méthode appelée lorsqu'on clique sur "Catégories" dans le menu.
     */
    @FXML
    private void loadCategories() {
        cardsTilePane.getChildren().clear(); // Vider les cartes actuelles
        try {
            String searchQuery = searchField.getText().trim(); // Récupérer la requête de recherche
            List<Categorie> categories = searchQuery.isEmpty() ? categorieService.getAllCategories() : categorieService.searchCategoryByName(searchQuery);

            if (categories.isEmpty()) {
                showAlert("La catégorie recherchée n'existe pas."); // Afficher une alerte si aucune catégorie n'est trouvée
            } else {
                for (Categorie categorie : categories) {
                    cardsTilePane.getChildren().add(createCategoryCard(categorie));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche une alerte avec un message donné.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Méthode appelée lorsqu'on clique sur "Événements" dans le menu.
     */
    @FXML
    private void loadEvents() {
        cardsTilePane.getChildren().clear(); // Vider les cartes actuelles
        cardsTilePane.getChildren().add(createHelloCard()); // Ajouter un "Hello"
    }

    /**
     * Crée une carte contenant "Hello" pour les événements.
     */
    private VBox createHelloCard() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: #ecf0f1; "
                + "-fx-padding: 20; "
                + "-fx-border-color: #bdc3c7; "
                + "-fx-border-radius: 10; "
                + "-fx-background-radius: 10; "
                + "-fx-min-width: 400; "
                + "-fx-min-height: 300; "
                + "-fx-alignment: CENTER;");

        Label helloLabel = new Label("Bonjour");
        helloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 24; -fx-text-alignment: center;");

        card.getChildren().add(helloLabel);
        return card;
    }

    /**
     * Crée une carte pour une catégorie.
     */
    private VBox createCategoryCard(Categorie categorie) {
        VBox card = new VBox(17);
        card.setStyle("-fx-background-color: #ecf0f1; "
                + "-fx-padding: 20; "
                + "-fx-border-color: #272142; "
                + "-fx-border-radius: 10; "
                + "-fx-background-radius: 10; "
                + "-fx-min-width: 500; "
                + "-fx-min-height: 350; "
                + "-fx-max-width: 500; "
                + "-fx-max-height: 350; "
                + "-fx-alignment: CENTER;");

        ImageView imageView = new ImageView(new Image("img/cat.png"));
        imageView.setFitWidth(350);
        imageView.setFitHeight(200);

        Label nameLabel = new Label(categorie.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-text-alignment: center;");

        Label descriptionLabel = new Label(categorie.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(350);
        descriptionLabel.setStyle("-fx-font-size: 14; -fx-text-alignment: center;");

        Button detailsButton = new Button("Détails");
        detailsButton.setStyle(
                "-fx-background-color: #272142; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14; " +
                        "-fx-padding: 10 20; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20;"
        );

        detailsButton.setOnAction(event -> navigateToChallenges(categorie.getName()));

        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, detailsButton);
        return card;
    }

    private void navigateToChallenges(String categoryName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChallengesList.fxml"));
            Parent root = loader.load();

            // Passer les données au contrôleur cible
            ChallengesListController controller = loader.getController();
            controller.setCategory(categoryName);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Challenges pour " + categoryName);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(String message) {
        System.out.println(message); // Afficher le message pour l'instant
    }
}
