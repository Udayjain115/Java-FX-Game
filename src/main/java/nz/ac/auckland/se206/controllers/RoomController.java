package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController {

  private static boolean isFirstTimeInit = true;
  private static GameStateContext context = new GameStateContext();

  @FXML private Button btnGuess;
  @FXML private TextArea text;
  @FXML private TextField textInput;
  @FXML private Button btnSend;
  @FXML private Rectangle mop;
  @FXML private Rectangle computer;
  @FXML private Rectangle policeman;
  @FXML private Rectangle bankManager;
  @FXML private Rectangle janitor;

  private String professionTalking;
  private String currentPersonTalking;
  private boolean interactedWithSuspect = false;
  private boolean interactedWithClue = false;
  private boolean guessMade = false;

  private ChatCompletionRequest chatCompletionRequest;
  private String profession;

  @FXML private Label timerLabel; // Label to display the timer
  @FXML private ProgressBar timerProgressBar; // ProgressBar for visual countdown

  @FXML private Button startButton; // Button to start the timer

  @FXML private Label state; // Label to display the current state

  private int totalPlayingTimeSeconds = 120; // Total playing time for percentage calculation
  private int playingTimeSeconds = totalPlayingTimeSeconds; // 1 minute and 40 seconds for playing

  private int totalGuessingTimeSeconds = 10; // Total guessing time for percentage calculation
  private int guessingTimeSeconds = totalGuessingTimeSeconds; // 20 seconds for guessing

  private boolean gameOver = false;
  private boolean isPlayingPhase = false; // Flag to indicate playing phase
  private boolean gameRunning = false; // Flag to control the timer
  private boolean isGuessingPhase = false; // Flag to indicate guessing phase
  private boolean guessingRunning = false; // Flag to control the guessing timer

  private final List<String> chatMessages =
      Collections.synchronizedList(new CopyOnWriteArrayList<>());

  private void startTimer() {

    btnSend.setDisable(true); // Disable the send button to start with

    if (gameRunning) {
      return; // Prevent starting a new thread if already running
    }
    gameRunning = true;
    isPlayingPhase = true;

    clearChatAndAddMessage("Game started! Investigate the clues and suspects.");

    Thread timerThread =
        new Thread(
            () -> {
              while (playingTimeSeconds > 0 && gameRunning) {
                playingTimeSeconds--;

                // Calculate minutes and seconds
                int minutes = playingTimeSeconds / 60;
                int seconds = playingTimeSeconds % 60;

                // Calculate progress
                double progress = (double) playingTimeSeconds / totalPlayingTimeSeconds;

                // Update the label and progress bar in the JavaFX Application Thread
                Platform.runLater(
                    () -> {
                      timerLabel.setText(
                          String.format("Playing Time: %02d:%02d", minutes, seconds));
                      timerProgressBar.setProgress(progress);
                    });

                try {
                  Thread.sleep(1000); // Pause for 1 second
                } catch (InterruptedException ex) {
                  ex.printStackTrace();
                }
              }

              // Transition to guessing phase
              Platform.runLater(
                  () -> {
                    if (!interactedWithClue || !interactedWithSuspect) {
                      clearChatAndAddMessage(
                          "You didn't investigate both a clue AND a suspect before guessing. Game"
                              + " over!");
                      TextToSpeech.speak(
                          "You didn't investigate both a clue AND a suspect before guessing. Game"
                              + " over!");
                      context.setState(context.getGameOverState());
                      timerLabel.setText("Game over!");
                      isPlayingPhase = false;
                      state.setText("You did not investigate! You lose.");
                      stopPlayingTimer();
                      return;
                    }

                    if (!guessMade) {
                      TextToSpeech.speak("Time to guess! Click on who you think is the thief.");
                      clearChatAndAddMessage("Time to guess! Click on who you think is the thief.");
                      state.setText("Click on who you think is the thief.");
                      isGuessingPhase = true;
                      context.setState(context.getGuessingState());
                      startGuessingTimer();
                      isPlayingPhase = false;
                    }
                  });
            });

    timerThread.setDaemon(true); // Allow the thread to exit when the application closes
    timerThread.start();
  }

  private void startGuessingTimer() {
    if (guessingRunning) {
      return; // Prevent starting a new thread if already running
    }
    guessingRunning = true;
    isGuessingPhase = true;

    Thread guessingThread =
        new Thread(
            () -> {

              // Making it so when guessing you can't press the guess button or the send button
              btnGuess.setDisable(true);
              btnSend.setDisable(true);

              while (guessingTimeSeconds > 0 && guessingRunning) {

                // Calculate minutes and seconds
                int minutes = guessingTimeSeconds / 60;
                int seconds = guessingTimeSeconds % 60;
                guessingTimeSeconds--;

                // Calculate progress
                double progress = (double) guessingTimeSeconds / totalGuessingTimeSeconds;

                // Update the label and progress bar in the JavaFX Application Thread
                Platform.runLater(
                    () -> {
                      timerLabel.setText(
                          String.format("Guessing Time: %02d:%02d", minutes, seconds));
                      timerProgressBar.setProgress(progress);
                    });

                try {
                  Thread.sleep(1000); // Pause for 1 second
                } catch (InterruptedException ex) {
                  ex.printStackTrace();
                }
              }
              guessingRunning = false; // Reset running flag

              // Optionally reset the timer after it finishes
              if (!gameOver) {
                clearChatAndAddMessage("Time is up! Game over... you didn't guess in time!");
                TextToSpeech.speak("Time is up! Game over... you didn't guess in time!");
                Platform.runLater(() -> state.setText("Game over!"));
                Platform.runLater(() -> timerLabel.setText("Time is up!"));
              }
              btnSend.setDisable(true);
              context.setState(context.getGameOverState());
              timerProgressBar.setProgress(0);
              gameOver = true;
            });

    guessingThread.setDaemon(true); // Allow the thread to exit when the application closes
    guessingThread.start();
  }

  /**
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    map.put("profession", profession);
    String fileName = String.format("%s.txt", profession);
    return PromptEngineering.getPrompt(fileName, map);
  }

  /**
   * Sets the profession for the chat context and initializes the ChatCompletionRequest.
   *
   * @param profession the profession to set
   */
  public void setProfession(String profession) {
    this.profession = profession;
    try {
      ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.5)
              .setMaxTokens(100);
      runGpt(new ChatMessage("system", getSystemPrompt()));
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    synchronized (chatMessages) {
      chatMessages.add(msg.getContent());
      Platform.runLater(
          () -> {
            if (!msg.getRole().equals(currentPersonTalking)) {
              text.clear();
            }

            if (msg.getRole().equals("assistant")) {
              if (profession.equals("policeman")) {
                text.appendText("Policeman: " + msg.getContent() + "\n\n");
              } else if (profession.equals("bankManager")) {
                text.appendText("Bank Manager: " + msg.getContent() + "\n\n");
              } else if (profession.equals("janitor")) {
                text.appendText("Janitor: " + msg.getContent() + "\n\n");
              }
            } else if (msg.getRole().equals("user")) {
              text.appendText("You: " + msg.getContent() + "\n\n");
            }
            currentPersonTalking = profession;
          });
    }
  }

  /** Updates the UI to show "User is thinking..." while waiting for the GPT response. */
  private void showThinkingMessage() {
    if (profession.equals("policeman")) {
      professionTalking = "Policeman";
    } else if (profession.equals("bankManager")) {
      professionTalking = "Bank Manager";
    } else if (profession.equals("janitor")) {
      professionTalking = "Janitor";
    }
    Platform.runLater(() -> text.appendText(professionTalking + " is thinking...\n\n"));
  }

  /** Clears the "User is thinking..." message. */
  private void clearThinkingMessage() {
    Platform.runLater(
        () -> {
          String currentText = text.getText();
          text.setText(currentText.replace(professionTalking + " is thinking...\n\n", ""));
        });
  }

  private void runGpt(ChatMessage msg) {
    Thread thread =
        new Thread(
            () -> {
              try {
                showThinkingMessage();
                chatCompletionRequest.addMessage(msg);
                ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
                Choice result = chatCompletionResult.getChoices().iterator().next();
                chatCompletionRequest.addMessage(result.getChatMessage());
                clearThinkingMessage();
                appendChatMessage(result.getChatMessage());
                // this way the people will not speak out loud
                // TextToSpeech.speak(result.getChatMessage().getContent());
              } catch (ApiProxyException e) {
                e.printStackTrace();
              }
            });

    thread.setDaemon(true);
    thread.start();
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
    String message = textInput.getText().trim();
    if (message.isEmpty()) {
      return;
    }
    textInput.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);
    runGpt(msg);
  }

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    if (isFirstTimeInit) {
      TextToSpeech.speak(
          "Chat with the three people at the crime scene, and guess who is the thief");
      isFirstTimeInit = false;
    }
    // Initialize the label with the starting time
    timerLabel.setText("Time: " + playingTimeSeconds);

    // Add hover event handlers for rectangles
    setupHoverEffect(mop);
    setupHoverEffect(computer);
    setupHoverEffect(policeman);
    setupHoverEffect(bankManager);
    setupHoverEffect(janitor);

    startTimer();
  }

  private void setupHoverEffect(Rectangle hoveringOver) {
    DropShadow hoverEffect = new DropShadow();
    hoverEffect.setColor(Color.BLACK); // Set the color of the glow
    hoverEffect.setRadius(10); // Set the radius of the shadow
    hoverEffect.setSpread(0.5); // Increase the spread to intensify the glow
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

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
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

    if (clickedRectangle.getId().equals("mop") || clickedRectangle.getId().equals("computer")) {
      btnSend.setDisable(true);
      interactedWithClue = true;
    } else if (clickedRectangle.getId().equals("policeman")
        || clickedRectangle.getId().equals("bankManager")
        || clickedRectangle.getId().equals("janitor")) {
      btnSend.setDisable(false);
      interactedWithSuspect = true;
    }

    if (isPlayingPhase) {
      if (clickedRectangle.getId().equals("mop")) {
        text.clear();
        text.appendText(
            "Mop: I'm a mop... it looks like I was brought here in a hurry? I'm covered in water,"
                + " but where's the bucket?\n\n"
                + "By the way, you can't chat to me... I'm just a clue\n\n");
        return;
      } else if (clickedRectangle.getId().equals("computer")) {
        text.clear();
        text.appendText(
            "Security Logs:\n\n"
                + "1 hr ago: Bank Manager entered the room\n"
                + "50 mins ago: Janitor entered the room\n"
                + "49 mins ago: Janitor & Bank Manager exited the room\n"
                + "20 mins ago: Policeman entered the room\n"
                + "5 mins ago: Bank Manager entered the room\n"
                + "4 mins ago: Janitor entered the room\n\n"
                + "By the way, you can't chat to me... I'm just a clue.\n\n");
        return;
      }
    }

    // Send to be handled by the game state context
    context.handleRectangleClick(event, clickedRectangle.getId());

    // return at this point because the game is not over
    if (clickedRectangle.getId().equals("mop") || clickedRectangle.getId().equals("computer")) {
      return;
    }

    // Temp comment to test the clicked rectangle
    // System.out.println(((Node) event.getSource()).getId());

    /// end the game
    if (isGuessingPhase && !gameOver) {
      if (clickedRectangle.getId().equals("policeman")) {
        clearChatAndAddMessage("You guessed the Policeman. Game over! You win!");
        state.setText("Game over! You win!");
        gameOver = true;
      } else {
        clearChatAndAddMessage("You guessed incorrectly. Game over! You lose!");
        state.setText("Game over! You lose!");
        gameOver = true;
      }

      // Stop the guessing timer
      timerLabel.setText("Time is up!");
      guessingRunning = false;
      timerProgressBar.setProgress(0);
    }
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {

    if (!interactedWithClue || !interactedWithSuspect) {
      TextToSpeech.speak("You must interact with both a suspect and a clue before guessing.");
      text.appendText("You must interact with both a suspect and a clue before guessing.\n\n");
      return;
    }

    // Stop the playing timer
    stopPlayingTimer();
    guessMade = true;
    isPlayingPhase = false;

    TextToSpeech.speak("Time to guess! Click on who you think is the thief.");
    clearChatAndAddMessage("Time to guess! Click on who you think is the thief.");

    // Immediately transition to the guessing phase
    context.handleGuessClick();
    context.setState(context.getGuessingState());
    startGuessingTimer();
    state.setText("Click on who you think is the thief.");
  }

  /** Stops the playing timer early when the guess button is pressed. */
  private void stopPlayingTimer() {
    gameRunning = false; // Stop the playing phase timer
    btnSend.setDisable(true); // Disable the send button
    btnGuess.setDisable(true); // Disable the guess button
  }

  /**
   * Clears the chat text area and adds a message to it.
   *
   * @param message the message to add to the chat
   */
  private void clearChatAndAddMessage(String message) {
    Platform.runLater(
        () -> {
          text.clear();
          text.appendText(message + "\n\n");
        });
  }
}
