package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.Timer;

public class StartScreenController {

  @FXML private Pane startPane;
  @FXML private Rectangle fadeOutRectangle;
  @FXML private ImageView startButton;

  @FXML
  private void initialize() {
    startButton.setVisible(true);
  }

  @FXML
  private void start() throws IOException {
    startButton.setVisible(false);
    // Create a fade out transition // Create a fade in transition for the black overlay
    FadeTransition fadeToBlack = new FadeTransition(Duration.seconds(1), fadeOutRectangle);
    fadeToBlack.setFromValue(0.0); // Start fully transparent
    fadeToBlack.setToValue(1.0); // End fully opaque (black)

    // After the fade to black ends, switch to the next scene
    fadeToBlack.setOnFinished(
        event -> {
          try {
            Timer timer = Timer.getTimer();
            timer.reset();
            timer.start();
            App.setRoot("crimeScene");
          } catch (IOException e) {
            e.printStackTrace();
          }
        });

    // Start the fade to black transition
    fadeToBlack.play();
  }
}
