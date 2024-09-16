package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class CrimeSceneController {

  @FXML private Button btnGuess;
  @FXML private Pane crimeScenePane;
  @FXML private Rectangle cameraRectangle;
  @FXML private Rectangle rulebookRectangle;
  @FXML private Rectangle evidenceRectangle;
  @FXML private Rectangle rulebookCloseButton;

  // Rules book
  @FXML private Rectangle rulesBackground;
  @FXML private Rectangle rulesCloseBackground;
  @FXML private Text crossText;
  @FXML private Text rulesText;

  private static boolean isFirstTimeInit = true;
  private static GameStateContext context = new GameStateContext();

  private boolean cameraClicked = false;
  private boolean rulebookClicked = false;
  private boolean evidenceClicked = false;
  private boolean isRulebookOpen = false;

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

  public void cameraClick() {
    cameraClicked = true;
    checkGuess();
  }

  public void rulebookClick() {
    rulebookClicked = true;
    checkGuess();
  }

  public void evidenceClick() {

    evidenceClicked = true;

    checkGuess();
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

  /**
   * Handles the event when the rulebook is clicked.
   *
   * @throws IOException
   */
  @FXML
  public void openRuleBook() throws IOException {
    // Set opacity to 1.0 to show the rulebook
    System.out.println("Opening rulebook");
    App.setRoot("ruleBook");
  }

  /**
   * Handles the event when the close button (X) is clicked.
   *
   * @throws IOException
   */
  @FXML
  public void closeRuleBook() throws IOException {
    App.setRoot("crimeScene");
  }
}
