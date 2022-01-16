package controllers.features;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import models.Minesweeper;
import models.features.Difficulty;
import models.features.Highscore;
import runnables.Main;

import java.util.ArrayList;

/**
 * A controller for the Highscore popup
 *
 * @author Alexander
 */

public class HighscoreController {

    @FXML
    private ComboBox<Difficulty> comboDifficulty;

    @FXML
    private VBox scoresList;

    public void initialize () {
        comboDifficulty.getItems().setAll(Highscore.listDifficulties());
        Minesweeper minesweeper = Main.boardController.getMinesweeper();
        if (minesweeper != null && comboDifficulty.getItems().contains(minesweeper.getDifficulty())) {
            comboDifficulty.setValue(minesweeper.getDifficulty());
            updateDifficulty();
        }
    }

    @FXML
    private void updateDifficulty () {
        Difficulty difficulty = comboDifficulty.getValue();

        scoresList.getChildren().clear();

        ArrayList<Integer> bestScore = Highscore.findScores(difficulty.getW() + "*" + difficulty.getH() + "*" + difficulty.getBombCount());

        if (bestScore != null) {
            for (int i = 0; i < bestScore.size(); i++) {
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER);
                hbox.setSpacing(20);

                Label number = new Label("" + (i+1));
                number.setFont(new Font(18));
                number.setStyle("-fx-font-weight: bold;");
                number.setAlignment(Pos.CENTER_LEFT);
                number.setPrefWidth(80);

                Label time = new Label(TimerController.timeToString(bestScore.get(i)));
                time.setFont(new Font(18));
                time.setAlignment(Pos.CENTER_RIGHT);
                time.setPrefWidth(80);

                hbox.getChildren().setAll(number, time);
                scoresList.getChildren().add(hbox);
            }
        }
    }



}
