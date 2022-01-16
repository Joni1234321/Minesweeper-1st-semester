package models;

import javafx.event.*;
import models.features.Difficulty;

import java.util.*;
import java.util.function.Predicate;

/**
 * The default Minesweeper object
 *
 * @author Alexander, Jonas, Heini
 */

public abstract class Minesweeper {

    protected int w, h, bombCount;
    protected Tile[][] grid;
    protected int undiscoveredTiles, flags;
    protected boolean playing = true;
    protected boolean bombsGenerated = false;
    protected boolean gameLost = false;
    public Queue<Tile> undrawnChanges = new ArrayDeque<>();

    public Minesweeper(int w, int h, int bombCount) {
        this.w = w;
        this.h = h;
        this.bombCount = bombCount;
        generateEmptyGrid();
    }
    public Minesweeper (String loadString) {
        load(loadString);
    }
    public Minesweeper (Difficulty difficulty) {
        this(difficulty.getW(), difficulty.getH(), difficulty.getBombCount());
    }
    public Minesweeper () {
        this(Difficulty.EASY);
    }

    //region Downstream Methods
    public void leftClickTile (Tile tile) {
        if (tile.hasFlag()) return;

        Stack<Tile> discoverStack = new Stack<>();
        if (!tile.isDiscovered()) {
            discoverStack.push(tile);
        }
        else if (countNeighborFlags(tile) == tile.countNeighborBombs()) {
            for (Tile t : getNeighborTiles(tile)) {
                if (!t.hasFlag() && !t.isDiscovered()) {
                    discoverStack.push(t);
                }
            }
        }

        while (!discoverStack.isEmpty()) {
            Tile currentTile = discoverStack.pop();
            discoverTile(currentTile);
            if (currentTile.countNeighborBombs() == 0 && !currentTile.isBomb()) {
                for (Tile t : getNeighborTiles(currentTile)) {
                    if (!t.isDiscovered()) discoverStack.push(t);
                }
            }
        }

        if (onTileChangeEvent != null) onTileChangeEvent.handle(null);

        checkGameEnd();
    }
    public void rightClickTile (Tile tile) {
        toggleFlag(tile);
    }

    protected void toggleFlag (Tile tile) {
        if (!tile.isDiscovered()) {
            if (tile.hasFlag()) clearFlag(tile);
            else setFlag(tile);
        }
    }
    protected void setFlag (Tile tile) {
        if (!tile.hasFlag()) {
            tile.setFlag();
            flags++;
        }

        undrawnChanges.add(tile);
        if (onTileChangeEvent != null) onTileChangeEvent.handle(null);
    }
    protected void clearFlag (Tile tile) {
        if (tile.hasFlag()) {
            tile.clearFlag();
            flags--;
        }

        undrawnChanges.add(tile);
        if (onTileChangeEvent != null) onTileChangeEvent.handle(null);
    }

    //region Subcommands

    /**
     * Discovers a single tile and updates data accordingly
     * @param tile Tile to discover
     */
    protected void discoverTile (Tile tile)  {
        if (tile == null) return;
        if (!bombsGenerated) { generateBombs(tile); }
        if (!tile.isDiscovered()) {
            if (tile.isBomb()) {
                gameLost = true;
            } else {
                undiscoveredTiles--;
            }
            if (tile.hasFlag()) {
                tile.clearFlag();
                flags--;
            }

            tile.setDiscovered();

            if (onTileChangeEvent != null) undrawnChanges.add(tile);
        }
    }
    //endregion

    //endregion

    //region Check end of game
    public void end () {
        flags = bombCount;
        playing = false;
    }
    public void resetGame (int w, int h, int bombCount) {
        this.w = w;
        this.h = h;
        this.bombCount = bombCount;

        undrawnChanges = new ArrayDeque<>();
        undiscoveredTiles = w*h;
        playing = true;
        bombsGenerated = false;
        gameLost = false;

        generateEmptyGrid();

        if (redrawGameEvent != null) redrawGameEvent.handle(null);
    }
    public void resetGame () {
        resetGame(w, h, bombCount);
    }
    public void startGame () {
        resetGame(w, h, bombCount);
    }

    /**
     * Checks if game has ended and if onGameLost or onGameWon events should be called
     */
    protected void checkGameEnd () {
        if (!playing) return;
        if (isGameLost()) {
            if (onGameLost != null)
                onGameLost.handle(null);
            else
                System.out.println("Missing onGameLost event");
        }
        else if (isGameWon()) {
            if (onGameWon != null)
                onGameWon.handle(null);
            else
                System.out.println("Missing onGameWon event");
        }
    }

    protected boolean isGameWon () {
        return undiscoveredTiles == bombCount && !gameLost && bombsGenerated;
    }
    //endregion


