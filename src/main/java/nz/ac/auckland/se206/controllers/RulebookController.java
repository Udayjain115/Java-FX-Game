package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Timer;

/**
 * This class is the controller for the rulebook view. It handles the logic for the rulebook view.
 * It allows the player to view the rulebook sections. The player can move the sections around the
 * screen. The player can also exit the rulebook view and return to the crime scene. The player can
 * also view the timer on the rulebook view.
 */
public class RulebookController {
  @FXML private ImageView left;
  @FXML private ImageView right;
  @FXML private ImageView middle;
  @FXML private Label timerLbl;
  @FXML private Label completeLabel; // Reference to the "Complete" label

  // Variables to store the initial mouse click position
  private double horizontalOffset = 0;
  private double verticalOffset = 0;

  // Snapping tolerance (in pixels)
  private static final double SNAP_TOLERANCE = 30;

  /**
   * Initializes the rulebook view. This method is called when the rulebook view is loaded. It
   * initializes the timer and binds the timer label to the time left.
   */
  @FXML
  public void initialize() {
    // Make all sections draggable
    makeDraggable(left);
    makeDraggable(right);
    makeDraggable(middle);
    Timer timer = Timer.getTimer();
    StringBinding timeLayout =
        Bindings.createStringBinding(
            () -> {
              int time = timer.getTimeLeft().get();
              int mins = time / 60;
              int secs = time % 60;
              return String.format("%1d:%02d", mins, secs);
            },
            timer.getTimeLeft());

    timerLbl.textProperty().bind(timeLayout);
    timer.start();
  }

  /**
   * Makes the given ImageView draggable by adding mouse listeners and enables snapping.
   *
   * @param imageView the ImageView to make draggable.
   */
  private void makeDraggable(ImageView imageView) {
    // Restrict dragging to the visible part of the image
    imageView.setPickOnBounds(false);

    // On mouse press, record the initial position of the mouse relative to the image
    imageView.setOnMousePressed(
        (MouseEvent event) -> {
          horizontalOffset = event.getSceneX() - imageView.getLayoutX();
          verticalOffset = event.getSceneY() - imageView.getLayoutY();
        });

    // On mouse drag, update the layout position of the image based on the new mouse position
    imageView.setOnMouseDragged(
        (MouseEvent event) -> {
          imageView.setLayoutX(event.getSceneX() - horizontalOffset);
          imageView.setLayoutY(event.getSceneY() - verticalOffset);

          // Check for snapping with other images
          checkSnap(imageView);
        });
  }

  /**
   * Checks if the dragged image is close enough to any other image to snap them together.
   *
   * @param draggedImage the currently dragged ImageView
   */
  private void checkSnap(ImageView draggedImage) {
    // Check snapping with left
    if (draggedImage != left) {
      snapIfClose(draggedImage, left);
    }
    // Check snapping with right
    if (draggedImage != right) {
      snapIfClose(draggedImage, right);
    }
    // Check snapping with middle
    if (draggedImage != middle) {
      snapIfClose(draggedImage, middle);
    }

    // Check if all images are snapped together
    if (isPuzzleComplete()) {

      // Set the opacity of the "Complete" label to 1
      completeLabel.setOpacity(1.0);
    } else {
      // Set the opacity of the "Complete" label to 0
      completeLabel.setOpacity(0.28);
    }
  }

  /**
   * Snaps the dragged image to the target image if they are within the SNAP_TOLERANCE distance.
   *
   * @param draggedImage the currently dragged ImageView
   * @param targetImage the target ImageView to snap to
   */
  private void snapIfClose(ImageView draggedImage, ImageView targetImage) {
    double draggedX = draggedImage.getLayoutX();
    double draggedY = draggedImage.getLayoutY();
    double targetX = targetImage.getLayoutX();
    double targetY = targetImage.getLayoutY();

    // Check if the images are within the snapping tolerance
    if (Math.abs(draggedX - targetX) < SNAP_TOLERANCE
        && Math.abs(draggedY - targetY) < SNAP_TOLERANCE) {
      // Snap to the target image's position
      draggedImage.setLayoutX(targetX);
      draggedImage.setLayoutY(targetY);
    }
  }

  /**
   * Handles the event when the close button (X) is clicked.
   *
   * @param event the mouse event that triggered the method
   * @throws IOException if the FXML file is not found
   */
  @FXML
  public void closeRulebook(MouseEvent event) throws IOException {
    Parent crimeSceneRoot = SceneManager.getUiRoot(SceneManager.AppUi.CRIME_SCENE);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    stage.getScene().setRoot(crimeSceneRoot);
  }

  /**
   * Checks if all pieces of the puzzle are snapped together.
   *
   * @return true if all pieces are correctly aligned
   */
  private boolean isPuzzleComplete() {
    // Example condition: check if all pieces are within tolerance of one another
    return Math.abs(left.getLayoutX() - middle.getLayoutX()) < SNAP_TOLERANCE
        && Math.abs(right.getLayoutX() - middle.getLayoutX()) < SNAP_TOLERANCE
        && Math.abs(left.getLayoutY() - middle.getLayoutY()) < SNAP_TOLERANCE
        && Math.abs(right.getLayoutY() - middle.getLayoutY()) < SNAP_TOLERANCE;
  }
}
