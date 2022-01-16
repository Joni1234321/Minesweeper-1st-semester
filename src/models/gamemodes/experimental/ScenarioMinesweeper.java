package models.gamemodes.experimental;

import models.Minesweeper;
import models.Tile;
import models.features.Difficulty;

import java.util.List;
import java.util.Random;

/**
 * A model for the Scenario game mode
 *
 * @author Jonas
 */

public class ScenarioMinesweeper extends Minesweeper {
    private final double progress;
    private final boolean mistakes, boundToNeighbors;

    public ScenarioMinesweeper (Difficulty diff) {
        this(diff, 0.8, true, false);
    }
    public ScenarioMinesweeper (Difficulty diff, double progress, boolean mistakes, boolean bound) {
        super(diff);
        this.progress = progress;
        this.mistakes = mistakes;
        this.boundToNeighbors = bound;
    }

    //region MAP GENERATION
    @Override
    public void resetGame (int w, int h, int bombCount) {
        super.resetGame(w, h, bombCount);
        leftClickTile(getRandomTile());
        if (mistakes) mistakes();
        else normal();
    }

    void normal () {
        discoverMapUntilReachedProgress(progress);
    }
    void mistakes () {
        discoverMapUntilReachedProgress(progress);
        placeFlagOnUndiscoveredTiles();
    }

    private void discoverMapUntilReachedProgress (double progress) {
        int shouldBeDiscovered = (int)((getMapSize() - bombCount) * progress);
        while (countDiscoveredTiles() < shouldBeDiscovered) {
            List<Tile> tiles = getTiles(t -> !t.isDiscovered() && !t.isBomb() && (!boundToNeighbors || getNeighborTiles(t, Tile::isDiscovered).size() != 0));

            Tile randomTile = tiles.get(new Random().nextInt(tiles.size()));
            leftClickTile(randomTile);
        }
    }

    private void placeFlagOnUndiscoveredTiles () {
        List<Tile> tiles = getTiles(t -> !t.isDiscovered());
        for (Tile t : tiles) setFlag(t);
    }

    private Tile getRandomTile () {
        Tile t = null;
        while(t == null) t = getTile(new Random().nextInt(w), new Random().nextInt(h));
        return t;
    }
    //endregion


    @Override
    public void rightClickTile(Tile tile) {
        super.rightClickTile(tile);
        if (mistakes) leftClickTile(tile);
    }
}
