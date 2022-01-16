package models.networking;

import java.io.*;
import java.net.*;

/**
 * The server's connection to the client
 *
 * @author Jonas
 */

public class ClientConnection implements Runnable {
    // MinesweeperClient
    private final int id;
    private final Socket clientSocket;

    // Parent
    private final MinesweeperServer server;

    // IO
    private PrintWriter out;
    private BufferedReader in;

    public ClientConnection(MinesweeperServer server, Socket clientSocket, int id) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.id = id;
    }

    public void write (String msg) {
        out.println(msg);
    }

    @Override
    public void run () {
        try {
            connect();
            waitForCommand();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        try {
            disconnect();
        } catch (Exception e) {
            System.out.println("Couldn't disconnect properly in clientmanager");
            System.out.println(e.getMessage());
        }
    }

    private void waitForCommand () throws IOException{
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("[" + (id+1) + "]: " + line);
            Command.applyToMinesweeper(server.getMinesweeper(), line);

            if (Command.shouldBroadcast(line)) server.broadcast(line);
        }
    }

    private void connect () throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Send map
        if (server.getMinesweeper().isBombsGenerated()) {
            write(Command.minesweeperToString(server.getMinesweeper()));
        } else {
            write(Command.createNewGameToString(server.getMinesweeper()));
        }
    }
    private void disconnect () throws IOException {
        out.close();
        in.close();

        if(server != null) {
            server.removePlayer(id);
            System.out.println("[" + (id + 1) + "] disconnected \t| " + (server.MAX_CLIENTS - server.activeClients)+ " free slots");
        }
    }
}
