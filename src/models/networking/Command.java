package models.networking;

import models.Minesweeper;
import models.features.Difficulty;
import models.gamemodes.NetworkingMinesweeper;
import models.Tile;

/**
 * A class for handling commands to be sent between server and client
 *
 * @author Jonas
 */

public class Command {
    public final String function;
    private final String data;

    private Command (String function, String data) {
        this.function = function;
        this.data = data;
    }

    public static Command split (String command) {
        String[] cmd = command.split("-");
        if (cmd.length != 2) return null;
        return new Command(cmd[0],  cmd[1]);
    }

    public String getData () {
        return data;
    }
    public int[] getIntegers (){
        String[] split = data.split(",");
        int[] coord = new int[split.length];
        for (int i = 0; i < coord.length; i++)
            coord[i] = Integer.parseInt(split[i]);
        return coord;
    }

    public static void applyToMinesweeper(NetworkingMinesweeper minesweeper, String line){
        applyToMinesweeper(minesweeper, Command.split(line));
    }
    public static void applyToMinesweeper(NetworkingMinesweeper minesweeper, Command command) {
        if (command == null) return;

        // Left click tile
        if (command.function.equals("t")) {
            int[] coord = command.getIntegers();

            if (coord.length != 2) return;
            Tile t = minesweeper.getTile(coord[0], coord[1]);

            if (t == null) return;
            minesweeper.applyLeftClick(t);
        }
        // Flag
        else if (command.function.charAt(0) == 'f') {
            int[] coord = command.getIntegers();

            if (coord.length != 2) return;
            Tile t = minesweeper.getTile(coord[0], coord[1]);

            if (t == null) return;

            if (command.function.charAt(1) == '1') minesweeper.applySetFlag(t);
            else if (command.function.charAt(1) == '0') minesweeper.applyClearFlag(t);
        }
        // Map
        else if (command.function.equals("m")) {
            minesweeper.applyLoad(command.data);
        }
        // new Map
        else if (command.function.equals("r")) {
            int[] values = command.getIntegers();
            if (values.length != 3) return;
            minesweeper.applyResetGame(values[0], values[1], values[2]);
        }
    }

    public static boolean shouldBroadcast(String cmd) {
        Command command = split(cmd);
        return command != null;
    }

    public static String minesweeperToString (Minesweeper minesweeper) {
        return "m-" + minesweeper.toString();
    }
    public static String minesweeperToString (String loadString) { return "m-" + loadString; }

    public static String tileClickToString ( Tile tile) {
        return "t-" + tile.getX() + "," + tile.getY();
    }
    public static String setFlagToString(Tile tile) {
        return "f1-" + tile.getX() + "," + tile.getY();
    }
    public static String clearFlagToString (Tile tile) {
        return "f0-" + tile.getX() + "," + tile.getY();
    }
    public static String createNewGameToString (Minesweeper minesweeper) { return "r-" + minesweeper.getW() + "," + minesweeper.getH() + "," + minesweeper.getBombs(); }
    public static String createNewGameToString (Difficulty diff) { return "r-" + diff.getW() + "," + diff.getH() + "," + diff.getBombCount(); }

}
