package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Timer;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class CrimeSceneController {

  private static boolean isFirstTimeInit = true;
  private static GameStateContext context = new GameStateContext();
  public static Set<String> visitedRooms = new HashSet<>();

  @FXML private Button btnGuess;
  @FXML private Button menuButton;
  @FXML private Button switchButton;
  @FXML private Pane crimeScenePane;
  @FXML private Rectangle cameraRectangle;
  @FXML private Rectangle rulebookRectangle;
  @FXML private Rectangle evidenceRectangle;
  @FXML private VBox menuBox; // Root layout of the scene
  @FXML private Rectangle rulebookCloseButton;
  @FXML private Label timerLbl;

  @FXML private Circle cameraHoverGlow;
  @FXML private Ellipse paperHoverGlow;
  @FXML private Rectangle laptopHoverGlow;

  // Rules book
  @FXML private Rectangle rulesBackground;
  @FXML private Rectangle rulesCloseBackground;
  @FXML private Text crossText;
  @FXML private Text rulesText;

  private boolean isMenuVisible = false;

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    btnGuess.setDisable(true);

    Timer timer = Timer.getTimer();
    StringBinding timeLayout =
        Bindings.createStringBinding(
            () -> {
              int time = timer.getTimeLeft().get();
              int mins = time / 60;
              int secs = time % 60;
              return String.format("%s: %1d:%02d", "Time Left", mins, secs);
            },
            timer.getTimeLeft());

    timerLbl.textProperty().bind(timeLayout);
    timer.start();

    if (!isFirstTimeInit) {
      crimeScenePane.setVisible(true);
    }
    if (isFirstTimeInit) {

      // cameraClicked = false;
      // rulebookClicked = false;
      // evidenceClicked = false;
      // Disable the guess button until the user has spoken to all suspects and
      // interacted with all clues?

      // TO-DO ADD ANY INITIALISATION CODE HERE
      isFirstTimeInit = false;
    }

    // Add hover event handlers for rectangles
    setupHoverEffect(cameraHoverGlow);
    setupHoverEffect(paperHoverGlow);
    setupHoverEffect(laptopHoverGlow);
  }

  @FXML
  private void setupHoverEffect(Shape hoveringOver) {
    DropShadow hoverEffect = new DropShadow();
    hoverEffect.setColor(Color.YELLOW); // Set the color of the glow
    hoverEffect.setRadius(10); // Set the radius of the shadow
    hoverEffect.setSpread(0.8); // Increase the spread to intensify the glow
    hoverEffect.setOffsetX(0); // No offset, centered glow
    hoverEffect.setOffsetY(0); // No offset, centered glow
    hoveringOver.setOnMouseEntered(
        event -> {
          hoveringOver.setOpacity(.15);
          hoveringOver.setEffect(hoverEffect);
        });
    hoveringOver.setOnMouseExited(
        event -> {
          hoveringOver.setOpacity(0);
          hoveringOver.setEffect(null);
        });
  }

  @FXML
  public void addVisitedRoom(String room) {
    System.out.println(visitedRooms);

    visitedRooms.add(room);
    if (visitedRooms.size() == 3) {
      btnGuess.setDisable(false);
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
  public void allowGuess() {
    btnGuess.setDisable(false);
  }

  @FXML
  public void cameraClick() throws IOException {
    App.setRoot("camera");
  }

  @FXML
  public void evidenceClick() throws IOException {
    App.setRoot("evidence");
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
    App.setRoot("guessing");
  }

  /**
   * Handles the event when the rulebook is clicked.
   *
   * @throws IOException
   */
  @FXML
  public void openRuleBook(MouseEvent event) throws IOException {

    Parent ruleBookRoot = SceneManager.getUiRoot(SceneManager.AppUi.RULEBOOK);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    stage.getScene().setRoot(ruleBookRoot);
  }

  // Switch to Room 1
  public void switchToCopRoom(ActionEvent event) throws IOException {
    App.setRoot("copRoom");
    App.openChat(null, "policeman");
  }

  public void switchToJanitorRoom(ActionEvent event) throws IOException {
    App.setRoot("janitorRoom");
    App.openChat(null, "janitor");
  }

  // Switch to Room 3
  public void switchToBankManagerRoom(ActionEvent event) throws IOException {
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
