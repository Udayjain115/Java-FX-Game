package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.GameStateContext;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class CrimeSceneController {

  @FXML private Button btnGuess;
  @FXML private Pane crimeScenePane;
  @FXML private Pane evidencePane;
  @FXML private Pane suspectFingerprintPane;
  @FXML private Pane vaultFingerprintPane;
  @FXML private Pane forensicsRulesPane;
  @FXML private Pane investigationLogPane;
  @FXML private Rectangle cameraRectangle;
  @FXML private Rectangle rulebookRectangle;
  @FXML private Rectangle evidenceRectangle;

  private static boolean isFirstTimeInit = true;
  private static GameStateContext context = new GameStateContext();

  private boolean cameraClicked = false;
  private boolean rulebookClicked = false;
  private boolean evidenceClicked = false;

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    if (isFirstTimeInit) {
      btnGuess.setDisable(true);
      // Disable the guess button until the user has spoken to all suspects and
      // interacted with all clues?

      // TO-DO ADD ANY INITIALISATION CODE HERE
      isFirstTimeInit = false;
    }
    crimeScenePane.setVisible(true);

    evidencePane.setVisible(false);
    suspectFingerprintPane.setVisible(false);
    vaultFingerprintPane.setVisible(false);
    forensicsRulesPane.setVisible(false);
    investigationLogPane.setVisible(false);
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  @FXML
  public void checkGuess() {
    if (cameraClicked && rulebookClicked && evidenceClicked) {
      btnGuess.setDisable(false);
    } else {
      btnGuess.setDisable(true);
    }
  }

  @FXML
  public void suspectFingerprintClick() {
    evidencePane.setVisible(false);
    suspectFingerprintPane.setVisible(true);
  }

  @FXML
  public void backToEvidence() {
    evidencePane.setVisible(true);
    vaultFingerprintPane.setVisible(false);
    suspectFingerprintPane.setVisible(false);
    forensicsRulesPane.setVisible(false);
    investigationLogPane.setVisible(false);
  }

  @FXML
  public void forensicRulesClick() {
    evidencePane.setVisible(false);
    forensicsRulesPane.setVisible(true);
  }

  @FXML
  public void investigationLogClick() {
    evidencePane.setVisible(false);
    investigationLogPane.setVisible(true);
  }

  @FXML
  public void vaultFingerprintClick() {
    evidencePane.setVisible(false);
    vaultFingerprintPane.setVisible(true);
  }

  @FXML
  public void cameraClick() {
    cameraClicked = true;
    checkGuess();
  }

  @FXML
  public void rulebookClick() {
    rulebookClicked = true;
    checkGuess();
  }

  @FXML
  public void evidenceClick() {

    crimeScenePane.setVisible(false);
    evidencePane.setVisible(true);

    evidenceClicked = true;

    checkGuess();
  }

  @FXML
  public void shutDown() {
    crimeScenePane.setVisible(true);
    evidencePane.setVisible(false);
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
  }

  /**
   * Handles mouse clicks on rectangles representing people in the room.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleRectangleClick(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    context.handleRectangleClick(event, clickedRectangle.getId());
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    context.handleGuessClick();
  }
}
