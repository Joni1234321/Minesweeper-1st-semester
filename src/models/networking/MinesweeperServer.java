package models.networking;

import models.gamemodes.ServerMinesweeper;

import java.io.IOException;
import java.net.*;

/**
 * The Minesweeper server class
 *
 * @author Jonas
 */

public class MinesweeperServer {

    private final int port;
    private ServerSocket server;

    // Clients
    public final int MAX_CLIENTS = 8;
    public int activeClients = 0;
    private final ClientConnection[] clients = new ClientConnection[MAX_CLIENTS];

    private final ServerMinesweeper minesweeper = new ServerMinesweeper(this);

    public MinesweeperServer(int port) {
        this.port = port;
    }

    // WHEN ACTIVE METHODS
    public void write (int receiver, String msg) {
        clients[receiver].write(msg);
    }
    public void broadcast (String msg) {
        broadcast(-1, msg);
    }
    public void broadcast (int sender, String msg) {
        for (int i = 0; i < MAX_CLIENTS; i++) {
            if (i == sender) continue;
            if (clients[i] != null) {
                clients[i].write(msg);
            }
        }
    }

    public void removePlayer (int id) {
        if(clients[id] != null) {
            activeClients--;
            clients[id] = null;
        }
    }

    private void startClientReference(Socket socket) {
        int id = generatePlayerId();

        // Create MinesweeperClient
        clients[id] = new ClientConnection(this, socket, id);
        new Thread(clients[id]).start();

        // Free slots
        activeClients++;
        System.out.println("[" + (id + 1) + "] connected \t\t| " + (MAX_CLIENTS - activeClients)+ " free slots");
    }
    private int generatePlayerId () {
        for (int i = 0; i < MAX_CLIENTS; i++) {
            if (clients[i] == null) return i;
        }
        return -1;
    }


    public void start () throws IOException {
        System.out.println("Starting server");
        try {
            server = new ServerSocket(port);

            //noinspection InfiniteLoopStatement
            while (true) {
                if (activeClients == MAX_CLIENTS) continue;
                startClientReference(server.accept());
            }
        }
        catch (Exception e) {
            // In case port doesnt work
            System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
            System.out.println(e.getMessage());
            stop();
        }
    }
    public void stop () throws IOException {
        System.out.println("Stopping server");
        if (server != null) server.close();
    }

    public ServerMinesweeper getMinesweeper() {
        return minesweeper;
    }
}
