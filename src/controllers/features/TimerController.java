package controllers.features;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * A controller for timing games in Classic mode
 * The timer is kept entirely separate from the Minesweeper model
 *
 * @author Alexander
 */

public class TimerController {
    private final Label label;

    // Counting in seconds
    private int time = 0;
    private Timeline timeline;

    public TimerController(Label label) {
        this.label = label;
    }

    public void start() {
        stop();
        reset();

        timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> incrementTimer()));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void reset() {
        setTime(0);
    }
    public void stop () {
        if (timeline != null) timeline.stop();
    }


    private void incrementTimer() {
        setTime(++time);
    }

    public static String timeToString (int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d", minutes)+":"+String.format("%02d", seconds);
    }

    private void setTime(int time) {
        this.time = time;
        label.setText(timeToString(time));
    }
    public int getTime() {
        return this.time;
    }
    public boolean isStarted () {
        return timeline != null && timeline.getStatus() == Animation.Status.RUNNING;
    }
}
