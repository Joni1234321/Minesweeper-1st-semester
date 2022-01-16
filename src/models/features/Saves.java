package models.features;

import models.Minesweeper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *  Saves class has control over how saves are handled
 *
 *  @author Heini
 */

public class Saves {

    public static void createSave(Minesweeper bord) throws IOException {
        FileWriter writer = new FileWriter(getFilePath());
        writer.write(bord.toString());
        writer.close();
    }
    
    public static String loadSave() throws FileNotFoundException {
        Scanner sc = new Scanner(new File(getFilePath()));
        
        StringBuilder save = new StringBuilder();

        while (sc.hasNextLine()){
            save.append(sc.nextLine());
        }

        return save.toString();
    }

    public static String getFilePath () {
        return Highscore.getPathFolder() + "/save.txt";
    }

    public static void newFile() throws IOException {
        boolean success;

        File f = new File(getFilePath());

        if (!f.exists()) {
            success = f.createNewFile();

            if (!success) {
                System.out.println("something went wrong in the making of the save file.");
            }
        }

    }
}






