package controllers.menu.experimental;

import controllers.menu.ClassicController;
import controllers.menu.GameModeController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import models.features.Difficulty;
import models.gamemodes.ClassicMinesweeper;
import models.gamemodes.GameMode;
import models.gamemodes.experimental.MovingMinesweeper;

public class MovingController extends GameModeController {

    public static String RESOURCE_NAME = "Moving";

    @FXML
    private CheckBox limitMovement;
    @FXML
    private CheckBox cantEscapeFlag;

    @FXML
    protected void startGameEvent() {
        Difficulty difficulty = getDifficulty();
        boolean doLimitMovement = limitMovement.isSelected();
        boolean doCantEscapeFlag = cantEscapeFlag.isSelected();

        if (difficulty != null) {
            startGame(new MovingMinesweeper(difficulty, doLimitMovement, doCantEscapeFlag));
        }
    }

    @Override
    protected GameMode getGameMode() {
        return GameMode.MOVING;
    }
}
