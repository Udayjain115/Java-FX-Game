package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager;

public class RulebookController {
  @FXML private ImageView section1;
  @FXML private ImageView section2;
  @FXML private ImageView section3;
  @FXML private ImageView section4;

  // Variables to store the initial mouse click position
  private double xOffset = 0;
  private double yOffset = 0;

  @FXML
  public void initialize() {
    // Make all sections draggable
    makeDraggable(section1);
    makeDraggable(section2);
    makeDraggable(section3);
    makeDraggable(section4);
  }

  /**
   * Makes the given ImageView draggable by adding mouse listeners.
   *
   * @param imageView the ImageView to make draggable.
   */
  private void makeDraggable(ImageView imageView) {
    // On mouse press, record the initial position of the mouse relative to the image
    imageView.setOnMousePressed(
        (MouseEvent event) -> {
          xOffset = event.getSceneX() - imageView.getLayoutX();
          yOffset = event.getSceneY() - imageView.getLayoutY();
        });

    // On mouse drag, update the layout position of the image based on the new mouse position
    imageView.setOnMouseDragged(
        (MouseEvent event) -> {
          imageView.setLayoutX(event.getSceneX() - xOffset);
          imageView.setLayoutY(event.getSceneY() - yOffset);
        });
  }

  /**
   * Handles the event when the close button (X) is clicked.
   *
   * @throws IOException
   */
  @FXML
  public void closeRulebook(MouseEvent event) throws IOException {
    Parent crimeSceneRoot = SceneManager.getUiRoot(SceneManager.AppUi.CRIME_SCENE);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    stage.getScene().setRoot(crimeSceneRoot);
  }
}
