package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import nz.ac.auckland.se206.App;

public class StartScreenController {

  @FXML
  private void start() throws IOException {
    App.setRoot("crimeScene");
  }
}
