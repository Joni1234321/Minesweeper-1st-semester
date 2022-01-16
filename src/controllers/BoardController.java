package controllers;

import javafx.scene.input.KeyCode;
import models.gamemodes.*;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Minesweeper;
import models.Tile;
import controllers.features.TimerController;
import models.features.Highscore;
import models.features.Saves;
import runnables.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * A controller for the Minesweeper board
 *
 * @author Alexander, Jonas, Heini
 */

public class BoardController {

    // DEBUG
    private static final boolean SHOW_ALL_TILES = false;

    private static final double TILE_GAP = 3.0;
    private static final String CSS_COLOR_RED = "#D62828";

    private boolean useTimer;

    private Image bombImage;
    private Image flagImage;
    private Image clockImage;

    @FXML
    private StackPane boardStackPane;

    @FXML
    private GridPane boardGridPane;

    @FXML
    private ImageView clockImageGoesHere;

    @FXML
    private Label timerLabel;
    TimerController timer;

    @FXML
    private ImageView remainingFlagImage;

    @FXML
    private Label remainingFlagLabel;


    // Model
    private GameMode gameMode = GameMode.CLASSIC;
    private Minesweeper minesweeper;
    public Minesweeper getMinesweeper () { return minesweeper; }

    //region Game Modes
    public void setGameMode (GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Starts a new game from the given minesweeper object
     */
    public void startGame (Minesweeper minesweeper) {
        this.minesweeper = minesweeper;
        setEvents();

        timer.stop();
        timer.reset();
        useTimer = gameMode == GameMode.CLASSIC;

        minesweeper.startGame();
        drawBoard();
    }

    /**
     * Restarts game in the current game mode
     */
    public void resetGame () {
        timer.stop();
        timer.reset();
        useTimer = gameMode == GameMode.CLASSIC;

        minesweeper.resetGame();
        drawBoard();
    }
    //endregion
    //region Minesweeper Events (Called by Minesweeper instance)
    EventHandler<Event> onTileChangeEvent = e -> Platform.runLater(this::drawChangedTiles);
    EventHandler<Event> onGameLost = e -> Platform.runLater(() -> endOfGame(false));
    EventHandler<Event> onGameWon = e -> Platform.runLater(() -> endOfGame(true));
    EventHandler<Event> redrawGameEvent = e -> Platform.runLater(this::drawBoard);
    private void setEvents () {
        minesweeper.setOnTileChangeEvent(onTileChangeEvent);
        minesweeper.setOnGameLost(onGameLost);
        minesweeper.setOnGameWon(onGameWon);
        minesweeper.setRedrawGameEvent(redrawGameEvent);
    }

    /**
     * End of game event has been reached
     *
     * @param won If the game was won or lost
     */
    private void endOfGame (boolean won) {
        minesweeper.end();
        timer.stop();

        if (won && useTimer) Highscore.newHighscore(timer.getTime(), minesweeper.getDifficulty());

        for (Tile t : minesweeper.getTiles()) {
            // Highlight every mistake
            if (!t.isBomb() &&  t.hasFlag() && !won) getTileButton(t).setStyle("-fx-background-color: " + CSS_COLOR_RED);
            if ( t.isBomb() && !t.hasFlag()) getTileButton(t).setImage(won ? flagImage : bombImage);
        }
        if (won) updateFlagCount();

        gameOverAlertBox(won);
    }
    //endregion

    // View
    //region Initialize

    /**
     * Initialize FX events and data files
     *
     * @throws IOException If the making of highscores or saves fails
     */
    public void initialize () throws IOException {
        InputStream bombInputStream = getClass().getResourceAsStream("/resources/bomb.png");
        bombImage = new Image(bombInputStream);

        InputStream flagInputStream = getClass().getResourceAsStream("/resources/flag.png");
        flagImage = new Image(flagInputStream);

        InputStream clockInputStream = getClass().getResourceAsStream("/resources/clockTime.png");
        clockImage = new Image(clockInputStream);

        Highscore.newFile();
        Saves.newFile();

        Main.stage.setOnCloseRequest(e -> { if (minesweeper != null) minesweeper.onGameClose(); });
        boardStackPane.widthProperty().addListener((observableValue, number, t1) -> setBoardSize());
        boardStackPane.heightProperty().addListener((observableValue, number, t1) -> setBoardSize());

        timer = new TimerController(timerLabel);
        remainingFlagImage.setImage(flagImage);
        clockImageGoesHere.setImage(clockImage);
    }
    //endregion
    //region FXML Events
    @FXML
    private void resetGameEvent() {
        resetGame();
    }

    @FXML
    private void quitGameEvent() {
        minesweeper.onGameClose();
        Platform.exit();
    }

    @FXML
    private void backToMenuEvent () {
        minesweeper.onGameClose();
        minesweeper = null;
        timer.stop();
        Main.menuController.resetMenu();
        Main.stage.setScene(Main.menuScene);
    }

    @FXML
    private void saveGameEvent() throws IOException {
        if (minesweeper.isPlaying() && minesweeper.isBombsGenerated()) {
            Saves.createSave(minesweeper);
        }
    }

    @FXML
    public void loadGameEvent() {
        resetGame();
        try {
            minesweeper.load(Saves.loadSave());
            useTimer = false;
        } catch (FileNotFoundException|NoSuchElementException ignore) {}
    }
    //endregion
    //region RenderEvents (Called by JavaFX)
    EventHandler<MouseEvent> clickTileEvent = e -> {
        if (!minesweeper.isPlaying()) return;
        TileButton btn = (TileButton) e.getSource();
        Tile tile = minesweeper.getTile(btn.getX(), btn.getY());

        if (e.getButton() == MouseButton.PRIMARY) minesweeper.leftClickTile(tile);
        else if (e.getButton() == MouseButton.SECONDARY) minesweeper.rightClickTile(tile);

        if (useTimer) {
            if (minesweeper.isBombsGenerated() && !timer.isStarted()) timer.start();
        }
    };
    //endregion
    //region Draw

    /**
     * Fills in the board GridPane with buttons and gives them their appropriate look
     * To be called when a new game is started
     *
     * @author Alexander
     */
    public void drawBoard () {
        // Remove old buttons
        boardGridPane.getChildren().clear();
        boardGridPane.getColumnConstraints().clear();
        boardGridPane.getRowConstraints().clear();

        // Create buttons for new board
        for (int i = 0; i < minesweeper.getW(); i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            column.setPercentWidth(1./minesweeper.getW()*100.);
            boardGridPane.getColumnConstraints().add(column);
        }
        for (int i = 0; i < minesweeper.getH(); i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            row.setPercentHeight(1./minesweeper.getH()*100.);
            boardGridPane.getRowConstraints().add(row);
        }

        // Draw every button
        for (int y = 0; y < minesweeper.getH(); y++) {
            for (int x = 0; x < minesweeper.getW(); x++) {
                TileButton btn = new TileButton(x, y);
                btn.setOnMousePressed(clickTileEvent);
                btn.drawTile(minesweeper.getTile(x, y));
                boardGridPane.add(btn, x, y);
            }
        }

        setBoardSize();
        updateFlagCount();
    }

    /**
     * Updates flag counter and redraws changed tiles
     * Called by the Minesweeper object's onTileChangedEvent
     */
    private void drawChangedTiles() {
        updateFlagCount();
        while(!minesweeper.undrawnChanges.isEmpty()) {
            Tile t = minesweeper.undrawnChanges.poll();
            getTileButton(t).drawTile(t);
        }
    }

    /**
     * Updates FX board size when window size has changed
     *
     * @author Alexander
     */
    private void setBoardSize () {
        if (minesweeper == null) return;
        double tileSize = Math.min((boardStackPane.getHeight()+TILE_GAP) / minesweeper.getH() - TILE_GAP,
                (boardStackPane.getWidth()+TILE_GAP) / minesweeper.getW() - TILE_GAP);
        boardGridPane.setMaxHeight(minesweeper.getH()*(tileSize+TILE_GAP) - TILE_GAP);
        boardGridPane.setMaxWidth(minesweeper.getW()*(tileSize+TILE_GAP) - TILE_GAP);
    }

    @FXML
    public void highscoreEvent() {
        try {
            Stage popup = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Highscore.fxml"));
            Scene scene = new Scene(loader.load());
            scene.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ESCAPE) popup.close(); } );
            popup.setScene(scene);
            popup.setMinWidth(400);
            popup.setMinHeight(400);
            popup.initOwner(Main.stage);
            popup.initModality(Modality.APPLICATION_MODAL);

