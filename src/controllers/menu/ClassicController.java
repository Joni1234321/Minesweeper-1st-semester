package controllers.menu;

import models.features.Difficulty;
import models.gamemodes.ClassicMinesweeper;
import models.gamemodes.GameMode;
import javafx.fxml.FXML;
import runnables.Main;

public class ClassicController extends GameModeController {

    public static String RESOURCE_NAME = "Classic";

    public void initialize () {
        super.initialize();
    }

    @Override
    protected GameMode getGameMode () {
        return GameMode.CLASSIC;
    }

    //region Events
    @FXML
    protected void startGameEvent () {
        Difficulty difficulty = getDifficulty();
        if (difficulty != null) {
            startGame(new ClassicMinesweeper(difficulty));
        }
    }

    @FXML
    protected void loadGameEvent () {
        Difficulty difficulty = getDifficulty();
        if (difficulty != null) {
            startGame(new ClassicMinesweeper(difficulty));
        }
        Main.boardController.loadGameEvent();
    }

    @FXML
    protected final void highscoreEvent () {
        Main.boardController.highscoreEvent();
    }

    //endregion
}
