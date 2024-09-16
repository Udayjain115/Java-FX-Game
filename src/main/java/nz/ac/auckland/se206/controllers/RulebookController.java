package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;

public class RulebookController {

  @FXML private ImageView dustImage;
  @FXML private ImageView dustImage2;

  public void dustClickedOn() {
    FadeTransition ft = new FadeTransition(Duration.millis(1000), dustImage);
    ft.setFromValue(1.0);
    ft.setToValue(0.0);
    ft.play();
  }

  public void dustClickedOn2() {
    FadeTransition ft2 = new FadeTransition(Duration.millis(1000), dustImage2);
    ft2.setFromValue(1.0);
    ft2.setToValue(0.0);
    ft2.play();
  }

  /**
   * Handles the event when the close button (X) is clicked.
   *
   * @throws IOException
   */
  @FXML
  public void closeRulebook() throws IOException {
    App.setRoot("crimeScene");
  }
}