    //region MAP GENERATION
    //region Load and Save
    public String toString () {
        StringBuilder sb = new StringBuilder();
        sb.append(w).append(" ").append(h).append(" ");
        for (Tile tile : getTiles()) {
            int val = 0;
            if (tile.isBomb())          val |= 0b001;
            if (tile.isDiscovered())    val |= 0b010;
            if (tile.hasFlag())         val |= 0b100;
            sb.append(val);
        }
        return sb.toString();
    }
    public void load (String loadString) {
        Scanner in = new Scanner(loadString);

        w = in.nextInt();
        h = in.nextInt();

        undrawnChanges = new ArrayDeque<>();
        bombCount = 0;
        bombsGenerated = true;
        undiscoveredTiles = w*h;

        generateEmptyGrid();

        playing = true;
        gameLost = false;

        String tileValues = in.next();

        int counter = 0;
        for (Tile tile : getTiles()) {
            int tileValue = tileValues.charAt(counter) - '0';
            if ((tileValue & 0b001) > 0) {
                tile.setBomb();
                bombCount++;
            }
            if ((tileValue & 0b010) > 0) {
                tile.setDiscovered();
                undiscoveredTiles--;
                if (tile.isBomb()) gameLost = true;
            }
            if ((tileValue & 0b100) > 0 && !tile.isDiscovered()) {
                tile.setFlag();
                flags++;
            }
            counter++;
        }

        calculateAllNeighbors();
        if (redrawGameEvent != null) {
            redrawGameEvent.handle(null);
        } else {
            System.out.println("Missing redrawGame event");
        }

        checkGameEnd();
    }
    //endregion

    //region Generate Grid
    protected void generateEmptyGrid () {
        flags = 0;
        grid = new Tile[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++ ) {
                grid[x][y] = new Tile(x, y);
            }
        }
        undiscoveredTiles = w*h;
    }

    /**
     * Places the desired amount of bombs randomly
     * @author Alexander
     * @param avoidTiles A list of tiles to not place bombs in
     */
    protected void generateBombs (List<Tile> avoidTiles) {
        List<Boolean> isBombList = new ArrayList<>(w*h);

        bombCount = Math.min(bombCount, w*h - avoidTiles.size());

        for (int i = 0; i < w*h - avoidTiles.size(); i++) {
            isBombList.add(i < bombCount);
        }
        List<Tile> tiles = getTiles();
        Collections.shuffle(isBombList);

        int idx = 0;
        for (Tile tile : tiles) {
            if (!avoidTiles.contains(tile)) {
                if (isBombList.get(idx)) tile.setBomb();
                idx++;
            }
        }

        calculateAllNeighbors();
        bombsGenerated = true;
    }
    protected void generateBombs (Tile firstTile) {
        List<Tile> avoidTiles = getNeighborTiles(firstTile);
        avoidTiles.add(firstTile);

        generateBombs(avoidTiles);
    }

    protected final void calculateAllNeighbors () {
        for (Tile t : getTiles(t -> !t.isBomb()))
            t.setNeighbors(countNeighborBombs(t));
    }

    //endregion
    //endregion

    //region Get Tiles
    /**
     * Get Tile object from coordinates.
     *
     * @param x X-position
     * @param y Y-position
     * @return Tile at given coordinates. Out of bounds returns null
     */
    public Tile getTile(int x, int y) {
        if (x >= w || y >= h || x < 0 || y < 0) return null;
        return grid[x][y];
    }
    // Returns list of all tiles
    public List<Tile> getTiles () {
        return getTiles(t -> true);
    }
    public List<Tile> getTiles (Predicate<Tile> tilePredicate) {
        List<Tile> tiles = new ArrayList<>();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Tile t = getTile(x, y);
                if (tilePredicate.test(t)) tiles.add(t);
            }
        }
        return tiles;
    }

    /**
     * Get a list of tiles adjacent to a given tile
     */
    public List<Tile> getNeighborTiles (Tile tile) {
       return getNeighborTiles(tile, t -> true);
    }
    public List<Tile> getNeighborTiles (Tile tile, Predicate<Tile> tilePredicate) {
        List<Tile> neighborTiles = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (j == 0 && i == 0) continue;
                Tile t = getTile(tile.getX() + i, tile.getY() + j);
                if (t != null && tilePredicate.test(t)) {
                    neighborTiles.add(t);
                }
            }
        }
        return neighborTiles;
    }

    public int countNeighborBombs(Tile tile) {
        int counter = 0;
        for (Tile t : getNeighborTiles(tile)) {
            if (t.isBomb()) counter++;
        }
        return counter;
    }
    public int countNeighborFlags(Tile tile) {
        int counter = 0;
        for (Tile t : getNeighborTiles(tile)) {
            if (t.hasFlag()) counter++;
        }
        return counter;
    }
    //endregion


    //region Accessors
    public int getW () { return w; }
    public int getH () { return h; }
    public int getBombs () { return bombCount; }
    public Difficulty getDifficulty () { return new Difficulty(w, h, bombCount); }
    public int countUndiscoveredTiles() { return undiscoveredTiles; }
    public int countDiscoveredTiles () { return getMapSize() - undiscoveredTiles; }
    public int getFlags () { return flags; }
    public int getMapSize () {
        return w*h;
    }
    public boolean isBombsGenerated () { return bombsGenerated; }
    public boolean isPlaying() { return playing; }
    public boolean isGameLost () {
        return gameLost;
    }
    //endregion

    //region Events
    protected EventHandler<Event> onTileChangeEvent, onGameLost, onGameWon, redrawGameEvent;

    public void setOnTileChangeEvent (EventHandler<Event> onTileChangeEvent) {
        this.onTileChangeEvent = onTileChangeEvent;
    }
    public void setOnGameWon (EventHandler<Event> onGameWon) {
        this.onGameWon = onGameWon;
    }
    public void setOnGameLost (EventHandler<Event> onGameLost) {
        this.onGameLost = onGameLost;
    }
    public void setRedrawGameEvent(EventHandler<Event> redrawGameEvent) {
        this.redrawGameEvent = redrawGameEvent;
    }
    public void onGameClose () { }

    //endregion
}
