package controllers.menu;

import javafx.event.ActionEvent;
import models.Minesweeper;
import models.features.Difficulty;
import models.gamemodes.GameMode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import runnables.Main;

import java.util.function.Predicate;

public abstract class GameModeController {

    @FXML
    protected ComboBox<Difficulty> comboDifficulty;

    @FXML
    protected HBox customToggle;

    @FXML
    protected TextField inpHeight;

    @FXML
    protected TextField inpWidth;

    @FXML
    protected TextField inpBombCount;

    protected MenuTextField width, height, bombCount;

    public void initialize () {
        comboDifficulty.getItems().setAll(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.CUSTOM);
        comboDifficulty.setValue(Difficulty.EASY);
        comboDifficulty.setOnAction(this::updateDifficultyEvent);
        this.width = new MenuTextField(inpWidth, "Size must be 4 - 100", isValidLength);
        this.height = new MenuTextField(inpHeight, "Size must be 4 - 100", isValidLength);
        this.bombCount = new MenuTextField(inpBombCount, "Bombs must be above 0", isValidBombCount);
    }

    protected abstract GameMode getGameMode ();

    protected void startGame (Minesweeper minesweeper) {
        Main.stage.setScene(Main.boardScene);
        Main.boardController.setGameMode(getGameMode());
        Main.boardController.startGame(minesweeper);
    }

    /**
     * A class used to easily validate text fields and display an error message
     *
     * @author Alexander
     */
    public static class MenuTextField {
        private static final String CSS_BORDER_GREEN = "-fx-text-box-border: green; -fx-focus-color: green";
        private static final String CSS_BORDER_RED = "-fx-text-box-border: red; -fx-focus-color: red";

        public final TextField textField;
        private final Predicate<String> predicate;
        private final String errorText;

        public MenuTextField (TextField textField, String errorText, Predicate<String> predicate) {
            this.textField = textField;
            this.errorText = errorText;
            this.predicate = predicate;
        }

        public boolean check () {
            if (predicate.test(textField.getText())) {
                textField.setStyle(CSS_BORDER_GREEN);
                return true;
            } else {
                textField.setText("");
                textField.setPromptText(errorText);
                textField.setStyle(CSS_BORDER_RED);
                return false;
            }
        }

        public String getText () {
            return textField.getText();
        }
    }

    //region Predicates
    protected final static int MIN_LENGTH = 4, MAX_LENGTH = 100, MIN_BOMBS = 1;

    Predicate<String> isValidLength = s -> {
        if (!isInt(s)) return false;
        int n = Integer.parseInt(s);
        return MIN_LENGTH <= n && n <= MAX_LENGTH;
    };

    Predicate<String> isValidBombCount = s -> {
        if (!isInt(s)) return false;
        int n = Integer.parseInt(s);
        return n >= MIN_BOMBS;
    };
    //endregion

    //region MinesweeperSize

    protected final boolean isInt (String s){
        try{
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    protected Difficulty getDifficulty () {
        if (comboDifficulty.getValue() == Difficulty.CUSTOM) {
            if (width.check() & height.check() & bombCount.check()) {
                return new Difficulty(
                        Integer.parseInt(width.getText()),
                        Integer.parseInt(height.getText()),
                        Integer.parseInt(bombCount.getText()));
            } else {
                return null;
            }
        } else {
            return comboDifficulty.getValue();
        }
    }

    private void updateDifficultyEvent (ActionEvent event) {
        customToggle.setVisible(comboDifficulty.getValue() == Difficulty.CUSTOM);
    }
    //endregion

}
