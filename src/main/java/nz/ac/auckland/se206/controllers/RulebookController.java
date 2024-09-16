package nz.ac.auckland.se206.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class RulebookController {
  ;

  @FXML private ImageView dustImage;

  public void dustClickedOn() {
    System.out.println("I'm being clicked on");
    FadeTransition ft = new FadeTransition(Duration.millis(1000), dustImage);
    ft.setFromValue(1.0);
    ft.setToValue(0.0);
    ft.play();
  }
}
