package runnables;


import models.networking.MinesweeperServer;

import java.io.IOException;

public class Server {

    public static void main(String[] args) throws IOException {
        MinesweeperServer gs = new MinesweeperServer(3000);
        gs.start();
    }
}
