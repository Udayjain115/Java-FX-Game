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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Timer;

public class CameraController {
  @FXML private Rectangle forward;
  @FXML private Rectangle back;
  @FXML private Rectangle exit;
  @FXML private ImageView twocam;
  @FXML private ImageView onecam;
  @FXML private ImageView twelvecam;
  @FXML private ImageView elevencam;
  @FXML private ImageView tencam;
  @FXML private Label timerLbl;

  private int count = 5;

  public void initialize() {
    forward.setDisable(true);

    // getting timer instance to put on label
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

  public void onExit(MouseEvent event) throws IOException {

    // loading crime scene when player goes to exit
    Parent crimeSceneRoot = SceneManager.getUiRoot(SceneManager.AppUi.CRIME_SCENE);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    stage.getScene().setRoot(crimeSceneRoot);
  }

  public void onGoBack() {
    // changing the viewable scene when going back
    if (count == 5) {
      twocam.setVisible(false);
      forward.setDisable(false);
      count--;
      return;
    } else if (count == 4) {
      onecam.setVisible(false);
      count--;
      return;
    } else if (count == 3) {
      twelvecam.setVisible(false);
      count--;
      return;
    } else if (count == 2) {
      elevencam.setVisible(false);
      count--;
      back.setDisable(true);
    }
  }

  public void onGoForward() {
    // changing viewable scene when going forward
    if (count == 4) {
      twocam.setVisible(true);
      forward.setDisable(true);
      count++;
      return;
    } else if (count == 3) {
      onecam.setVisible(true);
      count++;
      return;
    } else if (count == 2) {
      twelvecam.setVisible(true);
      count++;
      return;
    } else if (count == 1) {
      elevencam.setVisible(true);
      count++;
      back.setDisable(false);
    }
  }
}
