/*package Controllers;

import Entite.Challenge;
import Entite.Categorie;
import Service.CategorieService;
import Service.ChallengeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.List;

public class GestionChallengesController {

    @FXML
    private TableView<Challenge> tableChallenges;
    @FXML
    private TableColumn<Challenge, Integer> colId;
    @FXML
    private TableColumn<Challenge, String> colName;
    @FXML
    private TableColumn<Challenge, String> colDescription;
    @FXML
    private TableColumn<Challenge, Integer> colCategoryId;
    @FXML
    private TextField txtName, txtDescription, txtSearch;
    @FXML
    private ComboBox<String> comboCategory;
    @FXML
    private Button btnAdd, btnSearch, btnReset, btnDelete;
    @FXML
    private LineChart<String, Number> statsChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private final ChallengeService challengeService = new ChallengeService();
    private final CategorieService categorieService = new CategorieService();
    private ObservableList<Challenge> challengeList = FXCollections.observableArrayList();
    private List<Categorie> categories;

    @FXML
    private void initialize() {
        setupTableView();
        loadChallenges();
        loadCategories();
        loadStatistics();

        btnAdd.setOnAction(e -> {
            addChallenge();
            loadStatistics(); // Mettre à jour le graphique après ajout
        });
        btnDelete.setOnAction(e -> {
            deleteSelectedChallenge();
            loadStatistics(); // Mettre à jour le graphique après suppression
        });
        btnSearch.setOnAction(e -> searchChallengesByName(txtSearch.getText()));
        btnReset.setOnAction(e -> loadChallenges());
        animateButton(btnAdd);
        animateButton(btnDelete);
        animateButton(btnSearch);
        animateButton(btnReset);
    }

    private void setupTableView() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
    }

    private void loadChallenges() {
        try {
            challengeList = FXCollections.observableArrayList(challengeService.getAllChallenges());
            tableChallenges.setItems(challengeList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les challenges.");
        }
    }

    private void loadCategories() {
        try {
            categories = categorieService.getAllCategories();
            ObservableList<String> categoryNames = FXCollections.observableArrayList();

            for (Categorie category : categories) {
                categoryNames.add(category.getName());
            }

            comboCategory.setItems(categoryNames);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories.");
        }
    }

    private void loadStatistics() {
        try {
            statsChart.getData().clear(); // Vider le graphique

            // Préparer une série de données pour le graphique
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Challenges par catégorie");

            // Charger les statistiques
            for (Categorie category : categories) {
                int count = challengeService.countChallengesByCategory(category.getId());
                series.getData().add(new XYChart.Data<>(category.getName(), count));
            }

            statsChart.getData().add(series);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les statistiques.");
        }
    }

    private void addChallenge() {
        try {
            String selectedCategory = comboCategory.getValue();
            if (selectedCategory == null) {
                showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner une catégorie.");
                return;
            }

            int categoryId = categories.stream()
                    .filter(c -> c.getName().equals(selectedCategory))
                    .findFirst()
                    .map(Categorie::getId)
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie invalide"));

            Challenge challenge = new Challenge(
                    txtName.getText(),
                    txtDescription.getText(),
                    categoryId
            );

            challengeService.ajouter(challenge);
            loadChallenges();
            clearFields(txtName, txtDescription);
            comboCategory.getSelectionModel().clearSelection();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Challenge ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le challenge.");
        }
    }

    private void deleteSelectedChallenge() {
        Challenge selectedChallenge = tableChallenges.getSelectionModel().getSelectedItem();
        if (selectedChallenge != null) {
            try {
                challengeService.deleteChallenge(selectedChallenge);
                loadChallenges();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Challenge supprimé avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le challenge.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun challenge sélectionné", "Veuillez sélectionner un challenge à supprimer.");
        }
    }

    private void searchChallengesByName(String name) {
        try {
            challengeList = FXCollections.observableArrayList(challengeService.searchChallengeByName(name));
            tableChallenges.setItems(challengeList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de rechercher les challenges.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }
    private void animateButton(Button button) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), button);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), button);
        scaleOut.setToX(1);
        scaleOut.setToY(1);

        button.setOnMouseEntered(e -> scaleIn.playFromStart());
        button.setOnMouseExited(e -> scaleOut.playFromStart());
    }
}*/
package Controllers;

