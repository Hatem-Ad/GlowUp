package Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import Service.CategorieService;
import Service.UserService;
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
    UserService userService = new UserService();

    @FXML
    private void initialize() {
        loadCategories();
        loadUsers();
        nameColumnn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumnn.setCellValueFactory(new PropertyValueFactory<>("description"));
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

        EM.setCellValueFactory(new PropertyValueFactory<>("email"));
        PS.setCellValueFactory(new PropertyValueFactory<>("password"));

        try {
            ObservableList<User> users = FXCollections.observableArrayList(userService.getAllUsersWithRoleAdmin());
            AdminTable.setItems(users);
        } catch (SQLException e) {
            showErrorDialog("Erreur lors du chargement des administrateurs", "Une erreur s'est produite lors de la récupération des utilisateurs avec le rôle d'administrateur.");
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        try {
            categoriesListt.setAll(categorieServicee.getAllCategories());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        categoriesTablee.setItems(categoriesListt);
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


    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

