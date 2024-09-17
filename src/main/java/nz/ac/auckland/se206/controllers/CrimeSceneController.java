package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import org.apache.http.impl.io.AbstractSessionInputBuffer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SceneManager;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class CrimeSceneController {

  @FXML private Button btnGuess;
  @FXML private Button menuButton;
  @FXML private Button switchButton;
  @FXML private Pane crimeScenePane;
  @FXML private Rectangle cameraRectangle;
  @FXML private Rectangle rulebookRectangle;
  @FXML private Rectangle evidenceRectangle;
  @FXML private VBox menuBox; // Root layout of the scene
  @FXML private Rectangle rulebookCloseButton;

  // Rules book
  @FXML private Rectangle rulesBackground;
  @FXML private Rectangle rulesCloseBackground;
  @FXML private Text crossText;
  @FXML private Text rulesText;

  private static boolean isFirstTimeInit = true;
  private static GameStateContext context = new GameStateContext();

  private boolean cameraClicked;
  private boolean rulebookClicked;
  private boolean evidenceClicked;
  private boolean isMenuVisible = false;

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */




  @FXML
  public void initialize() {
    if(!isFirstTimeInit){
    System.out.println("Not first time init");
    checkGuess();
    crimeScenePane.setVisible(true);

    
    }
    if (isFirstTimeInit) {
      

      cameraClicked = false;
      rulebookClicked = false;
      evidenceClicked = false;
      checkGuess();
      System.out.println("First time init");
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
    if ((cameraClicked || rulebookClicked || evidenceClicked)){
      btnGuess.setDisable(false);
      System.out.println("Button Enabled");
    } else {
      btnGuess.setDisable(true);
      System.out.println("Button Disabled");
    }

  }





 

 
 

  @FXML
  public void cameraClick() throws IOException {
    cameraClicked = true;
    checkGuess();
    App.setRoot("camera");
  }



  @FXML
  public void evidenceClick() throws IOException {
    App.setRoot("evidence");
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
  public void openRuleBook(MouseEvent event) throws IOException {
    rulebookClicked = true;

    Parent ruleBookRoot = SceneManager.getUiRoot(SceneManager.AppUi.RULEBOOK);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    

  

    stage.getScene().setRoot(ruleBookRoot);
    System.out.println(rulebookClicked);
    checkGuess();
    btnGuess.setDisable(false);

  }

  // Switch to Room 1
  public void switchToCopRoom() throws IOException {
    App.setRoot("copRoom");
    App.openChat(null, "policeman");
  }

  public void switchToJanitorRoom() throws IOException {
    // Now switch rooms
    App.setRoot("janitorRoom");
    App.openChat(null, "janitor");
  }

  // Switch to Room 3
  public void switchToBankManagerRoom() throws IOException {
    App.setRoot("bankManagerRoom");
    App.openChat(null, "bankManager");
  }

  @FXML
  // Function to toggle the visibility of the drop-down menu
  private void toggleMenu() {
    isMenuVisible = !isMenuVisible;
    menuBox.setVisible(isMenuVisible);
  }
}
