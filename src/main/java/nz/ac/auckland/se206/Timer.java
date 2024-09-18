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
  private Boolean reachedZero;
  private Boolean hasReset = false;

  private Timer(){
    reachedZero = false;
    time = new SimpleIntegerProperty(300);
    timeline = new Timeline(new KeyFrame(Duration.seconds(1), e ->{
      if(time.get() > 0){
        time.set(time.get() - 1);
      } else {
        if(!reachedZero && hasReset){
          timeline.stop();
          reachedZero = true;
          try {
            App.setRoot("start");
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        }
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

  public void reset(){
    hasReset = true;
    time.set(300);
  }

  public IntegerProperty getTimeLeft(){
    return time;
  }
}
