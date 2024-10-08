package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import nz.ac.auckland.se206.App;

public class IntroController {

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
