package models.gamemodes.experimental;

import models.Minesweeper;
import models.Tile;
import models.features.Difficulty;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * A model for the Moving game mode
 *
 * @author Jonas
 */

public class MovingMinesweeper extends Minesweeper {
    private final boolean limitMovement;
    private final boolean cantEscapeFlag;

    public MovingMinesweeper (Difficulty difficulty, boolean limitMovement, boolean cantEscapeFlag) {
        super(difficulty);
        this.limitMovement = limitMovement;
        this.cantEscapeFlag = cantEscapeFlag;
    }

    @Override
    public void leftClickTile(Tile tile) {
        if (tile.hasFlag()) return;
        if (!tile.isDiscovered()) {
            discoverTile(tile);
            if (onTileChangeEvent != null) onTileChangeEvent.handle(null);
            checkGameEnd();
            shuffleRequest();
        }
    }

    @Override
    public void rightClickTile(Tile tile) {
        if (cantEscapeFlag) {
            if (!tile.hasFlag() && !tile.isDiscovered()) {
                setFlag(tile);
                if (!tile.isBomb()) gameLost = true;
                checkGameEnd();
                shuffleRequest();
            }
        } else {
            super.rightClickTile(tile);
        }
    }

    private void shuffleRequest() {
        if (!isGameLost() && !isGameWon()) {
            List<Tile> from = getTiles(t -> !t.isDiscovered() &&                   // Get tiles that is not discovered
                                            (!cantEscapeFlag || !(t.hasFlag())));  // Get tiles that does not have a flag and a bomb on it
            List<Tile> to = getTiles(t -> !t.isDiscovered() &&
                    (!cantEscapeFlag || !(t.hasFlag()))                                 // Flags block if rule is set
            );

            if (limitMovement) moveBombs(from, n -> !n.isDiscovered() && !n.isBomb() && (!cantEscapeFlag || !(n.hasFlag())));
            else shuffleBombs(from, to);

            if (redrawGameEvent != null) redrawGameEvent.handle(null);
        }
    }

    // Shuffle the bombs from one list to another list
    protected void shuffleBombs(List<Tile> from, List<Tile> to) {
        int bombs = 0;
        for (Tile t : from) {
            clearFlag(t);
            if (t.isBomb()) {
                t.removeBomb();
                bombs++;
            }
        }

        if (bombs > to.size()) System.out.println("Cant fit all bombs in the destination");

        Collections.shuffle(to);

        int idx = 0;
        for (Tile t : to) {
            if (idx >= bombs) break;
            if (!t.isBomb()) {
                t.setBomb();
                idx ++;
            }
        }

        calculateAllNeighbors();
    }

    private void moveBombs (List<Tile> from, Predicate<Tile> neighborRequirement) {
        for (Tile t : from) {
            clearFlag(t);
            if (t.isBomb()) {
                // Check for valid neighbors
                List<Tile> validNeighbors = getNeighborTiles(t, neighborRequirement);
                if (validNeighbors.size() == 0) continue;

                // Place bomb at random empty neighbor
                validNeighbors.get(new Random().nextInt(validNeighbors.size())).setBomb();
                t.removeBomb();
            }
        }

        calculateAllNeighbors();
    }
}