import Entite.Challenge;
import Entite.Step;
import Entite.Categorie;
import Service.CategorieService;
import Service.ChallengeService;
import Service.StepService;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GestionChallengesController {

    @FXML
    private TableView<Challenge> tableChallenges;
    @FXML
    private TableColumn<Challenge, Integer> colId;
    @FXML
    private TableColumn<Challenge, String> colName;
    @FXML
    private TableColumn<Challenge, String> colDescription;
    @FXML
    private TableColumn<Challenge, Integer> colCategoryId;
    @FXML
    private TextField txtName, txtDescription, txtSearch;
    @FXML
    private ComboBox<String> comboCategory;
    @FXML
    private Button btnAdd, btnSearch, btnReset, btnDelete;

    @FXML
    private LineChart<String, Number> statsChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private ListView<Step> listViewSteps;
    @FXML
    private TextField txtStepName, txtStepDescription;
    @FXML
    private Button btnAddStep, btnUpdateStep, btnDeleteStep;

    @FXML
    private TextField txtStepOrder;

    @FXML
    private VBox root;  // Le VBox principal de l'interface

    private final ChallengeService challengeService = new ChallengeService();
    private final CategorieService categorieService = new CategorieService();
    private final StepService stepService = new StepService();

    private ObservableList<Challenge> challengeList = FXCollections.observableArrayList();
    private List<Categorie> categories;

    @FXML
    private void initialize() {
        setupTableView();
        loadChallenges();
        loadCategories();
        loadStatistics();

        btnAdd.setOnAction(e -> {
            addChallenge();
            loadStatistics();
        });
        btnDelete.setOnAction(e -> {
            deleteSelectedChallenge();
            loadStatistics();
        });
        btnSearch.setOnAction(e -> searchChallengesByName(txtSearch.getText()));
        btnReset.setOnAction(e -> loadChallenges());

        btnAddStep.setOnAction(e -> addStep());
        btnUpdateStep.setOnAction(e -> updateStep());
        btnDeleteStep.setOnAction(e -> deleteStep());

        tableChallenges.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadStepsForSelectedChallenge();
            }
        });

        animateButton(btnAdd);
        animateButton(btnDelete);
        animateButton(btnSearch);
        animateButton(btnReset);
        animateButton(btnAddStep);
        animateButton(btnUpdateStep);
        animateButton(btnDeleteStep);
    }

    private void setupTableView() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
    }

    private void loadChallenges() {
        try {
            challengeList = FXCollections.observableArrayList(challengeService.getAllChallenges());
            tableChallenges.setItems(challengeList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les challenges.");
        }
    }

    private void loadCategories() {
        try {
            categories = categorieService.getAllCategories();
            ObservableList<String> categoryNames = FXCollections.observableArrayList();
            for (Categorie category : categories) {
                categoryNames.add(category.getName());
            }
            comboCategory.setItems(categoryNames);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les catégories.");
        }
    }

    private void loadStatistics() {
        try {
            statsChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Challenges par catégorie");

            for (Categorie category : categories) {
                int count = challengeService.countChallengesByCategory(category.getId());
                series.getData().add(new XYChart.Data<>(category.getName(), count));
            }
            statsChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les statistiques.");
        }
    }

    private void addChallenge() {
        try {
            String selectedCategory = comboCategory.getValue();
            if (selectedCategory == null) {
                showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner une catégorie.");
                return;
            }

            int categoryId = categories.stream()
                    .filter(c -> c.getName().equals(selectedCategory))
                    .findFirst()
                    .map(Categorie::getId)
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie invalide"));

            Challenge challenge = new Challenge(
                    txtName.getText(),
                    txtDescription.getText(),
                    categoryId
            );

            challengeService.ajouter(challenge);
            loadChallenges();
            clearFields(txtName, txtDescription);
            comboCategory.getSelectionModel().clearSelection();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Challenge ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le challenge.");
        }
    }

    private void deleteSelectedChallenge() {
        Challenge selectedChallenge = tableChallenges.getSelectionModel().getSelectedItem();
        if (selectedChallenge != null) {
            try {
                challengeService.deleteChallenge(selectedChallenge);
                loadChallenges();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Challenge supprimé avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le challenge.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun challenge sélectionné", "Veuillez sélectionner un challenge à supprimer.");
        }
    }

    private void loadStepsForSelectedChallenge() {
        Challenge selectedChallenge = tableChallenges.getSelectionModel().getSelectedItem();
        if (selectedChallenge != null) {
            try {
                ObservableList<Step> steps = FXCollections.observableArrayList(stepService.getStepsByChallengeId(selectedChallenge.getId()));
                listViewSteps.setItems(steps);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les étapes.");
            }
        }
    }

    private void addStep() {
        Challenge selectedChallenge = tableChallenges.getSelectionModel().getSelectedItem();
        if (selectedChallenge != null) {
            try {

                int challengeId = selectedChallenge.getId();
                System.out.println("Challenge ID: " + challengeId); // Debugging
                if (challengeService.getChallengeById(challengeId) == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Le challenge sélectionné n'existe pas.");
                    return;
                }
                Step step = new Step(selectedChallenge.getId(),txtStepName.getText(), txtStepDescription.getText());
                stepService.addStep(step);
                loadStepsForSelectedChallenge();
                clearFields(txtStepName, txtStepDescription);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Étape ajoutée avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'étape.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucun challenge sélectionné", "Veuillez sélectionner un challenge pour ajouter une étape.");
        }
    }

    /*private void updateStep() {
        Step selectedStep = listViewSteps.getSelectionModel().getSelectedItem();
        if (selectedStep != null) {
            try {
                selectedStep.setName(txtStepName.getText());
                selectedStep.setDescription(txtStepDescription.getText());
                stepService.updateStep(selectedStep);
                loadStepsForSelectedChallenge();
                clearFields(txtStepName, txtStepDescription);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Étape mise à jour avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour l'étape.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune étape sélectionnée", "Veuillez sélectionner une étape à mettre à jour.");
        }
    }*/

    /*
    @FXML
    private void updateStep() {
        // Récupérer l'étape sélectionnée dans la ListView
        Step selectedStep = listViewSteps.getSelectionModel().getSelectedItem();

        if (selectedStep != null) {
            try {
                // Charger la nouvelle fenêtre (FXML) pour mettre à jour l'étape
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateStep.fxml"));
                Parent root = loader.load();

                // Initialiser le contrôleur avec l'étape sélectionnée
                UpdateStepController updateStepController = loader.getController();
                updateStepController.initData(selectedStep);

                // Créer une nouvelle scène et l'afficher dans une nouvelle fenêtre
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Mettre à jour l'étape");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de mise à jour.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune étape sélectionnée", "Veuillez sélectionner une étape à mettre à jour.");
        }
    }*/

    @FXML
    private void updateStep() {
        // Récupérer l'étape sélectionnée dans la ListView
        Step selectedStep = listViewSteps.getSelectionModel().getSelectedItem();

        if (selectedStep != null) {
            try {
                // Charger la nouvelle fenêtre (FXML) pour mettre à jour l'étape
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateStep.fxml"));
                Parent root = loader.load();

                // Initialiser le contrôleur avec l'étape sélectionnée
                UpdateStepController updateStepController = loader.getController();
                updateStepController.initData(selectedStep);

                // Créer une nouvelle scène et l'afficher dans une nouvelle fenêtre
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Mettre à jour l'étape");

                // Appliquer le style CSS à la scène
                scene.getStylesheets().add(getClass().getResource("/UpdateStep.css").toExternalForm());

                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de mise à jour.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune étape sélectionnée", "Veuillez sélectionner une étape à mettre à jour.");
        }
    }



    private void deleteStep() {
        Step selectedStep = listViewSteps.getSelectionModel().getSelectedItem();
        if (selectedStep != null) {
            try {
                stepService.deleteStep(selectedStep.getId());
                loadStepsForSelectedChallenge();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Étape supprimée avec succès !");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'étape.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune étape sélectionnée", "Veuillez sélectionner une étape à supprimer.");
        }
    }
    private void searchChallengesByName(String name) {
        try {
            List<Challenge> filteredChallenges = challengeService.searchChallengeByName(name);
            challengeList = FXCollections.observableArrayList(filteredChallenges);
            tableChallenges.setItems(challengeList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les challenges filtrés.");
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void animateButton(Button button) {

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), button);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), button);
        scaleOut.setToX(1);
        scaleOut.setToY(1);

        button.setOnMouseEntered(e -> scaleIn.playFromStart());
        button.setOnMouseExited(e -> scaleOut.playFromStart());
    }
}



