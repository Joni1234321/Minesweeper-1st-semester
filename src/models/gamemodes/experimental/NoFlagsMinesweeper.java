package models.gamemodes.experimental;

import models.Minesweeper;
import models.Tile;
import models.features.Difficulty;

/**
 * A model for the No Flags game mode
 *
 * @author Jonas
 */

public class NoFlagsMinesweeper extends Minesweeper {


    public NoFlagsMinesweeper (Difficulty difficulty) {
        super(difficulty);
    }

    @Override
    protected void toggleFlag(Tile tile) {
        // Do nothing
    }

}
