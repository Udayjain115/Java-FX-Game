package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;

public class RulebookController {

  @FXML private ImageView dustImage;
  @FXML private ImageView dustImage2;

  private ImageCursor dustBrushCursor;

  @FXML
  public void initialize() {
    // Load the original cursor image
    Image originalCursorImage = new Image(getClass().getResourceAsStream("/images/brush.png"));

    // Create an ImageCursor with the scaled image
    dustBrushCursor =
        new ImageCursor(
            originalCursorImage, originalCursorImage.getWidth(), originalCursorImage.getHeight());

    // Set event handlers for dustImage
    dustImage.setOnMouseEntered(
        event -> {
          dustImage.setCursor(dustBrushCursor);
        });
    dustImage.setOnMouseExited(
        event -> {
          dustImage.setCursor(Cursor.DEFAULT);
        });

    // Set event handlers for dustImage2
    dustImage2.setOnMouseEntered(
        event -> {
          dustImage2.setCursor(dustBrushCursor);
        });
    dustImage2.setOnMouseExited(
        event -> {
          dustImage2.setCursor(Cursor.DEFAULT);
        });
  }

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
