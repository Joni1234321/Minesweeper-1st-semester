package controllers;

import controllers.menu.FrontPageController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * A controller for the entire menu
 *
 * @author Alexander
 */

public class MenuController {

    private final Scene scene;

    public MenuController () {

        scene = new Scene(new StackPane(),640, 480);
        scene.setOnKeyPressed(keyEventHandler);
        resetMenu();
    }

    /**
     * Loads page from resources and displays it
     * @param pageName The name of the file under menu in resources
     */
    public void setPage (String pageName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/menu/" +pageName+".fxml"));
            scene.setRoot(loader.load());
        } catch (IOException ignore) {}
    }

    public Scene getScene () {
        return scene;
    }

    EventHandler<KeyEvent> keyEventHandler = e -> {
        if (e.getCode() == KeyCode.ESCAPE) {
            resetMenu();
        }
    };

    public void resetMenu() {
        setPage(FrontPageController.RESOURCE_NAME);
    }
}
