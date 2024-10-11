package nz.ac.auckland.se206.controllers;

import java.io.File;
import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.Timer;

/**
 * Controller class for the start screen view. Handles user interactions within the start screen
 * where the user can start the game.
 */
public class StartScreenController {

  @FXML private Pane startPane;
  @FXML private Rectangle fadeOutRectangle;
  @FXML private ImageView startButton;
  private MediaPlayer mediaPlayer;

  /**
   * Initializes the start screen view. This method is called when the start screen view is loaded.
   * It initializes the start button.
   */
  @FXML
  private void initialize() {
    startButton.setVisible(true);
    playWelcomeAudio();
  }

  /**
   * This method is called when the player clicks the start button. It changes the viewable scene to
   * the crime scene.
   *
   * @throws IOException if the FXML file is not found
   */
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
            timer.reset(300);
            timer.start();
            App.setRoot("introBank");
          } catch (IOException e) {
            e.printStackTrace();
          }
        });

    // Start the fade to black transition
    fadeToBlack.play();
  }

  private void playWelcomeAudio() {
    String audioFilePath = "src/main/resources/sounds/welcome.mp3"; // Update with the correct path
    Media sound = new Media(new File(audioFilePath).toURI().toString());
    mediaPlayer = new MediaPlayer(sound);
    mediaPlayer.play();
  }
}