            popup.showAndWait();
        } catch (IOException ignore) {}
    }

    //endregion
    //region Custom UI
    private class TileButton extends Button {
        private final int x, y;
        private TileButton (int x, int y) {
            super();
            setMinHeight(20.);
            setMinWidth(20.);
            setMaxHeight(Double.POSITIVE_INFINITY);
            setMaxWidth(Double.POSITIVE_INFINITY);
            setPadding(new Insets(0,0,0,0));
            widthProperty().addListener((observableValue, number, t1) -> changeSize());
            heightProperty().addListener((observableValue, number, t1) -> changeSize());
            this.x = x;
            this.y = y;
        }

        private void drawTile (Tile tile) {
            setGraphic(null);
            setStyle("-fx-background-color: rgb(180,180,180);");
            setText("");
            if (tile == null) return;
            if (SHOW_ALL_TILES || tile.isDiscovered()) {
                if (tile.isBomb()) {
                    setStyle("-fx-background-color: " + CSS_COLOR_RED);
                    setImage(bombImage);
                } else if (tile.countNeighborBombs() == 0) {
                    setStyle("-fx-background-color: rgb(230, 230, 230)");
                } else {
                    setText("" + tile.countNeighborBombs());
                    int hue = 96 - 12*tile.countNeighborBombs();
                    setStyle("-fx-background-color: hsb("+hue+", 100%, 75%)");
                }
            } else if (tile.hasFlag()) {
                setImage(flagImage);
            }
        }
        private void changeSize () {
            ImageView imgView = (ImageView) getGraphic();
            if (imgView != null) {
                imgView.setFitWidth(getWidth());
                imgView.setFitHeight(getHeight());
            }
            setFont(new Font(Math.min(getWidth(),getHeight())/2));
        }
        private void setImage (Image image) {
            ImageView bombImageView = new ImageView(image);
            bombImageView.setFitWidth(getWidth());
            bombImageView.setFitHeight(getHeight());
            setGraphic(bombImageView);
        }

        private int getX () { return x; }
        private int getY () { return y; }
    }
    private TileButton getTileButton (Tile t) {
        return (TileButton) boardGridPane.getChildren().get(t.getX() + t.getY() * minesweeper.getW());
    }

    private Alert alert;

    /**
     * Displays Game Over alert box to user
     *
     * @author Mathias
     * @param won If the game was won
     */
    private void gameOverAlertBox(boolean won) {
        if (alert != null && alert.isShowing()) return;

        alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Game over!");
        alert.setHeaderText("You " + (won ? "won!" : "lost!"));
        alert.setContentText("Choose your option.");
        alert.setGraphic(new ImageView (won ? flagImage : bombImage));

        ButtonType restartButton = new ButtonType("Restart");
        ButtonType quitButton = new ButtonType("Close", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(restartButton, quitButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == restartButton) resetGameEvent();
    }
    //endregion

    /**
     * Updates flag counter on the bottom of the screen
     */
    private void updateFlagCount(){
        if (minesweeper.getFlags() > minesweeper.getBombs()){
            remainingFlagLabel.setStyle("-fx-text-fill: " + CSS_COLOR_RED);
        }else{
            remainingFlagLabel.setStyle("");
        }
        remainingFlagLabel.setText(minesweeper.getFlags() + "/" + minesweeper.getBombs());
    }
}
