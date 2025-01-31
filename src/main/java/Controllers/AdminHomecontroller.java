package Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import Entite.Evenement;
import Service.*;
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

import Service.CategorieService;
import Entite.Categorie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import Entite.Categorie;
import Entite.User;
import Service.CategorieService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AdminHomecontroller {
    @FXML
    private TableView<Categorie> categoriesTablee;
    @FXML
    private TableColumn<Categorie, String> nameColumnn;
    @FXML
    private TableColumn<Categorie, String> descriptionColumnn;
    @FXML
    private ObservableList<Categorie> categoriesListt = FXCollections.observableArrayList();
    @FXML
    private CategorieService categorieServicee = new CategorieService();
    @FXML
    private AnchorPane mainpane;
    @FXML
    private TableView<User> AdminTable;
    @FXML
    private TableColumn<User, String> email, password;
    @FXML
    private TextField Ename, Epassword, Edescription, Ecname;
    @FXML
    private TableColumn<Categorie, Void> actionsColumn;
    @FXML
    private Label Emaill;
    @FXML
    private TableColumn<User, String> EM;
    @FXML
    private TableColumn<User, String> PS;
    @FXML
    private Label ppassword;
    @FXML
    private EvenementService evenementsServicee = new EvenementService();
    @FXML
    private ObservableList<Evenement> evenementsListt = FXCollections.observableArrayList();
    @FXML
    private TableView<Evenement> evenementsTablee;
    @FXML
    private TableColumn<Evenement, String> nomColumnn;
    @FXML
    private TableColumn<Evenement, String> descriptionnColumnn;
    @FXML
    private TableColumn<Evenement, Void> actionColumn;

    UserService userService = new UserService();

    @FXML
    private Hyperlink adminNameLink;
    @FXML
    public void setAdminName(String email) {
        adminNameLink.setText(email); // Mettre à jour le texte du lien
    }


    @FXML
    private void initialize() {
        loadCategories();
        loadevenements();
        loadUsers();
        nameColumnn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumnn.setCellValueFactory(new PropertyValueFactory<>("description"));
        nomColumnn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionnColumnn.setCellValueFactory(new PropertyValueFactory<>("description"));
        actionsColumn.setCellFactory(new Callback<TableColumn<Categorie, Void>, TableCell<Categorie, Void>>() {
            @Override
            public TableCell<Categorie, Void> call(TableColumn<Categorie, Void> param) {
                return new TableCell<Categorie, Void>() {
                    private final Button detailsButton = new Button("Détails");
                    {
                        detailsButton.setStyle("-fx-background-color: #4c5a75; -fx-text-fill: white; "
                                + "-fx-font-size: 14px; -fx-padding: 5px 10px; "
                                + "-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;");
                        detailsButton.setOnAction(e -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionChallenges.fxml"));
                                AnchorPane pane = loader.load();
                                Stage stage = new Stage();
                                stage.setScene(new Scene(pane));
                                stage.setTitle("Gestion des Challenges");
                                stage.show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                showAlert("Erreur", "Impossible de charger l'interface de gestion des challenges.");
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5, detailsButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
        actionColumn.setCellFactory(new Callback<TableColumn<Evenement, Void>, TableCell<Evenement, Void>>() {
            @Override
            public TableCell<Evenement, Void> call(TableColumn<Evenement, Void> param) {
                return new TableCell<Evenement, Void>() {
                    private final Button detailsButton = new Button("Détails");
                    {
                        detailsButton.setStyle("-fx-background-color: #4c5a75; -fx-text-fill: white; "
                                + "-fx-font-size: 14px; -fx-padding: 5px 10px; "
                                + "-fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand;");
                        detailsButton.setOnAction(e -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionChallenges.fxml"));
                                AnchorPane pane = loader.load();
                                Stage stage = new Stage();
                                stage.setScene(new Scene(pane));
                                stage.setTitle("Gestion des Challenges");
                                stage.show();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                showAlert("Erreur", "Impossible de charger l'interface de gestion des challenges.");
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5, detailsButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });

        EM.setCellValueFactory(new PropertyValueFactory<>("email"));
        PS.setCellValueFactory(new PropertyValueFactory<>("password"));

        try {
            ObservableList<User> users = FXCollections.observableArrayList(userService.getAllUsersWithRoleAdmin());
            AdminTable.setItems(users);
        } catch (SQLException e) {
            showErrorDialog("Erreur lors du chargement des administrateurs", "Une erreur s'est produite lors de la récupération des utilisateurs avec le rôle d'administrateur.");
            e.printStackTrace();
        }
        // Rafraîchir automatiquement toutes les 10 secondes
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            loadCategories();
            loadevenements();
            loadUsers();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

    }

    private void loadCategories() {
        try {
            categoriesListt.setAll(categorieServicee.getAllCategories());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        categoriesTablee.setItems(categoriesListt);
    }
    private void loadevenements() {
        try {
            evenementsListt.setAll(evenementsServicee.getAllEvenements());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        evenementsTablee.setItems(evenementsListt);
    }
    private void loadUsers() {
        try {
            ObservableList<User> users = FXCollections.observableArrayList(userService.getAllUsersWithRoleAdmin());
            AdminTable.setItems(users);
        } catch (SQLException e) {
            showErrorDialog("Erreur lors du chargement des administrateurs", "Une erreur s'est produite lors de la récupération des utilisateurs avec le rôle d'administrateur.");
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void logout() {    new LoginController().LogOut(mainpane);}

    @FXML
    private void handleAddCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCategorie.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle("Ajouter Catégorie");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la fenêtre d'ajout de catégorie.");
        }
    }

    @FXML
    private void handleAddEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvenement.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle("Ajouter Event");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la fenêtre d'ajout de Event.");
        }
    }

    @FXML
    private void handleViewCategories() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConsulterCategories.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle("Liste des Catégories");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la fenêtre des catégories.");
        }
    }

    @FXML
    private void handleViewevent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConsulterEvenements.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle("Liste des Evenements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la fenêtre des evenements.");
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleViewusers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Consulterusers.fxml"));
            AnchorPane pane = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle("Liste des Catégories");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la fenêtre des utilsateurs.");
        }
    }

}

