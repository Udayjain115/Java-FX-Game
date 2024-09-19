package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

public class Timer {
  private static Timer timer;

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

  public void start() {
    if (timeline != null) {
      timeline.play();
    }
  }

  public void stop() {
    if (timeline != null) {
      timeline.stop();
    }
  }

  public void reset(int number) {
    this.number = number;
    hasReset = true;
    reachedZero = false;
    time.set(number);
    this.number = number;
  }

  public IntegerProperty getTimeLeft() {
    return time;
  }
}
