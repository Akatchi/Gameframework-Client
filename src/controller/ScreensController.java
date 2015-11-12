package controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import utils.Log;
import view.ControlledScreen;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to allow the swapping of screens in JavaFX
 * Code credits to: https://blogs.oracle.com/acaicedo/entry/managing_multiple_screens_in_javafx1
 * And ofcourse the github repo: https://github.com/acaicedo/JFX-MultiScreen
 */
public class ScreensController extends StackPane
{
    // Holds the screens to be displayed
    private Map<String, Node> screens = new HashMap<>();

    public ScreensController()
    {
        super();
    }

    // Add the screen to the collection
    public void addScreen(String name, Node screen)
    {
        screens.put(name, screen);
    }

    // Returns the Node with the appropriate name
    public Node getScreen(String name)
    {
        return screens.get(name);
    }

    // Loads the fxml file, add the screen to the screens collection and
    // finally injects the screenPane to the controller.
    public boolean loadScreen(String name, String resource)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) loader.load();

            ControlledScreen screenController = ((ControlledScreen) loader.getController());
            screenController.setScreenParent(this);

            addScreen(name, loadScreen);

            return true;
        }
        catch( Exception e )
        {
            Log.ERROR(e.getMessage());
            e.printStackTrace();

            return false;
        }
    }

    // This method tries to displayed the screen with a predefined name.
    // First it makes sure the screen has been already loaded.  Then if there is more than
    // one screen the new screen is been added second, and then the current screen is removed.
    // If there isn't any screen being displayed, the new screen is just added to the root.
    public boolean setScreen(final String name)
    {
        if( screens.get(name) != null )
        {
            //screen loaded
            final DoubleProperty opacity = opacityProperty();

            if( !getChildren().isEmpty() )
            {
                //if there is more than one screen
                Timeline fade = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                    new KeyFrame(new Duration(1000), new EventHandler<ActionEvent>()
                    {
                        @Override
                        public void handle(ActionEvent t)
                        {
                            //remove the displayed screen
                            getChildren().remove(0);

                            //add the screen
                            getChildren().add(0, screens.get(name));

                            Timeline fadeIn = new Timeline(
                                new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                new KeyFrame(new Duration(800), new KeyValue(opacity, 1.0))
                            );

                            fadeIn.play();
                        }
                    }, new KeyValue(opacity, 0.0)));

                fade.play();
            }
            else
            {
                setOpacity(0.0);
                getChildren().add(screens.get(name));       //no one else been displayed, then just show

                Timeline fadeIn = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                    new KeyFrame(new Duration(2500), new KeyValue(opacity, 1.0))
                );

                fadeIn.play();
            }

            return true;
        }
        else
        {
            Log.DEBUG("screen hasn't been loaded!!! \n");
            return false;
        }
    }

    // This method will remove the screen with the given name from the collection of screens
    public boolean unloadScreen(String name)
    {
        if( screens.remove(name) == null )
        {
            Log.DEBUG("Screen didn't exist");
            return false;
        }
        else
        {
            return true;
        }
    }
}