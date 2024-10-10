package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.Timer;
import nz.ac.auckland.se206.prompts.PromptEngineering;

/**
 * This class is the controller for the guessing view. It handles the logic for the guessing view.
 * It allows the player to guess the thief based on the information provided. The player can also
 * provide reasoning for their guess. The player can also reset the game if they are unable to guess
 * the thief.
 */
public class GuessingController {
  @FXML private TextArea text;
  @FXML private TextField textInput;
  @FXML private Rectangle police;
  @FXML private Rectangle manager;
  @FXML private Rectangle janitor;
  @FXML private Button btnSend;
  @FXML private Label timerLbl;
  @FXML private ImageView suspects;
  @FXML private ImageView wrongPerson;
  @FXML private ImageView wrongReason;
  @FXML private ImageView won;
  @FXML private ImageView timeOut;
  @FXML private Button resetButton;
  @FXML private javafx.scene.image.ImageView animationImage;

  // Declare the ImageView for the pen-writing animation
  private TranslateTransition movingPen;
  private ParallelTransition parallelPen;

  private String profession;
  private Boolean timeLeft = true;
  private Timer timer;
  private Boolean appendedMsg = false;
  private Boolean hasClicked = false;

  private final List<String> chatMessages =
      Collections.synchronizedList(new CopyOnWriteArrayList<>());

  private ChatCompletionRequest gptCompleteRequest;

