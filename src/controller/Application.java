package controller;

import utils.SocketInputSender;
import utils.SocketOutputReader;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by akatchi on 12-8-15.
 */
public class Application
{
    private static Application instance = new Application();

    private String loggedInUserName;

    private SocketInputSender inputSender;
    private SocketOutputReader outputReader;
    private Socket socket;

    private Application(){}

    public static Application getInstance()
    {
        return instance;
    }

    public void connectToSocket(Socket socket)
    {
        this.socket = socket;

        try
        {
            inputSender = new SocketInputSender(socket);
            outputReader = new SocketOutputReader(socket);
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void loginWithUserName(String userName)
    {
        if( inputSender != null )
        {
            inputSender.addCommand(String.format("login %s", userName));
        }
    }

    public void retrievePlayerList()
    {
        if( inputSender != null )
        {
            inputSender.addCommand("get players");
        }
    }

    public void sendGameInvite(String opponentName, String gameType)
    {
        if( inputSender != null )
        {
            inputSender.addCommand(String.format("challenge %s %s", opponentName, gameType));
        }
    }

    public void acceptChallenge(int challengeNumber)
    {
        if( inputSender != null )
        {
            inputSender.addCommand(String.format("challenge accept %d", challengeNumber));
        }
    }

    public void makeMove(String move)
    {
        if( inputSender != null )
        {
            inputSender.addCommand(String.format("move %s", move));
        }
    }

    public SocketInputSender getInputSender()
    {
        return inputSender;
    }

    public SocketOutputReader getOutputReader()
    {
        return outputReader;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public String getLoggedInUserName()
    {
        return loggedInUserName;
    }

    public void setLoggedInUserName(String loggedInUserName)
    {
        this.loggedInUserName = loggedInUserName;
    }

    public boolean isConnected()
    {
        if( socket != null )
        {
            return socket.isConnected();
        }

        return false;
    }
}
