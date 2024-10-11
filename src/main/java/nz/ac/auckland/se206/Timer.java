package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

/**
 * This class is the timer for the game. It is a singleton class that keeps track of the time left
 * in the game. It has a start method that starts the timer, a stop method that stops the timer, a
 * reset method that resets the timer, and a getTimeLeft method that returns the time left.
 */
public class Timer {
  private static Timer timer;

  /**
   * Returns the timer instance. If the timer instance is null, a new timer instance is created.
   * This is to ensure that there is only one timer instance.
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

  /**
   * Starts the timer. So that the time decreases by 1 every second. This is as the user has started
   * the game.
   */
  public void start() {
    if (timeline != null) {
      timeline.play();
    }
  }

  /**
   * Stops the timer. This is as the user has stopped the game. The time will not decrease anymore.
   */
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
   * Returns the time left. This is used to display the time left in the game. It is a property so
   * that it can be bound to a label. This is so that the label updates every second.
   *
   * @return the time left
   */
  public IntegerProperty getTimeLeft() {
    return time;
  }
}
