package Controllers;

import Entite.Categorie;
import Entite.Evenement;
import Service.CategorieService;
import Service.EvenementService;
import com.docraptor.Doc;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class ClientHomeController {

    @FXML
    private TilePane cardsTilePane;

    @FXML
    private TextField searchField; // Champ de recherche

    private CategorieService categorieService = new CategorieService();
    private EvenementService evenementService = new EvenementService();
    @FXML
    private Hyperlink clientNameLink; // Lien où le nom sera affiché



    @FXML
    private WebView webView; // WebView pour afficher le PDF

    @FXML
    private AnchorPane mainpane;  // ou le type de conteneur que vous utilisez
    @FXML

    private void Deconnecter(ActionEvent event) {
        try {
            // Charger la vue de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent loginView = loader.load();

            // Récupérer la scène à partir du bouton cliqué
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changer la scène
            stage.setScene(new Scene(loginView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @FXML
    private void loadEvents() {
        cardsTilePane.getChildren().clear(); // Vider les cartes actuelles
        try {
            String searchQuery = searchField.getText().trim(); // Récupérer la requête de recherche
            List<Evenement> evenements = searchQuery.isEmpty() ? evenementService.getAllEvenements() : evenementService.searcheventByName(searchQuery);

            if (evenements.isEmpty()) {
                showAlert("La catégorie recherchée n'existe pas."); // Afficher une alerte si aucune catégorie n'est trouvée
            } else {
                for (Evenement evenement : evenements) {
                    cardsTilePane.getChildren().add(createEvenementCard(evenement));
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
     * Crée une carte pour une catégorie.
     */
    private VBox createCategoryCard(Categorie categorie) {
        VBox card = new VBox(17);
        card.setStyle("-fx-background-color: #ecf0f1; "
                + "-fx-padding: 20; "
                + "-fx-border-color: #272142; " // Utiliser la même couleur que le bouton
                + "-fx-border-radius: 10; "
                + "-fx-background-radius: 10; "
                + "-fx-min-width: 500; " // Augmenter la largeur
                + "-fx-min-height: 350; " // Augmenter la hauteur
                + "-fx-max-width: 500; "
                + "-fx-max-height: 350; "
                + "-fx-alignment: CENTER;");

        // ImageView pour l'image de la catégorie
        ImageView imageView = new ImageView(new Image("img/cat.png"));
        imageView.setFitWidth(350);
        imageView.setFitHeight(200);

        // Nom de la catégorie
        Label nameLabel = new Label(categorie.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-text-alignment: center;");

        // Description de la catégorie
        Label descriptionLabel = new Label(categorie.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(350);
        descriptionLabel.setStyle("-fx-font-size: 14; -fx-text-alignment: center;");

        // HBox pour les boutons
        HBox buttonBox = new HBox(10);
        buttonBox.setStyle("-fx-alignment: CENTER;");

        // Bouton de détails
        Button detailsButton = new Button("Détails");
        detailsButton.setStyle(
                "-fx-background-color: #272142; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14; " +
                        "-fx-padding: 5 10; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
        detailsButton.setOnAction(event -> navigateToChallenges(categorie.getName()));

        // Bouton PDF
        Button pdfButton = new Button("Télécharger PDF");
        pdfButton.setStyle(
                "-fx-background-color: #272142; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14; " +
                        "-fx-padding: 5 10; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
        pdfButton.setOnAction(event -> generatePdf(categorie));

        buttonBox.getChildren().addAll(detailsButton, pdfButton);

        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, buttonBox);
        return card;
    }

    private VBox createEvenementCard(Evenement evenement) {
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

        Label nameLabel = new Label(evenement.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-text-alignment: center;");

        Label descriptionLabel = new Label(evenement.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(350);
        descriptionLabel.setStyle("-fx-font-size: 14; -fx-text-alignment: center;");

        Button detailsButton = new Button("Participer");
        detailsButton.setStyle(
                "-fx-background-color: #272142; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14; " +
                        "-fx-padding: 10 20; " +
                        "-fx-border-radius: 20; " +
                        "-fx-background-radius: 20;"
        );

       // detailsButton.setOnAction(event -> navigateToChallenges(evenement.getName()));

        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, detailsButton);
        return card;
    }

    private void showDetails(String message) {
        System.out.println(message); // Afficher le message pour l'instant
    }

    /**
     * Générer un PDF pour une catégorie donnée.
     */
    private void generatePdf(Categorie categorie) {
        try {
            // Convertir l'image en Base64
            String base64Image = convertImageToBase64("img/al.jpg");

            // Créer le contenu HTML pour le PDF
            String htmlContent = "<!DOCTYPE html>" +
                    "<html lang='fr'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<title>Catégorie PDF</title>" +
                    "<style>" +
                    "body {" +
                    "   font-family: Arial, sans-serif;" +
                    "   margin: 0;" +
                    "   padding: 0;" +
                    "   height: 100vh;" +
                    "   display: flex;" +
                    "   justify-content: center;" +
                    "   align-items: center;" +
                    "   background-color: #ffffff;" +
                    "}" +
                    ".page {" +
                    "   border: 5px solid black;" + // Cadre noir gras
                    "   width: 95%;" +
                    "   height: 95%;" +
                    "   box-sizing: border-box;" +
                    "   padding: 50px;" +
                    "   display: flex;" +
                    "   flex-direction: column;" + // Organisation verticale
                    "   align-items: center;" + // Centré horizontalement
                    "   position: relative;" + // Permet de positionner les éléments en haut
                    "}" +
                    ".header {" +
                    "   position: absolute;" +
                    "   top: 50px;" + // Distance depuis le haut
                    "   width: 100%;" + // Prend toute la largeur disponible
                    "   text-align: center;" +
                    "}" +
                    ".category-name {" +
                    "   font-size: 36px;" +
                    "   font-weight: bold;" +
                    "   margin-bottom: 20px;" +
                    "   color: #000000;" +
                    "}" +
                    ".category-description {" +
                    "   font-size: 20px;" +
                    "   color: #333333;" +
                    "   line-height: 1.5;" +
                    "   margin: 0;" +
                    "}" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='page'>" +
                    "   <div class='header'>" +
                    "       <div class='category-name'>" + categorie.getName() + "</div>" + // Nom centré en haut
                    "       <div class='category-description'>" + categorie.getDescription() + "</div>" + // Description juste en dessous
                    "   </div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            // Créer le document PDF
            Doc doc = new Doc();
            doc.setDocumentContent(htmlContent);
            doc.setDocumentType(com.docraptor.Doc.DocumentTypeEnum.PDF); // Correct document type as a string
            doc.setName("categorie_" + categorie.getId() + ".pdf");

            // Encoder la clé API en Base64 pour l'autorisation
            String apiKey = "DW1TEVPBTnQ4dS13WwsD";
            String encodedApiKey = java.util.Base64.getEncoder().encodeToString(apiKey.getBytes());

            // Créer un client HTTP pour l'envoi de la requête
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://docraptor.com/docs"))
                    .header("Authorization", "Basic " + encodedApiKey) // Utilise la clé API encodée
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(Collections.singletonMap("doc", doc))))
                    .build();

            // Exécuter la requête
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                // Sauvegarder le PDF localement
                String pdfFileName = "categorie_" + categorie.getId() + ".pdf";
                try (FileOutputStream fos = new FileOutputStream(pdfFileName)) {
                    fos.write(response.body());
                }

                // Ouvrir automatiquement le PDF généré
                openPdf(pdfFileName);

                showAlert("PDF généré avec succès pour la catégorie : " + categorie.getName());
            } else {
                showAlert("Erreur lors de la génération du PDF : " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    /**
     * Convertir l'image en Base64 pour l'intégrer dans le PDF.
     */
    private String convertImageToBase64(String imagePath) {
        // Code pour convertir l'image en base64, à implémenter si nécessaire
        return "data:image/png;base64,....";  // Placeholder pour la conversion réelle
    }

    /**
     * Ouvrir le fichier PDF généré avec le lecteur PDF par défaut du système.
     */
    private void openPdf(String pdfFileName) {
        try {
            File pdfFile = new File(pdfFileName);
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile); // Utiliser la classe Desktop pour ouvrir le PDF
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture du fichier PDF.");
        }
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

    @FXML
    private void navigateToForum() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forum.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur si vous devez lui passer des données
            ForumController controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Forum");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
