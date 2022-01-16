package models.gamemodes;

import models.Tile;
import models.networking.Command;
import models.networking.MinesweeperServer;

/**
 * A model for the Server-side of the Multiplayer game mode
 *
 * @author Jonas
 */

public class ServerMinesweeper extends NetworkingMinesweeper {
    private final MinesweeperServer server;

    public ServerMinesweeper(MinesweeperServer server) {
        // Set server with default size to easy
        super();
        this.server = server;
    }

    //region Downstream Methods
    @Override
    protected void generateBombs (Tile firstTile){
        System.out.println("Generating bombs");
        super.generateBombs(firstTile);
        server.broadcast(Command.minesweeperToString(this));
    }
    //endregion

    @Override
    protected void checkGameEnd() {
        // Do nothing. Server should not check if game has ended
    }
}
