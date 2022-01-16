package controllers.menu;

import models.features.Difficulty;
import models.gamemodes.ClientMinesweeper;
import models.gamemodes.GameMode;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MultiplayerController extends GameModeController {

    public static String RESOURCE_NAME = "Multiplayer";

    @FXML
    private TextField inpIp;

    private MenuTextField ip;

    public void initialize() {
        super.initialize();

        ip = new MenuTextField(inpIp, "You need to enter a valid IP", s -> s.length() > 0);
    }

    @Override
    protected GameMode getGameMode () {
        return GameMode.MULTIPLAYER;
    }

    //region Events
    @FXML
    private void joinGameEvent(){
        Difficulty difficulty = getDifficulty();
        String ip = getIp();

        startGame(new ClientMinesweeper(difficulty, ip));
    }
    //endregion

    //region Validation
    protected String getIp () {
        if (ip.check()) {
            return ip.getText();
        }
        return null;
    }


    //endregion
    //endregion
}
