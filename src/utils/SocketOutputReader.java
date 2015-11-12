package utils;

import com.google.gson.Gson;
import listeners.*;
import models.JsonMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by akatchi on 11-8-15.
 */
public class SocketOutputReader implements Runnable
{
    private boolean running;

    private Scanner socketOutput;
    private Socket socket;
    private Thread thread;

    // We use the CopyOnWriteArrayList (this is a thread-safe variant of Arraylist)
    // to prevent the throwign of the ConcurrentModificationExcpetion.
    // This exceptions gets thrown whenever an addition is made while the list is being iterated over
    // This can happen when we receiver messages from the server (quickly in a row) with the usage of this
    // list we can prevent the error (since it creates a copy whenever a write command is ordered).
    private CopyOnWriteArrayList<LoginErrorListener> loginErrorListeners = new CopyOnWriteArrayList<LoginErrorListener>();
    private CopyOnWriteArrayList<LoginOkListener> loginOkListeners = new CopyOnWriteArrayList<LoginOkListener>();
    private CopyOnWriteArrayList<ErrorListener> errorListeners = new CopyOnWriteArrayList<ErrorListener>();
    private CopyOnWriteArrayList<ChallengeInvitedListener> challengeInvitedListeners = new CopyOnWriteArrayList<ChallengeInvitedListener>();
    private CopyOnWriteArrayList<ChallengeAcceptedListener> challengAcceptedListeners = new CopyOnWriteArrayList<ChallengeAcceptedListener>();
    private CopyOnWriteArrayList<MoveListener> moveListeners = new CopyOnWriteArrayList<MoveListener>();
    private CopyOnWriteArrayList<PlayerListReceivedListener> playerListReceivedListeners = new CopyOnWriteArrayList<PlayerListReceivedListener>();
    private CopyOnWriteArrayList<MatchStartedListener> matchStartedListeners = new CopyOnWriteArrayList<MatchStartedListener>();
    private CopyOnWriteArrayList<TurnResponseListener> turnResponseListeners = new CopyOnWriteArrayList<TurnResponseListener>();
    private CopyOnWriteArrayList<GameOverListener> gameOverListeners = new CopyOnWriteArrayList<GameOverListener>();

    public SocketOutputReader(Socket socket) throws IOException
    {
        this.socket = socket;

        socketOutput = new Scanner(socket.getInputStream());

        Thread thread = new Thread(this, "[Thread] Socket output reader");
        running = true;
        thread.start();
    }

    @Override
    public void run()
    {
        while( running )
        {
            while( socketOutput.hasNext() )
            {
                String line = socketOutput.nextLine();

                // Transfer the json string from the server to the JsonMessage object.
                Gson gson = new Gson();
                JsonMessage message = gson.fromJson(line, JsonMessage.class);

                Log.DEBUG(String.format("Server message: %s", line));

                switch( message.STATUS )
                {
                    case "LOGIN_OK":
                        notifyLoginOkListeners(message);
                        break;
                    case "LOGIN_ERROR":
                        notifyLoginErrorListeners(message);
                        break;
                    case "ERROR":
                        notifyErrorListeners(message);
                        break;
                    case "CHALLENGE_INVITED":
                        notifyChallengeInvitedListeners(message);
                        break;
                    case "CHALLENGE_ACCEPTED":
                        notifyChallengeAcceptedListeners(message);
                        break;
                    case "MOVE":
                        notifyMoveListeners(message);
                        break;
                    case "PLAYERLIST_RECEIVED":
                        notifyPlayerListReceivedListeners(message);
                        break;
                    case "MATCH_STARTED":
                        notifyMatchStartedListeners(message);
                        break;
                    case "TURN":
                        notifyTurnResponseListeners(message);
                        break;
                    case "GAME_OVER":
                        notifyGameOverListeners(message);
                        break;
                }
            }
        }
    }

    public void addLoginOkMessageListener(LoginOkListener listener)
    {
        loginOkListeners.addIfAbsent(listener);
    }

    public void removeLoginOkMessageListener(LoginOkListener listener)
    {
        loginOkListeners.remove(listener);
    }

