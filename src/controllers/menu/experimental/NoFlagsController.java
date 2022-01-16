package controllers.menu.experimental;

import controllers.menu.GameModeController;
import javafx.fxml.FXML;
import models.features.Difficulty;
import models.gamemodes.ClassicMinesweeper;
import models.gamemodes.GameMode;
import models.gamemodes.experimental.NoFlagsMinesweeper;

public class NoFlagsController extends GameModeController {

    public static String RESOURCE_NAME = "NoFlags";

    @FXML
    protected void startGameEvent () {
        Difficulty difficulty = getDifficulty();
        if (difficulty != null) {
            startGame(new NoFlagsMinesweeper(difficulty));
        }
    }

    @Override
    protected GameMode getGameMode() {
        return GameMode.NO_FLAGS;
    }
}
