package models.gamemodes;

import models.Tile;
import models.features.Difficulty;
import models.networking.Command;
import models.networking.ServerConnection;

/**
 * A model for the Client-side of the Multiplayer game mode
 *
 * @author Jonas
 */

public class ClientMinesweeper extends NetworkingMinesweeper {
    Difficulty myDifficulty;
    String ipAddress;
    ServerConnection connection;

    public ClientMinesweeper(Difficulty myDifficulty, String ipAddress) {
        super();
        this.myDifficulty = myDifficulty;
        this.ipAddress = ipAddress;
    }

    @Override
    public void startGame () {
        try {
            connection = new ServerConnection(this, ipAddress, 3000);
            new Thread(connection).start();
        } catch (Exception e) {
            System.out.println("Could not create minesweeper client");
            System.out.println(e.getMessage());
        }
    }

    //region Upstream Methods
    @Override
    public void leftClickTile(Tile tile) {
        connection.write(Command.tileClickToString(tile));
    }
    @Override
    public void setFlag(Tile tile) {
        connection.write(Command.setFlagToString(tile));
    }
    @Override
    public void clearFlag(Tile tile) {
        connection.write(Command.clearFlagToString(tile));
    }
    @Override
    public void load(String loadString) {
        connection.write(Command.minesweeperToString(loadString));
    }
    @Override
    public void resetGame() {
        connection.write(Command.createNewGameToString(myDifficulty));
    }
    //endregion


    @Override
    public void onGameClose() {
        super.onGameClose();
        connection.disconnect();
    }
}
