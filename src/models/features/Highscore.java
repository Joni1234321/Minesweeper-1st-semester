package models.features;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Highscore class has control of how the scores are handled
 *
 * @author Heini
 */

public class Highscore {


    public static void newFile() throws IOException {

        boolean success;

        File directory = new File(getPathFolder());
        if (!directory.exists()) {
            success = directory.mkdir();

            if (!success) {
                System.out.println("Something went wrong in the making of the folder for the highscore.");
            }
        }


        File f = new File (getPathFile());

        if (!f.exists()) {
            success = f.createNewFile();

            if (!success){
                System.out.println("Something went wrong in the making of the file for the highscore.");
            }
        }
    }

    /**
     * Checks if the score you got is a new highscore and
     * if it is, add the score where it belongs.
     *
     * @param score New score
     * @param diff Difficulty of the game
     */
    public static void newHighscore(int score, Difficulty diff) {

        String StringHolder = readFile();

        String sizeAndBombs = diff.getW() + "*" + diff.getH() + "*" + diff.getBombCount();

        ArrayList<Integer> oldScores = findScores(sizeAndBombs);

        String newScore = addNewScore(oldScores, score, sizeAndBombs);

        if (oldScores == null) {
            StringHolder += "\n" + newScore;
        }   else {

            assert StringHolder != null;
            String a = StringHolder.substring(0,StringHolder.indexOf(sizeAndBombs));
            String b = StringHolder.substring(StringHolder.indexOf("n",StringHolder.indexOf(sizeAndBombs))+1);

            StringHolder = a + newScore + b;
        }

        try {
            FileWriter writer = new FileWriter(getPathFile());
            writer.append(StringHolder);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in writing to highscore file");
        }
    }

    public static ArrayList<Difficulty> listDifficulties () {
        ArrayList<Difficulty> list = new ArrayList<>();

        Scanner sc;

        try{
            sc = new Scanner(new File(getPathFile()));
        } catch (Exception e) {
            return list;
        }

        String buffer;
        while (sc.hasNextLine()) {
            buffer = sc.nextLine();
            if (!buffer.equals("")){
                try {
                    String difficultyString = buffer.substring(0,buffer.indexOf(","));
                    String[] values = difficultyString.split("\\*");
                    int w = Integer.parseInt(values[0]);
                    int h = Integer.parseInt(values[1]);
                    int bombCount = Integer.parseInt(values[2]);
                    list.add(new Difficulty(w, h, bombCount));
                } catch (Exception ignore) {}
            }
        }
        sc.close();

        Collections.sort(list);

        return list;
    }


    public static ArrayList<Integer> findScores(String sizeAndBombs) {


        String temp = readLineFromFile(sizeAndBombs);

        if (temp == null) {
            return null;
        }

        ArrayList<Integer> scores = new ArrayList<>();

        int last = temp.indexOf(",")+1;
            for (int i = 0; i < 10; i++) {
                scores.add(Integer.parseInt(temp.substring(last , temp.indexOf(",", last ))));

                last = temp.indexOf(",", last + 1) + 1;
                if (temp.indexOf(",", last +1) == -1 ){
                    break;
                }
            }
       return scores;
    }


    public static String addNewScore (ArrayList<Integer> list, int score, String sizeAndBombs){

        if (list == null) {
            list = new ArrayList<>();
        }

        list.add(score);
        Collections.sort(list);

        if (list.size() > 10) {
            list.remove(10);
        }

        StringBuilder newscore = new StringBuilder();

        newscore.append(sizeAndBombs).append(",");

        for (int n : list) {
            newscore.append(n).append(",");
        }
        newscore.append("n");
        return newscore.toString();
    }

    /**
     * Returns the line of the save file corresponding to the chosen difficulty
     *
     * @param sizeAndBombs String describing difficulty used in save files
     * @return Line with highscores for this difficulty
     */
    private static String readLineFromFile(String sizeAndBombs) {

        Scanner sc;

        try{
            sc = new Scanner(new File(getPathFile()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String line = null;
        String buffer;
        while (sc.hasNextLine() && line == null ) {
            buffer = sc.nextLine();
            if (!buffer.equals("")){
                if (buffer.substring(0,buffer.indexOf(",")).equals(sizeAndBombs)) {
                    line = buffer;
            }
            }
        }
        sc.close();

        return line;
    }

    /**
     * Reads the whole save file as a single string
     * @return Entire save file as string
     */
    private static String readFile() {

        Scanner sc;

        try{
            sc = new Scanner(new File(getPathFile()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder buffer = new StringBuilder();

        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()).append("\n");
        }

        sc.close();

        return buffer.toString();
    }

    private static String getPathFile(){
        return getPathFolder() + "/Highscore.txt";
    }

    public static String getPathFolder(){
        return System.getProperty("user.dir") + "/Minesweeper";
    }

}
