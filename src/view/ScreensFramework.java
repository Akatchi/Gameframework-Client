package view;

import controller.ScreensController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreensFramework extends Application
{
    private static final String FXML_FOLDER = "/view/";

    public static final String LOGIN_SCREEN = "login";
    public static final String LOGIN_SCREEN_FXML = FXML_FOLDER + "login.fxml";
    public static final String MAIN_SCREEN = "main";
    public static final String MAIN_SCREEN_FXML = FXML_FOLDER + "main.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen(LOGIN_SCREEN, LOGIN_SCREEN_FXML);
        mainContainer.loadScreen(MAIN_SCREEN, MAIN_SCREEN_FXML);

        mainContainer.setScreen(LOGIN_SCREEN);

        Group root = new Group();
        root.getChildren().addAll(mainContainer);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        //TODO set exit on close because right now it stays alive :)
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
