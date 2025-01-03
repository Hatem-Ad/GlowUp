package Controllers;

import Entite.Challenge;
import Service.ChallengeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import Service.SessionManager;
import Service.ParticipationService;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import java.util.List;

public class ChallengesListController {

    @FXML
    private Label categoryNameLabel;

    @FXML
    private TilePane challengesTilePane;

    @FXML
    private Label statisticsLabel;

    @FXML
    private LineChart<String, Number> statsChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private final ChallengeService challengeService = new ChallengeService();
    private final ParticipationService participationService = new ParticipationService();

    /**
     * Définit la catégorie et charge les challenges associés.
     */
    public void setCategory(String categoryName) {
        categoryNameLabel.setText("Challenges pour la catégorie : " + categoryName);
        loadChallenges(categoryName);
        showStatistics(SessionManager.getInstance().getUserId(), categoryName);
    }

    /**
     * Charge les challenges pour une catégorie donnée et les affiche sous forme de cartes.
     */
    private void loadChallenges(String categoryName) {
        challengesTilePane.getChildren().clear(); // Vider les challenges actuels
        try {
            ObservableList<Challenge> challenges = FXCollections.observableArrayList(
                    challengeService.getChallengesByCategory(categoryName)
            );

            if (challenges.isEmpty()) {
                Label noChallengesLabel = new Label("Aucun challenge disponible pour cette catégorie.");
                noChallengesLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #b71c1c; -fx-font-weight: bold;");
                challengesTilePane.getChildren().add(noChallengesLabel);
            } else {
                for (Challenge challenge : challenges) {
                    challengesTilePane.getChildren().add(createChallengeCard(challenge));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche les statistiques d'utilisateur.
     */

    private void showStatistics(int userId, String categoryName) {
        try {
            // Récupération de l'ID de la catégorie
            int categoryId = challengeService.getCategoryIdByName(categoryName);
            if (categoryId == -1) {
                statisticsLabel.setText("Catégorie introuvable : " + categoryName);
                return;
            }

            // Calcul des statistiques
            int totalChallenges = challengeService.countChallengesByCategory(categoryId);
            int userParticipations = participationService.getUserParticipationCount(userId, categoryId);

            // Mise à jour de l'affichage
            String message = "Vous avez participé à " + userParticipations + "/" + totalChallenges + " challenges dans cette catégorie.";
            System.out.println("Statistiques générées : " + message);
            statisticsLabel.setText(message);
        } catch (SQLException e) {
            e.printStackTrace();
            statisticsLabel.setText("Erreur lors du chargement des statistiques.");
        }
    }

    private void loadStatistics(int userId) {
        try {
            // Liste des catégories disponibles
            List<String> categories = challengeService.getChallengeCategoriesByUserId(userId);

            // Séries pour les données du graphique
            XYChart.Series<String, Number> totalChallengesSeries = new XYChart.Series<>();
            totalChallengesSeries.setName("Challenges disponibles");

            XYChart.Series<String, Number> participationsSeries = new XYChart.Series<>();
            participationsSeries.setName("Challenges participés");

            for (String categoryName : categories) {
                int categoryId = challengeService.getCategoryIdByName(categoryName);

                // Nombre total de challenges par catégorie
                int totalChallenges = challengeService.countChallengesByCategory(categoryId);

                // Nombre de participations par catégorie pour l'utilisateur
                int userParticipations = participationService.getUserParticipationCount(userId, categoryId);

                // Ajout des données aux séries
                totalChallengesSeries.getData().add(new XYChart.Data<>(categoryName, totalChallenges));
                participationsSeries.getData().add(new XYChart.Data<>(categoryName, userParticipations));
            }

            // Nettoyer le graphique avant de recharger
            statsChart.getData().clear();

            // Ajouter les séries au graphique
            statsChart.getData().addAll(totalChallengesSeries, participationsSeries);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les statistiques.");
        }
    }



    /**
     * Crée une carte stylisée pour un challenge.
     */
    private VBox createChallengeCard(Challenge challenge) {
        VBox card = new VBox(10); // Espacement entre les éléments de la carte
        card.getStyleClass().add("card"); // Appliquer le style CSS

        // Image par défaut
        ImageView imageView = new ImageView(new Image("img/challenge.jpg"));
        imageView.setFitWidth(200);
        imageView.setFitHeight(100);

        // Nom du challenge
        Label nameLabel = new Label(challenge.getName());
        nameLabel.getStyleClass().add("card-title");

        // Description du challenge
        Label descriptionLabel = new Label(challenge.getDescription());
        descriptionLabel.getStyleClass().add("card-description");
        descriptionLabel.setWrapText(true);

        // Bouton de participation
        Button participateButton = new Button("Participer");
        participateButton.getStyleClass().add("participate-button");
        participateButton.setOnAction(event -> handleParticipation(challenge));

        // Ajouter les éléments à la carte
        card.getChildren().addAll(imageView, nameLabel, descriptionLabel, participateButton);
        card.setPrefWidth(250); // Fixer une largeur pour uniformiser les cartes
        card.setStyle("-fx-alignment: center; -fx-padding: 10;");
        return card;
    }

    @FXML
    public void initialize() {
        int userId = SessionManager.getInstance().getUserId(); // Récupérer l'utilisateur connecté
        loadStatistics(userId); // Charger les statistiques
    }

/*
    private void handleParticipation(Challenge challenge) {
        try {
            int userId = SessionManager.getInstance().getUserId();
            boolean success = challengeService.addParticipation(userId, challenge.getId());

            if (success) {
                // Afficher l'alerte de succès
                showAlert("Participation réussie", "Vous participez maintenant au challenge : " + challenge.getName());

                // Charger l'écran des étapes après la participation
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParticipationChallenge.fxml"));
                Parent root = loader.load();

                // Passer le challenge et l'utilisateur au contrôleur de l'écran des étapes
                ParticipationChallengeController etapesController = loader.getController();
                etapesController.setChallenge(challenge);
                etapesController.setUserId(userId);

                // Changer la scène pour l'écran des étapes
                AnchorPane pane = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(pane));
                stage.setTitle("Liste des étapes");
                stage.show();


            } else {
                showAlert("Échec", "Vous êtes déjà inscrit à ce challenge.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'inscription au challenge.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du chargement de l'écran des étapes.");
        }
    }*/

    private void handleParticipation(Challenge challenge) {
        try {
            // Vérifier si l'utilisateur est connecté
            int userId = SessionManager.getInstance().getUserId();

            // Vérifier si l'utilisateur a déjà participé au challenge et a terminé (progression à 100%)
            boolean alreadyParticipated = challengeService.hasUserParticipatedAndCompleted(userId, challenge.getId());

            if (alreadyParticipated) {
                // Si l'utilisateur a déjà terminé le challenge, afficher une alerte
                showAlert("Participation déjà terminée", "Vous avez déjà terminé ce challenge et ne pouvez pas y participer à nouveau.");
                return; // Sortir de la méthode si l'utilisateur ne peut pas participer
            }

            // Vérifier si l'utilisateur est déjà inscrit à ce challenge sans l'avoir terminé
            boolean alreadyRegistered = challengeService.hasUserAlreadyParticipated(userId, challenge.getId());

            if (!alreadyRegistered) {
                // Si l'utilisateur n'est pas encore inscrit, ajouter la participation
                boolean success = challengeService.addParticipation(userId, challenge.getId());

                if (success) {
                    // Afficher une alerte de succès
                    showAlert("Participation réussie", "Vous participez maintenant au challenge : " + challenge.getName());
                } else {
                    showAlert("Échec", "Une erreur est survenue lors de l'inscription au challenge.");
                    return;
                }
            } else {
                // Si l'utilisateur est déjà inscrit, mais pas terminé, afficher un message approprié
                showAlert("Participation en cours", "Vous avez déjà commencé ce challenge. Vous pouvez le recommencé.");
            }

            // Charger l'écran des étapes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParticipationChallenge.fxml"));
            Parent root = loader.load();

            // Passer les données nécessaires au contrôleur
            ParticipationChallengeController etapesController = loader.getController();
            etapesController.setUserId(userId);
            etapesController.setChallenge(challenge);

            // Obtenir la scène actuelle et afficher la nouvelle scène
            Stage currentStage = (Stage) challengesTilePane.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Liste des étapes du challenge");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'inscription au challenge.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du chargement de l'écran des étapes.");
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}


