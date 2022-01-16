package controllers.menu.experimental;


import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import controllers.menu.GameModeController;
import models.features.Difficulty;
import models.gamemodes.GameMode;
import models.gamemodes.experimental.ScenarioMinesweeper;

public class ScenarioController extends GameModeController {

    public static String RESOURCE_NAME = "Scenario";

    @FXML
    private Slider progress;

    @FXML
    private Label progressLabel;

    @FXML
    private CheckBox mistakes;

    @FXML
    private CheckBox bound;

    @FXML
    protected final void startGameEvent () {
        Difficulty difficulty = getDifficulty();
        boolean doMistakes = mistakes.isSelected();
        boolean doBound = bound.isSelected();
        double progressValue = progress.valueProperty().intValue() * 0.01d;

        if (difficulty != null) {
            startGame(new ScenarioMinesweeper(difficulty, progressValue, doMistakes, doBound));
        }
    }

    public void reset () {
        comboDifficulty.setValue(Difficulty.EASY);
    }

    protected GameMode getGameMode() {
        return GameMode.SCENARIO;
    }


    @Override
    public void initialize() {
        super.initialize();
        progressLabel.textProperty().bind(Bindings.format(
                "%.0f",
                progress.valueProperty()
        ).concat("%"));
    }
}
