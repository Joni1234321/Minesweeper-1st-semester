package models.gamemodes;

import models.Minesweeper;
import models.Tile;
import models.features.Difficulty;

/**
 * A model for the Multiplayer game mode
 *
 * @author Jonas
 */

public class NetworkingMinesweeper extends Minesweeper {

    public NetworkingMinesweeper (Difficulty difficulty) {
        super(difficulty);
    }
    public NetworkingMinesweeper () { super(); }

    //region Downstream Methods
    public void applyLeftClick (Tile tile) {
        super.leftClickTile(tile);
    }
    public void applySetFlag (Tile tile) {
        super.setFlag(tile);
    }
    public void applyClearFlag (Tile tile) {
        super.clearFlag(tile);
    }
    public void applyLoad (String loadString) { super.load(loadString); }
    public void applyResetGame (int w, int h, int bombCount) { super.resetGame(w, h, bombCount); }
    //endregion

}
