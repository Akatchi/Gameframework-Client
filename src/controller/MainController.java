package controller;

import ai.tictactoe.EasyAI;
import games.TicTacToe;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import listeners.*;
import models.Challenge;
import models.ChallengeListViewCell;
import models.JsonMessage;
import utils.DialogGenerator;
import utils.Log;
import view.ControlledScreen;

import java.util.*;
import java.util.List;

/**
 * Created by akatchi on 13-8-15.
 */
public class MainController implements ControlledScreen, ErrorListener, MatchStartedListener,
        ChallengeInvitedListener, MoveListener, PlayerListReceivedListener, TurnResponseListener,
        GameOverListener
{
    public ListView playerListView;
    public ListView challengeListView;
    public VBox gameBoardBox;

    private ScreensController screenController;
    private GameController gameController;
    private Map<String, String> challengeMap = new HashMap<String, String>();
    private List<Challenge> activeChallengeList = new ArrayList<Challenge>();
    private List<Button> boardButtons = new ArrayList<Button>();

    // Types for the choice dropdown.
    // The type selection changes the handling of the response
    // from the selection dialog
    private enum ChoiceType
    {
        ChallengeInvite;
    }

    private final String PARAM_INVITED_USER = "InvitedUser";

    private Button getBoardButton(final int posX, final int posY, String buttonValue)
    {
        // Variables with the positions of the current i (posX) and j (posY)
        // Declared final so they are accesible within the onMouseClick handler
        // which belongs with the button. Those variables can be used
        // To determine what move will be made
        Button gameButton = new Button(buttonValue);

        if( gameController != null )
        {
            // We have a gamecontroller
            // So we probably know how big the board is
            // With this in mind we can calculate the required
            // size of the buttons
            double vboxHeight = gameBoardBox.getHeight();
            double vboxWidth = gameBoardBox.getWidth();

            double buttonHeight = vboxHeight / gameController.getBoardHeight();
            double buttonWidth = vboxWidth / gameController.getBoardWidth();

            gameButton.setPrefHeight(buttonHeight);
            gameButton.setPrefWidth(buttonWidth);
        }

        gameButton.setOnMouseClicked(event -> {
            Log.DEBUG(String.format("Clicked the button on pos: [%d, %d]", posX, posY));

            String move = posX + "," + posY;

            // Make the move on the board
            Application.getInstance().makeMove(move);

            // Disable the buttons after the move has been made
            // To prevent another move from being made
            // The buttons should be enabled again after either an
            // error occured or when the human move is again
            disableBoardButtons();
        });

        return gameButton;
    }

    public void retrievePlayerList(ActionEvent actionEvent)
    {
        // TODO remove this to a global call that initiate all listeners
        Application.getInstance().getOutputReader().addPlayerListReceivedListener(this);
        Application.getInstance().getOutputReader().addChallengeInviteListener(this);
        Application.getInstance().getOutputReader().addMatchStartedListener(this);
        Application.getInstance().getOutputReader().addErrorListener(this);
        Application.getInstance().getOutputReader().addMoveListener(this);
        Application.getInstance().getOutputReader().addTurnResponseListener(this);
        Application.getInstance().getOutputReader().addGameOverListener(this);

        // This method will call the PlayerListReceivedListener method when
        // the server retrieved a list of players.
        Application.getInstance().retrievePlayerList();
    }

    @Override
    public void setScreenParent(ScreensController screenController)
    {
        this.screenController = screenController;
    }

    @Override
    public void playerListReceived(JsonMessage message)
    {
        // This method gets called whenever the retrieveplayers button
        // is pressed.
        // In this method we will update our listview with the players
        // that are online on the server.

        // TODO instead of adding the items it should be updated (or cleared adn added)
        // because it throws an error right now

        ObservableList<String> playerNames = FXCollections.observableArrayList();

        for( String playerName : message.PLAYERS )
        {
            // Don't show yourself in the playerlist! (this saves the effort
            // of disabling the playeritem in the list and indicating that its you)
            if( !playerName.equals(Application.getInstance().getLoggedInUserName()) )
            {
                playerNames.add(playerName);
            }
        }

        // TODO move this code to a custom trhead
        // Since we got this error: java.lang.IllegalStateException: Not on FX application thread; currentThread = [Thread] Socket output reader
        // but try to replicate this error first

        // Make sure we have some players in the list to update the listview
        // otherwise we have to display a notification showing that no players
        // where received from the server
        if( playerNames != null && playerNames.size() > 0 )
        {
            playerListView.setItems(playerNames);

            // TODO move this code to a possible initialize!
            playerListView.setOnMouseClicked(event -> {
                // TODO get the gametypes from the server instead of a hardcode value
                List<String> gameTypes = new ArrayList<String>();
                gameTypes.add("Tic Tac Toe");

                // Add the user we want to invite as parameter.
                // This is done so we can retrieve the selected user lateron (while
                // we are processing the dialog)
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put(PARAM_INVITED_USER, (String) playerListView.getSelectionModel().getSelectedItem());

                displaySelectDialogOnThread(
                        "Please select a gametype for the game you want to play.",
                        "Gametype: ",
                        gameTypes,
                        parameters,
                        ChoiceType.ChallengeInvite);
            });
        }
        else
        {
            displayInfoDialogOnThread("No active players found on the server!");
        }
    }

    @Override
    public void challengeInvitationReceived(JsonMessage message)
    {
        Challenge challenge = new Challenge(
                Integer.valueOf(message.CHALLENGENUMBER), message.CHALLENGER, message.OPPONENT, message.GAMETYPE
        );

        activeChallengeList.add(challenge);

        updateChallengeListView();
    }

    @Override
    public void matchStarted(JsonMessage message)
    {
        Log.DEBUG(String.format("Starting game %s. Challenge got accepted!", message.GAMETYPE));

        // TODO ask for the ai selection
        // TODO remove the selected challenge fro the list
        // ADD MATCH ID TO THE RESPONSE IN THE SERVER

        gameController = new GameController(new TicTacToe(message.PLAYERTOMOVE, message.OPPONENT), null);

        setupBoard(gameController.getBoardHeight(), gameController.getBoardWidth());

    }

    @Override
    public void turnMessageReceived(JsonMessage message)
    {
        // If the yourturn part of the message is not empty
        // it is our turn so here we decide what move to make
        if( message.YOURTURN != null && !message.YOURTURN.equals("") )
        {
            String move = gameController.getBestMove();

            // Check if we are playing as human or not if we are playing
            // as human we must prompt the user to make a move
            if( move.equals(gameController.HUMAN_MUST_MOVE) )
            {
                // enable buttons so the human can make a move
                enableBoardButtons();

                // TODO on invalid move enable the buttons again
                // with human move
            }
            else
            {
                Application.getInstance().makeMove(move);
            }
        }
    }

    @Override
    public void moveMade(JsonMessage message)
    {
        gameController.setMove(message.MOVE, message.PLAYER);

        int[][] board = gameController.getBoard();

        updateBoard(board);
    }

    @Override
    public void announceMatchResults(JsonMessage matchResults)
    {
        Log.DEBUG("Got matchresults!");
        displayInfoDialogOnThread(String.format("You %s the match! \n%s", matchResults.GAMESTATE, matchResults.MATCHCOMMENT));
    }

    @Override
    public void errorOccured(JsonMessage error)
    {
        displayErrorDialogOnThread(error.MESSAGE);
    }

    private void setupBoard(int height, int width)
    {
        Log.DEBUG(String.format("SETTING UP BOARD %d %d", height, width));

        // clear the vbox
        // Remove all children (buttons) and re-add them with
        // the updated values from the board

        // This has to be place in a new thread that runs on the JavaFX GUI Mainthread
        // Otherwise the thread will throw an error that the GUI (alert) call wasn't made
        // from the main JavaFX Thread
        Platform.runLater(() -> gameBoardBox.getChildren().clear());

        // Reinstantiate the button list to clear all the previous buttons
        boardButtons = new ArrayList<Button>();

        // TODO display board in GUI in a nice proper
        // way

        for( int i = 0; i < height; i++ )
        {
            // Create horizontal boxes for each height item
            HBox buttonLine = new HBox();

            for( int j = 0; j < width; j++ )
            {
                // Create buttons on the horizontal boxes
                // previously created for each width element
                Button gameButton = getBoardButton(i, j, gameController.getGameType().getEmptyIndicator());

                buttonLine.getChildren().add(gameButton);

                boardButtons.add(gameButton);
            }

            // This has to be place in a new thread that runs on the JavaFX GUI Mainthread
            // Otherwise the thread will throw an error that the GUI (alert) call wasn't made
            // from the main JavaFX Thread
            Platform.runLater(() -> gameBoardBox.getChildren().add(buttonLine));
        }

        // Disable the buttons so that the moves can't be made
        // unless it is the turn of a human based player (as in
        // no ai playing)
        disableBoardButtons();

    }

    private void updateBoard(int[][] currentBoard)
    {
        Log.DEBUG("UPDATING THE BOARD");

        // Remove all children (buttons) and re-add them with
        // the updated values from the board

        // This has to be place in a new thread that runs on the JavaFX GUI Mainthread
        // Otherwise the thread will throw an error that the GUI (alert) call wasn't made
        // from the main JavaFX Thread
        Platform.runLater(() -> gameBoardBox.getChildren().clear());

        // Reinstantiate the button list to clear all the previous buttons
        boardButtons = new ArrayList<Button>();

        for( int i = 0; i < currentBoard.length; i++ )
        {
            HBox buttonLine = new HBox();
            for( int j = 0; j < currentBoard[i].length; j++ )
            {
                // Update the button on this position
                // With the icon from the player
                // who 'owns' this button
                String buttonValue = gameController.getGameType().getEmptyIndicator();

                if( currentBoard[i][j] == gameController.getGameType().getPlayerOneArrayIndicator() )
                {
                    buttonValue = gameController.getGameType().getPlayerOneIndicator();
                }
                else if( currentBoard[i][j] == gameController.getGameType().getPlayerTwoArrayIndicator() )
                {
                    buttonValue = gameController.getGameType().getPlayerTwoIndicator();
                }

                Button gameButton = getBoardButton(i, j, buttonValue);

                buttonLine.getChildren().add(gameButton);

                boardButtons.add(gameButton);
            }

            // This has to be place in a new thread that runs on the JavaFX GUI Mainthread
            // Otherwise the thread will throw an error that the GUI (alert) call wasn't made
            // from the main JavaFX Thread
            Platform.runLater(() -> gameBoardBox.getChildren().add(buttonLine));
        }

        // Disable the board buttons again
        // to prevent the possibility of creating
        // 'false' moves
        disableBoardButtons();
    }

    private void enableBoardButtons()
    {
        // Enable all the buttons in the board
        for( Button button : boardButtons )
        {
            button.setDisable(false);

            // Only enable the button who are still
            // available for moves (in other words
            // Buttons who have the empty game string)
            if( gameController.getGameType() != null )
            {
                // Make sure we have a active boardgame
                // Where w ecan check the empty value
                if( !button.getText().equals(gameController.getGameType().getEmptyIndicator()) )
                {
                    // The button is not empty so we
                    // have to disable it
                    button.setDisable(true);
                }
            }
        }
    }

    private void disableBoardButtons()
    {
        // Disable all the buttons in the board
        for( Button button : boardButtons )
        {
            button.setDisable(true);
        }
    }

    private void updateChallengeListView()
    {
        ObservableList<Challenge> challengeList = FXCollections.observableArrayList();

        for( Challenge challenge : activeChallengeList )
        {
            challengeList.add(challenge);
        }

        challengeListView.setItems(challengeList);

        // See the link over here to see how this is implemented.
        // This is done to display the challenge in a nice way in the listview
        // http://stackoverflow.com/questions/19588029/customize-listview-in-javafx-with-fxml
        // answer: http://stackoverflow.com/a/19632586
        challengeListView.setCellFactory(listView -> new ChallengeListViewCell());

        // TODO move this code to a possible initialize!
        challengeListView.setOnMouseClicked(event -> {
            // Get the selected challenge
            Challenge challenge = (Challenge) challengeListView.getSelectionModel().getSelectedItem();

            // Check if we are not accepting our own challenge
            // TODO missclicks give a nullpointer fix it :)
            if( !challenge.getChallenger().equals(Application.getInstance().getLoggedInUserName()) )
            {
                // We are not accepting our own challenge so send tehe accept challenge command
                Application.getInstance().acceptChallenge(challenge.getChallengeNumber());
            }
            else
            {
                // We are accepting our own challenge show an error to the user that this is not possible
                displayErrorDialogOnThread("You can't accept your own challenges!");
            }
        });
    }

    private void displaySelectDialogOnThread(String dialogHeader, String dialogTitle, List<String> choices, Map<String, String> parameters, ChoiceType type)
    {
        // This has to be place in a new thread that runs on the JavaFX GUI Mainthread
        // Otherwise the thread will throw an error that the GUI (alert) call wasn't made
        // from the main JavaFX Thread
        Platform.runLater(() -> {
            if( type.equals(ChoiceType.ChallengeInvite) )
            {
                displayAndHandleChallengeInviteDialog(dialogHeader, dialogTitle, choices, parameters);
            }
        });
    }

    private void displayErrorDialogOnThread(String errorMessage)
    {
        // This has to be place in a new thread that runs on the JavaFX GUI Mainthread
        // Otherwise the thread will throw an error that the GUI (alert) call wasn't made
        // from the main JavaFX Thread
        Platform.runLater(() -> displayErrorDialog(errorMessage));
    }

    private void displayInfoDialogOnThread(String infoMessage)
    {
        // This has to be place in a new thread that runs on the JavaFX GUI Mainthread
        // Otherwise the thread will throw an error that the GUI (alert) call wasn't made
        // from the main JavaFX Thread

        Platform.runLater(() -> displayInformationDialog(infoMessage));
    }

    private void displayInformationDialog(String infoMessage)
    {
        DialogGenerator.getSimpleInfoAlert(infoMessage).showAndWait();
    }

    private void displayErrorDialog(String errorMessage)
    {
        String errorTitle = "An error occured.";

        DialogGenerator.getSimpleErrorAlert(errorTitle, errorMessage).showAndWait();
    }

    private void displayAndHandleChallengeInviteDialog(String dialogHeader, String dialogTitle, List<String> choices, Map<String, String> parameters)
    {
        // In the result string will be the choice of the user when he presses ok.
        // we will check if the whether the user pressed ok or cancle by calling
        // result.isPresent() this method will return false when the user
        // canceled the dialog.
        Optional<String> result = DialogGenerator.getChoiceDialog(dialogHeader, dialogTitle, choices).showAndWait();

        if( result.isPresent() )
        {
            // User made a choice and pressed ok.

            // Check if the call passed the parameter which contains the invited user
            // If this is not the case we will throw an error message and do nothing.
            // It is unlikely though that this will happen but 'better safe then sorry'

            if( parameters.get(PARAM_INVITED_USER) != null )
            {
                Log.DEBUG("Sending invite for gametype: " + result.get() + " to user: " + parameters.get(PARAM_INVITED_USER));

                Application.getInstance().sendGameInvite(parameters.get(PARAM_INVITED_USER), result.get());
            }
            else
            {
                displayErrorDialog("No player specified to play against! \nPlease try again");
            }
        }
    }
}
