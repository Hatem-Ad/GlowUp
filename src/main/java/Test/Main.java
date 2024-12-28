package Test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));

        // Créer la scène avec le contenu chargé
        Scene scene = new Scene(loader.load());

        // Ajouter le fichier CSS à la scène
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Configurer la scène principale
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
