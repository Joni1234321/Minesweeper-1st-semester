package models.networking;

import models.gamemodes.ClientMinesweeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The client's connection to the server
 *
 * @author Jonas
 */

public class ServerConnection implements Runnable {
    // Connection
    private final String host;
    private final int port;

    // Write and read
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Process
    private final ClientMinesweeper minesweeper;

    public ServerConnection(ClientMinesweeper minesweeper, String host, int port) {
        this.minesweeper = minesweeper;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run () {
        try {
            System.out.println("Client connecting to server...");
            connect(host, port);
            System.out.println("Client connected successful");
        }
        catch (Exception exception) {
            System.out.println("Client couldn't establish connection to server, please check if server is active");
            return;
        }
        try {
            waitForCommand();
            disconnect();
        }
        catch (Exception ignored) {
            System.out.println("Client disconnected from server");
        }
    }

    private void waitForCommand () throws IOException {
        String fromServer;
        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            Command.applyToMinesweeper(minesweeper, fromServer);
        }
    }
    private void connect (String host, int port) throws IOException {
        socket = new Socket(host, port);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void write (String msg) {
        if (out != null) out.println(msg);
        else System.out.println("Client couldn't write to server, no connection to server");
    }

    public void disconnect () {
        try {
            System.out.println("Client disconnecting...");
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        }
        catch (Exception e) {
            System.out.println("Error in MinesweeperClient.java");
            e.printStackTrace();
        }
    }
}