  /**
   * Initializes the guessing view. This method is called when the guessing view is loaded. It
   * initializes the pen-writing animation and the timer. It also sets the visibility of the images
   * to false and disables the send button and text input.
   */
  public void initialize() {
    // Initialize the pen-writing animation
    btnSend.setDisable(true);
    textInput.setDisable(true);
    InputStream penImageStream = getClass().getResourceAsStream("/images/pen.png");
    animationImage.setImage(new Image(penImageStream));
    movingPen = new TranslateTransition(Duration.seconds(2), animationImage);
    movingPen.setFromX(50);
    movingPen.setToX(200);
    movingPen.setCycleCount(TranslateTransition.INDEFINITE);
    movingPen.setAutoReverse(true);
    animationImage.setVisible(false); // Initially hide the animation image

    RotateTransition penRotate = new RotateTransition(Duration.seconds(0.5), animationImage);
    penRotate.setFromAngle(0);
    penRotate.setToAngle(15);
    penRotate.setCycleCount(TranslateTransition.INDEFINITE);
    penRotate.setAutoReverse(true);

    guessHover(police);
    guessHover(manager);
    guessHover(janitor);

    parallelPen = new ParallelTransition(movingPen, penRotate);

    text.appendText("Game: Click on who you think the thief is... \n\n");

    // Set the visibility of the images to false
    suspects.setVisible(true);
    wrongPerson.setVisible(false);
    wrongReason.setVisible(false);
    won.setVisible(false);
    timeOut.setVisible(false);
    resetButton.setDisable(true);
    resetButton.setVisible(false);

    textInput.setOnKeyPressed(
        event -> {
          switch (event.getCode()) {
            case ENTER:
              try {
                onSendMessage(null); // Trigger the send message method when Enter is pressed
                textInput.clear();
              } catch (ApiProxyException | IOException e) {
                e.printStackTrace();
              }
              break;
            default:
              break;
          }
        });

    if (CrimeSceneController.visitedRooms.size() < 4) {
      timeOut.setVisible(true);
      resetButton.setDisable(false);
      resetButton.setVisible(true);
      timerLbl.setVisible(false);
      text.clear();
      text.appendText("Game: You ran out of time!\n\n");
      return;
    }

    // Initialize the timer
    timer = Timer.getTimer();
    timer.reset(60);

    // Bind the timer label to the time left
    StringBinding timeLayout =
        Bindings.createStringBinding(
            () -> {
              int time = timer.getTimeLeft().get();
              int mins = time / 60;
              int secs = time % 60;
              return String.format("%1d:%02d", mins, secs);
            },
            timer.getTimeLeft());

    // Add a listener to the time left property
    timer
        .getTimeLeft()
        .addListener(
            (observable, oldValue, newValue) -> {
              // If the time left is 0, set the time left to false
              if (newValue.intValue() == 0) {
                timeLeft = false;
                try {
                  if (hasClicked) {
                    onSendMessage(new ActionEvent());
                  } else {
                    timeOut.setVisible(true);
                    suspects.setVisible(false);
                    resetButton.setVisible(true);
                    resetButton.setDisable(false);
                    timerLbl.setVisible(false);
                  }

                  // Stop the timer and set the visibility of the timer label to false
                  timerLbl.setVisible(false);
                  btnSend.setDisable(true);
                } catch (ApiProxyException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
            });

    // Start the timer
    timerLbl.textProperty().bind(timeLayout);
    timer.start();
  }

  /** Shows the pen-writing animation when the user is typing a message to GPT. */
  private void showPenAnimation() {
    Platform.runLater(
        () -> {
          animationImage.setVisible(true); // Show the pen-writing animation
          parallelPen.play(); // Start the animation
        });
  }

  @FXML
  private void guessHover(Shape mouseOver) {
    DropShadow effect = new DropShadow();
    effect.setColor(Color.YELLOW); // Set the color of the glow
    effect.setRadius(10); // Set the radius of the shadow
    effect.setSpread(0.8); // Increase the spread to intensify the glow
    effect.setOffsetX(0); // No offset, centered glow
    effect.setOffsetY(0); // No offset, centered glow
    mouseOver.setOnMouseEntered(
        event -> {
          mouseOver.setOpacity(.15);
          mouseOver.setEffect(effect);
        });
    mouseOver.setOnMouseExited(
        event -> {
          mouseOver.setOpacity(0);
          mouseOver.setEffect(null);
        });
  }

  // Stop the pen-writing animation once GPT responds
  /** Hides the pen-writing animation when the user has finished typing a message to GPT. */
  private void hidePenAnimation() {
    Platform.runLater(
        () -> {
          parallelPen.stop(); // Stop the animation
          animationImage.setVisible(false); // Hide the pen-writing animation
        });
  }

  /**
   * Handles the event when a rectangle is clicked. If the rectangle clicked is the police officer,
   * the player loses. If the rectangle clicked is the police officer, the player wins. If the
   * rectangle clicked is the police officer, the player is asked to provide reasoning. Otherwise,
   * the player loses.
   *
   * @param event the mouse event that triggered the method
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleRectangleClicked(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    hasClicked = true;
    police.setDisable(true);
    manager.setDisable(true);
    janitor.setDisable(true);

    // Check if the rectangle clicked is the police officer
    if (clickedRectangle == manager || clickedRectangle == janitor) {
      text.clear();
      text.appendText("Game: You did not guess correctly. You lost! The thief got away \n\n");
      btnSend.setDisable(true);
      textInput.setDisable(true);
      wrongPerson.setVisible(true);
      suspects.setVisible(false);
      resetButton.setDisable(false);
      resetButton.setVisible(true);
      timer.stop();
      timerLbl.setVisible(false);
      return;
      // If the rectangle clicked is the police officer
    } else {
      text.clear();
      text.appendText(
          "Game: The security guard has been arrested. Please give the detectives your"
              + " reasoning.\n\n");
      btnSend.setDisable(false);
      textInput.setDisable(false);
      return;
    }
  }

  /**
   * Sets the profession of the player.
   *
   * @param profession the profession of the player
   */
  public void setProfession(String profession) {
    // Set the profession
    this.profession = profession;
    // Initialize the chat
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      // Create a new chat completion request
      gptCompleteRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.1)
              .setTopP(0.5)
              .setMaxTokens(100);
      // Send the initial system prompt
      runGpt(new ChatMessage("system", getSystemPrompt()));
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the system prompt based on the profession of the player.
   *
   * @return the system prompt based on the profession of the player
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    map.put("profession", profession);
    String fileName = String.format("%s.txt", profession);
    return PromptEngineering.getPrompt(fileName, map);
  }

  /**
   * Runs the GPT model to generate a response to the message sent.
   *
   * @param gptMessageToSend the message to send to the GPT model
   */
  private void runGpt(ChatMessage gptMessageToSend) {
    Thread gptThread =
        new Thread(
            () -> {
              try {
                showPenAnimation();
                gptCompleteRequest.addMessage(gptMessageToSend);
                ChatCompletionResult gptCompleteResult = gptCompleteRequest.execute();
                Choice gptResult = gptCompleteResult.getChoices().iterator().next();
                gptCompleteRequest.addMessage(gptResult.getChatMessage());
                hidePenAnimation();

                onAppendChatMessage(gptResult.getChatMessage());
                // this way the people will not speak out loud
                // TextToSpeech.speak(result.getChatMessage().getContent());
              } catch (ApiProxyException e) {
                e.printStackTrace();
              }
            });

    gptThread.setDaemon(true);
    gptThread.start();
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    // Get the message from the text input
    String message = textInput.getText().trim();
    // If the message is empty and there is still time left, return
    if (message.isEmpty() && timeLeft) {
      return;
    }
    timer.stop();
    timerLbl.setVisible(false);
    text.appendText("You: " + message + "\n\n");
    textInput.clear();
    setProfession("feedback");
    runGpt(new ChatMessage("user", message));
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void onAppendChatMessage(ChatMessage msg) {
    synchronized (chatMessages) {
      // If the message has not been appended yet
      if (!appendedMsg) {
        // Add the message to the chat messages list
        chatMessages.add(msg.getContent());
        Platform.runLater(
            () -> {
              text.clear();
              // Append the message to the chat text area
              text.appendText("Game: " + msg.getContent() + "\n\n");
              // If the message contains the word "correct", show the won image
              if (msg.getContent().contains("correct")) {
                won.setVisible(true);
                suspects.setVisible(false);
                // If the message contains the word "missing", show the wrong person image
              } else if (msg.getContent().contains("missing")) {
                wrongReason.setVisible(true);
                suspects.setVisible(false);
              }
              // Disable the send button and enable the reset button
              btnSend.setDisable(true);
              textInput.setDisable(true);
              resetButton.setDisable(false);
              resetButton.setVisible(true);
            });
        appendedMsg = true;
      }
    }
  }

  /**
   * Resets the game when the reset button is clicked.
   *
   * @param event the action event triggered by the reset button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onResetGame(ActionEvent event) throws IOException {
    App.restartApp();
  }
}
