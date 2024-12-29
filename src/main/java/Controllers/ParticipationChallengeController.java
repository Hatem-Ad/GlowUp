package Controllers;

import Entite.Challenge;
import Entite.Participation;
import Service.ParticipationService;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ParticipationChallengeController {

    @FXML
    private VBox etapesContainer; // Conteneur pour les étapes

    @FXML
    private ProgressBar progressionBar; // Barre de progression

    @FXML
    private Button terminerButton; // Bouton Terminer Challenge

    @FXML
    private ImageView progressIcon;

    private Challenge currentChallenge; // Challenge actuel
    private Participation currentParticipation; // Participation de l'utilisateur
    private ParticipationService participationService = new ParticipationService(); // Service
    private int userId; // ID de l'utilisateur

    @FXML
    public void initialize() {
        // Initialisation des composants si nécessaire
        System.out.println("Contrôleur initialisé.");
        terminerButton.setVisible(false); // Assurez-vous que le bouton est invisible au départ

        // Charger le style CSS (facultatif si défini dans le FXML)
        //terminerButton.getScene().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        // Charger une icône pour la progression
        progressIcon.setImage(new Image(getClass().getResourceAsStream("/img/progress.png")));

        // Ajouter une icône au bouton "Terminer Challenge"
        ImageView icon = new ImageView(new Image(getClass().getResource("/img/trophy-icon.png").toExternalForm()));

        icon.setFitWidth(20);
        icon.setFitHeight(20);
        terminerButton.setGraphic(icon);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setChallenge(Challenge challenge) {
        this.currentChallenge = challenge;
        this.currentParticipation = new Participation();
        this.currentParticipation.setUserId(userId);
        this.currentParticipation.setChallengeId(currentChallenge.getId());
        loadEtapes();
    }

    private void loadEtapes() {
        if (currentChallenge != null) {
            try {
                // Récupération des étapes depuis le service
                List<String> etapes = participationService.getEtapes(currentChallenge);

                // Ajout des étapes dynamiquement au conteneur
                etapesContainer.getChildren().clear();
                for (String etape : etapes) {
                    CheckBox checkBox = new CheckBox(etape);
                    checkBox.setOnAction(event -> {
                        updateProgression();
                        animateCheckBox(checkBox);
                    });

                    etapesContainer.getChildren().add(checkBox);
                }

                // Mise à jour initiale de la progression
                updateProgression();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur est survenue lors du chargement des étapes.");
            }
        } else {
            showAlert("Erreur", "Le challenge n'est pas initialisé.");
        }
    }

    private void updateProgression() {
        long completedSteps = etapesContainer.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .filter(CheckBox::isSelected)
                .count();

        double progress = (double) completedSteps / etapesContainer.getChildren().size();
        animateProgress(progress);
        progressionBar.setProgress(progress);

        currentParticipation.setProgression((int) (progress * 100)); // Convertir en pourcentage

        try {
            participationService.updateProgression(currentParticipation);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la mise à jour de la progression.");
        }

        // Gestion du bouton Terminer Challenge
        terminerButton.setVisible(completedSteps == etapesContainer.getChildren().size());
    }
    // Ajoutez cette méthode dans votre classe ParticipationChallengeController
    private void generatePDF() {
        try {
            // Spécifier où enregistrer le fichier PDF
            String filePath = "C:\\Users\\Lenovo\\Desktop\\3_année_cycle_ing\\java\\TP\\ChallengeCompletion.pdf"; // Vous pouvez ajuster le chemin

            // Créer un PdfWriter pour écrire dans le document
            PdfWriter writer = new PdfWriter(filePath);

            // Créer un PdfDocument en utilisant le PdfWriter
            PdfDocument pdf = new PdfDocument(writer);

            // Créer un Document (pour ajouter du contenu)
            Document document = new Document(pdf);

            // Titre principal (centré et avec une grande taille)
            Paragraph title = new Paragraph("Félicitations, Challenge Terminé !")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold();
            document.add(title);

            // Espacement
            document.add(new Paragraph("\n"));

            // Détails du challenge
            document.add(new Paragraph("Détails du Challenge :")
                    .setBold()
                    .setFontSize(14));
            document.add(new Paragraph("Nom du challenge: Super Challenge")
                    .setFontSize(12));

            // Ajouter une table pour la progression
            Table table = new Table(2); // Table avec 2 colonnes

            // Ajouter des cellules à la table en utilisant un Paragraph
            table.addCell(new Cell().add(new Paragraph("Progression :")));
            table.addCell(new Cell().add(new Paragraph("100%")));
            table.addCell(new Cell().add(new Paragraph("Statut :")));
            table.addCell(new Cell().add(new Paragraph("Terminé")));

            document.add(table);

            // Ajouter une phrase de félicitations avec un texte coloré
            Text congratulationText = new Text("Vous avez terminé avec succès le challenge !")
                    .setFontSize(16)
                    .setFontColor(new DeviceRgb(34, 177, 76));  // Vert

            document.add(new Paragraph()
                    .setTextAlignment(TextAlignment.CENTER)
                    .add(congratulationText));

            // Fermer le document
            document.close();


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Une erreur est survenue lors de la génération du PDF.");
        }
    }

    @FXML
    private void terminerChallenge() {
        animateFinishButton(terminerButton);
        try {
            // Vérification de l'état actuel
            if (currentChallenge == null) {
                showAlert("Erreur", "Le challenge n'est pas défini. Veuillez sélectionner un challenge avant de continuer.");
                return;
            }
            if (currentParticipation == null) {
                showAlert("Erreur", "La participation n'est pas initialisée. Veuillez réessayer.");
                return;
            }
            if (currentParticipation.getProgression() < 100) {
                showAlert("Erreur", "Le challenge n'est pas encore terminé. Complétez toutes les étapes avant de continuer.");
                return;
            }

            // Logs pour débogage
            System.out.println("Tentative de terminer le challenge...");
            System.out.println("User ID: " + currentParticipation.getUserId());
            System.out.println("Challenge ID: " + currentParticipation.getChallengeId());
            System.out.println("Progression: " + currentParticipation.getProgression());

            // Appel au service
            boolean success = participationService.terminerChallenge(currentParticipation);
            if (success) {
                System.out.println("Challenge terminé avec succès.");
                showAlert("Challenge Terminé", "Félicitations, vous avez terminé le challenge");
                // Appel de la génération du PDF
                generatePDF(); // Appel pour générer le PDF
                showAlert("Un PDF genéré", "Félicitations !, vous avez reçu un PDF");
                // Redirection vers l'interface précédente
                System.out.println("Chargement de l'interface précédente...");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientHome.fxml"));
                Parent root = loader.load();
                Scene scene = terminerButton.getScene();
                scene.setRoot(root);

            } else {
                showAlert("Erreur", "Impossible de terminer le challenge. Vérifiez votre progression ou contactez un administrateur.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors du chargement de l'interface précédente.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur SQL", "Une erreur SQL est survenue : " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void animateProgress(double targetProgress) {
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(progressionBar.progressProperty(), targetProgress);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private void addStepWithAnimation(CheckBox checkBox) {
        FadeTransition fade = new FadeTransition(Duration.seconds(1), checkBox);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
        etapesContainer.getChildren().add(checkBox);
    }

    private void animateCheckBox(CheckBox checkBox) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), checkBox);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }

    private void animateFinishButton(Button button) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), button);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.15);
        scale.setToY(1.15);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }



}
