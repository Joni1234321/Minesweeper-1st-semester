package models;

/**
 * The object of a single tile
 */

public class Tile {
     private final int x, y;
     private boolean bomb = false;
     private boolean discovered = false;
     private boolean flag = false;

     private int neighbors = 0;

     public Tile(int x, int y){
         this.x = x;
         this.y = y;
     }

    // Coordinates
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    // Bombs
    public void setBomb () {
         bomb = true;
    }
    public void removeBomb () {
        bomb = false;
    }
    public boolean isBomb () {
        return bomb;
    }

    // Discovered
    public boolean isDiscovered () { return discovered; }
    public void setDiscovered () { this.discovered = true; }

    // Flags
    public boolean hasFlag () { return flag; }
    public void setFlag () { flag = true; }
    public void clearFlag () { flag = false; }

    // Neighbor Bomb cache
    public int countNeighborBombs () { return this.neighbors; }
    public void setNeighbors (int n) { this.neighbors = n; }
}
