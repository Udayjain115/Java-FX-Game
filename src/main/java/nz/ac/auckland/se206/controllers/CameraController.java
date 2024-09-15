package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;

public class CameraController {
  @FXML Rectangle forward;
  @FXML Rectangle back;
  @FXML Rectangle exit;
  @FXML ImageView twocam;
  @FXML ImageView onecam;
  @FXML ImageView twelvecam;
  @FXML ImageView elevencam;
  @FXML ImageView tencam;

  private int count = 5;

  
  public void initialize(){
    forward.setDisable(true);
  }

  public void onExit() throws IOException{
    App.setRoot("crimeScene");
  }

  public void onGoBack(){
    if(count == 5){
      twocam.setVisible(false);
      forward.setDisable(false);
      count--;
      return;
    }else if(count == 4){
      onecam.setVisible(false);
      count--;
      return;
    }else if(count == 3){
      twelvecam.setVisible(false);
      count--;
      return;
    }else if(count == 2){
      elevencam.setVisible(false);
      count--;
      back.setDisable(true);
    }
    
  }


}
