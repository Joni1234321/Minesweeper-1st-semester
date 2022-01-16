package controllers.menu;

import controllers.menu.experimental.MovingController;
import controllers.menu.experimental.NoFlagsController;
import controllers.menu.experimental.ScenarioController;
import javafx.fxml.FXML;
import runnables.Main;

public class FrontPageController {

    public static String RESOURCE_NAME = "FrontPage";

    @FXML
    void onPressClassic() {
        Main.menuController.setPage(ClassicController.RESOURCE_NAME);
    }

    @FXML
    void onPressMultiplayer() {
        Main.menuController.setPage(MultiplayerController.RESOURCE_NAME);
    }

    @FXML
    void onPressNoFlags() {
        Main.menuController.setPage(NoFlagsController.RESOURCE_NAME);
    }

    @FXML
    void onPressMoving() {
        Main.menuController.setPage(MovingController.RESOURCE_NAME);
    }

    @FXML
    void onPressScenario() { Main.menuController.setPage(ScenarioController.RESOURCE_NAME); }
}
