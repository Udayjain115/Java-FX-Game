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
  @FXML private ImageView section1;
  @FXML private ImageView section2;
  @FXML private ImageView section3;
  @FXML private ImageView section4;
  @FXML private Label timerLbl;

  // Variables to store the initial mouse click position
  private double horizontalOffset = 0;
  private double verticalOffset = 0;

  /**
   * Initializes the rulebook view. This method is called when the rulebook view is loaded. It
   * initializes the timer and binds the timer label to the time left.
   */
  @FXML
  public void initialize() {
    // Make all sections draggable
    makeDraggable(section1);
    makeDraggable(section2);
    makeDraggable(section3);
    makeDraggable(section4);
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
   * Makes the given ImageView draggable by adding mouse listeners.
   *
   * @param imageView the ImageView to make draggable.
   */
  private void makeDraggable(ImageView imageView) {
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
        });
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
}
