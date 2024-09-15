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
  @FXML ImageView twocamera;
  @FXML ImageView onecamera;
  @FXML ImageView twelvecamera;
  @FXML ImageView elevencamera;
  @FXML ImageView tencamera;

  
  public void initialize(){
    forward.setDisable(true);
  }

  public void onExit() throws IOException{
    App.setRoot("crimeScene");
  }


}
