package nz.ac.auckland.se206;

import java.io.IOException;


import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import nz.ac.auckland.se206.speech.TextToSpeech;



public class Timer {
  private static Timer timer;
  private Timeline timeline;
  private IntegerProperty time;

  private Timer(){
    time = new SimpleIntegerProperty(300);
    timeline = new Timeline(new KeyFrame(Duration.seconds(1), e ->{
      if(time.get() > 0){
        time.set(time.get() - 1);
      } else {
        timeline.stop();
      }
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  public static Timer getTimer(){
    if(timer == null){
      timer = new Timer();
    }
    return timer;
  }

  public void start(){
    if(timeline != null){
      timeline.play();
    }
  }

  public void stop(){
    if(timeline != null){
      timeline.stop();
    }
  }

  public IntegerProperty getTimeLeft(){
    return time;
  }
}
