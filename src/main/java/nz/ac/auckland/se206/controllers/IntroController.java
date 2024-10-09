package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.Timer;

public class IntroController {

  @FXML private Label timerLbl;
  @FXML private ImageView rightArrow;

  public void initialize() {

    // Create a drop shadow effect for the hover state
    DropShadow glow = new DropShadow();
    glow.setRadius(20);
    glow.setSpread(0.8);
    glow.setColor(Color.YELLOW);

    // Add mouse hover event to show the glow effect
    rightArrow.setOnMouseEntered(e -> rightArrow.setEffect(glow));

    // Remove the glow effect when the mouse exits
    rightArrow.setOnMouseExited(e -> rightArrow.setEffect(null));

    // Get the timer instance
    Timer timer = Timer.getTimer();

    // Create a string binding that updates the time left every second
    StringBinding timeLayout =
        Bindings.createStringBinding(
            () -> {
              int time = timer.getTimeLeft().get();
              int mins = time / 60;
              int secs = time % 60;
              return String.format("%1d:%02d", mins, secs);
            },
            timer.getTimeLeft());

    // Bind the timer label to the time layout
    timerLbl.textProperty().bind(timeLayout);
    timer.start();
  }

  public void goToPhone() throws IOException {
    App.setRoot("introPhone");
  }

  public void goToCrimeScene() throws IOException {
    App.setRoot("crimeScene");
  }

  public void goToUntil() throws IOException {
    App.setRoot("introUntil");
  }
}
