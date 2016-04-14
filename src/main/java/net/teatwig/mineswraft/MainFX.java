package net.teatwig.mineswraft;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by timo on 03.03.2016.
 */
public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        String implVersion = getClass().getPackage().getImplementationVersion();
        String version = implVersion==null?"DEV":implVersion;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gui.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        primaryStage.setOnCloseRequest(controller::quit);
        primaryStage.setTitle("Mineswraft "+version);
        primaryStage.getIcons().addAll(Controller.gameIcon());
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}
