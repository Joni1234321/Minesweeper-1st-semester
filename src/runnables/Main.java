package runnables;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controllers.BoardController;
import controllers.MenuController;

import java.io.IOException;

public class Main extends Application {

    public static Stage stage;

    public static Scene menuScene;
    public static Scene boardScene;

    public static BoardController boardController;
    public static MenuController menuController;

    @Override
    public void start(Stage stage) throws IOException {
        Main.stage = stage;

        menuController = new MenuController();

        FXMLLoader boardLoader = new FXMLLoader(getClass().getResource("/resources/Board.fxml"));
        Parent boardView = boardLoader.load();
        boardController = boardLoader.getController();

        menuScene = menuController.getScene();
        boardScene = new Scene(boardView, 640, 480);

        stage.setTitle("Minesweeper");
        stage.setScene(menuScene);
        stage.setMinHeight(400);
        stage.setMinWidth(400);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
