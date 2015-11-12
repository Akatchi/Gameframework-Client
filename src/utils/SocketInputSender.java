package utils;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by akatchi on 11-8-15.
 */
public class SocketInputSender implements Runnable
{
    private boolean running;

    private Socket socket;
    private BlockingQueue<String> commandQueue;
    private Thread thread;
    private PrintWriter socketOut;

    public SocketInputSender(Socket socket) throws IOException
    {
        this.socket = socket;

        commandQueue = new LinkedBlockingQueue<String>();
        socketOut = new PrintWriter(socket.getOutputStream(), true);

        Thread thread = new Thread(this, "[Thread] Socket input sender");
        running = true;
        thread.start();
    }

    @Override
    public void run()
    {
        while( running )
        {
            try
            {
                // Wait for a command to become available from the queue
                // When a command is available take it and process it.
                String command = commandQueue.take();

                sendCommand(command);
            }
            catch( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
    }

    public void addCommand(String command)
    {
        commandQueue.add(command);
    }

    private void sendCommand(String command)
    {
        socketOut.println(command);
        Log.DEBUG(String.format("Sent command: %s", command));
    }
}
