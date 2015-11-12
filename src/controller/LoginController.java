package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import listeners.LoginOkListener;
import listeners.LoginErrorListener;
import models.JsonMessage;
import utils.DialogGenerator;
import utils.Log;
import view.ControlledScreen;
import view.ScreensFramework;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by akatchi on 11-8-15.
 */
public class LoginController implements LoginOkListener, LoginErrorListener, ControlledScreen
{
    public Button login;
    public TextField hostIP;
    public TextField port;
    public TextField userName;

    private ScreensController screenController;

    public void handleLoginPressed(ActionEvent actionEvent)
    {
        // disable the textfields and enable them if the an error occured
        disableTextFields();

        // Check if there where any errors
        if( hostIP.getText().equals("") )
        {
            displayErrorDialog("Host IP can't be empty!");
        }
        else if( port.getText().equals("") )
        {
            displayErrorDialog("Port can't be empty!");
        }
        else if( userName.getText().equals("") )
        {
            displayErrorDialog("Username can't be empty");
        }
        else
        {
            //No errors so now we process the login.
            try
            {
                // Check if we are not connected yet (we can be connected if an error occured so we don't have to create
                // a new connection everytime
                if( !Application.getInstance().isConnected() )
                {
                    Socket socket = new Socket(hostIP.getText(), Integer.valueOf(port.getText()));

                    Application.getInstance().connectToSocket(socket);

                    // On the first time this button is pressed we have to register for the listeners.
                    // We can't do this in the constructor because it gets called before that the applicaiton is being initialized.
                    // Register for the loggedinListener and the loginErrorListener
                    Application.getInstance().getOutputReader().addLoginOkMessageListener(this);
                    Application.getInstance().getOutputReader().addLoginErrorListener(this);
                }

                Application.getInstance().loginWithUserName(userName.getText());
            }
            catch( IOException e )
            {
                displayErrorDialog("An error occured while trying to connect to the server!", e);
            }
        }

    }

    @Override
    public void loggedIn(JsonMessage message)
    {
        // Set the loggedin username. This can later be used to boldify or hide
        // your own name in the playerlist.
        // Since the fields where disabled we can assume that the username hasnt changed
        // because the fields where disabled
        Application.getInstance().setLoggedInUserName(userName.getText());

        Application.getInstance().getOutputReader().removeLoginOkMessageListener(this);
        Application.getInstance().getOutputReader().removeLoginErrorListener(this);

        screenController.setScreen(ScreensFramework.MAIN_SCREEN);
    }

    @Override
    public void loginError(JsonMessage error)
    {
        // This has to be place in a new thread that runs on the JavaFX GUI Mainthread
        // Otherwise the thread will throw an error that the GUI (alert) call wasn't made
        // from the main JavaFX Thread
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                displayErrorDialog(error.MESSAGE);
            }
        });
    }

    private void displayErrorDialog(String errorMessage)
    {
        // enable the textfields since an error occured
        enableTextFields();

        String errorTitle = "An error occured while trying to login.";

        DialogGenerator.getSimpleErrorAlert(errorTitle, errorMessage).showAndWait();
    }

    private void displayErrorDialog(String errorTitle, Exception e)
    {
        // enable the textfields since an error occured
        enableTextFields();

        DialogGenerator.getExceptionErrorAlert(errorTitle, e).showAndWait();
    }

    private void enableTextFields()
    {
        hostIP.setEditable(true);
        port.setEditable(true);
        userName.setEditable(true);
    }

    private void disableTextFields()
    {
        hostIP.setEditable(false);
        port.setEditable(false);
        userName.setEditable(false);
    }

    @Override
    public void setScreenParent(ScreensController screenController)
    {
        this.screenController = screenController;
    }
}
