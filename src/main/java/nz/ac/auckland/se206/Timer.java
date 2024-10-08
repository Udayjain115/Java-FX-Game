package nz.ac.auckland.se206;

import java.io.File;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * This class is the timer for the game. It is a singleton class that keeps track of the time left
 * in the game. It has a start method that starts the timer, a stop method that stops the timer, a
 * reset method that resets the timer, and a getTimeLeft method that returns the time left.
 */
public class Timer {
  private static Timer timer;

  /**
   * Returns the timer instance.
   *
   * @return the timer instance
   */
  public static Timer getTimer() {
    if (timer == null) {
      timer = new Timer();
    }
    return timer;
  }

  private Timeline timeline;
  private IntegerProperty time;
  private Boolean reachedZero;
  private Boolean hasReset = false;
  private int number = 300;

  /** Constructs a new Timer object. */
  private Timer() {
    reachedZero = false;
    time = new SimpleIntegerProperty(10000);
    timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                e -> {
                  if (time.get() > 0) {
                    time.set(time.get() - 1);
                    if (time.get() == 120) {
                      String soundFile =
                          "src/main/resources/sounds/ttsmaker-file-2024-10-7-17-10-55.mp3";

                      Media sound = new Media(new File(soundFile).toURI().toString());
                      MediaPlayer mediaPlayer = new MediaPlayer(sound);
                      mediaPlayer.play();
                    }
                  } else {
                    if (!reachedZero && hasReset) {
                      timeline.stop();
                      reachedZero = true;
                      try {
                        if (number == 300) {
                          App.setRoot("guessing");
                        }
                      } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                      }
                    }
                  }
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  /** Starts the timer. */
  public void start() {
    if (timeline != null) {
      timeline.play();
    }
  }

  /** Stops the timer. */
  public void stop() {
    if (timeline != null) {
      timeline.stop();
    }
  }

  /**
   * Resets the timer to the specified number.
   *
   * @param number the number to reset the timer to
   */
  public void reset(int number) {
    this.number = number;
    hasReset = true;
    reachedZero = false;
    time.set(number);
    this.number = number;
  }

  /**
   * Returns the time left.
   *
   * @return the time left
   */
  public IntegerProperty getTimeLeft() {
    return time;
  }
}