    private void notifyLoginOkListeners(JsonMessage message)
    {
        for( LoginOkListener listener : loginOkListeners )
        {
            listener.loggedIn(message);
        }
    }

    public void addLoginErrorListener(LoginErrorListener listener)
    {
        loginErrorListeners.addIfAbsent(listener);
    }

    public void removeLoginErrorListener(LoginErrorListener listener)
    {
        loginErrorListeners.remove(listener);
    }

    private void notifyLoginErrorListeners(JsonMessage message)
    {
        for( LoginErrorListener listener : loginErrorListeners )
        {
            listener.loginError(message);
        }
    }

    public void addErrorListener(ErrorListener listener)
    {
        errorListeners.addIfAbsent(listener);
    }

    public void removeErrorListener(ErrorListener listener)
    {
        errorListeners.remove(listener);
    }

    private void notifyErrorListeners(JsonMessage message)
    {
        for( ErrorListener listener : errorListeners )
        {
            listener.errorOccured(message);
        }
    }

    public void addChallengeInviteListener(ChallengeInvitedListener listener)
    {
        challengeInvitedListeners.addIfAbsent(listener);
    }

    public void removeChallengeInviteListener(ChallengeInvitedListener listener)
    {
        challengeInvitedListeners.remove(listener);
    }

    private void notifyChallengeInvitedListeners(JsonMessage message)
    {
        for( ChallengeInvitedListener listener : challengeInvitedListeners )
        {
            listener.challengeInvitationReceived(message);
        }
    }

    public void addChallengeAcceptedListener(ChallengeAcceptedListener listener)
    {
        challengAcceptedListeners.addIfAbsent(listener);
    }

    public void removeChallengeAcceptedListener(ChallengeAcceptedListener listener)
    {
        challengAcceptedListeners.remove(listener);
    }

    private void notifyChallengeAcceptedListeners(JsonMessage message)
    {
        for( ChallengeAcceptedListener listener : challengAcceptedListeners )
        {
            listener.challengeAccepted(message);
        }
    }

    public void addMoveListener(MoveListener listener)
    {
        moveListeners.addIfAbsent(listener);
    }

    public void removeMoveListener(MoveListener listener)
    {
        moveListeners.remove(listener);
    }

    private void notifyMoveListeners(JsonMessage message)
    {
        for( MoveListener listener : moveListeners )
        {
            listener.moveMade(message);
        }
    }

    public void addPlayerListReceivedListener(PlayerListReceivedListener listener)
    {
        playerListReceivedListeners.addIfAbsent(listener);
    }

    public void removePlayerListReceivedListener(PlayerListReceivedListener listener)
    {
        playerListReceivedListeners.remove(listener);
    }

    private void notifyPlayerListReceivedListeners(JsonMessage message)
    {
        for( PlayerListReceivedListener listener : playerListReceivedListeners )
        {
            listener.playerListReceived(message);
        }
    }

    public void addMatchStartedListener(MatchStartedListener listener)
    {
        matchStartedListeners.addIfAbsent(listener);
    }

    public void removeMatchStartedListener(MatchStartedListener listener)
    {
        matchStartedListeners.remove(listener);
    }

    private void notifyMatchStartedListeners(JsonMessage message)
    {
        for( MatchStartedListener listener : matchStartedListeners )
        {
            listener.matchStarted(message);
        }
    }

    public void addTurnResponseListener(TurnResponseListener listener)
    {
        turnResponseListeners.addIfAbsent(listener);
    }

    public void removeTurnResponseListener(TurnResponseListener listener)
    {
        turnResponseListeners.remove(listener);
    }

    private void notifyTurnResponseListeners(JsonMessage message)
    {
        for( TurnResponseListener listener : turnResponseListeners )
        {
            listener.turnMessageReceived(message);
        }
    }

    public void addGameOverListener(GameOverListener listener)
    {
        gameOverListeners.addIfAbsent(listener);
    }

    public void removeGameOverListener(GameOverListener listener)
    {
        gameOverListeners.remove(listener);
    }

    private void notifyGameOverListeners(JsonMessage message)
    {
        for( GameOverListener listener : gameOverListeners )
        {
            listener.announceMatchResults(message);
        }
    }
}
