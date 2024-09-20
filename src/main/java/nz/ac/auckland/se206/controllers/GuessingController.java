package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.Timer;
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class GuessingController {
  @FXML private TextArea text;
  @FXML private TextField textInput;
  @FXML private Rectangle police;
  @FXML private Rectangle manager;
  @FXML private Rectangle janitor;
  @FXML private Button btnSend;
  @FXML private Label timerLbl;
  @FXML private ImageView wrongPerson;
  @FXML private ImageView wrongReason;
  @FXML private ImageView won;
  @FXML private ImageView timeOut;
  @FXML private Button resetButton;

  private String profession;
  private Boolean timeLeft = true;
  private Timer timer;
  private Boolean appendedMsg = false;
  private Boolean hasClicked = false;

  private final List<String> chatMessages =
      Collections.synchronizedList(new CopyOnWriteArrayList<>());

  private ChatCompletionRequest chatCompletionRequest;

  public void initialize() {

    // Set the visibility of the images to false
    wrongPerson.setVisible(false);
    wrongReason.setVisible(false);
    won.setVisible(false);
    timeOut.setVisible(false);
    resetButton.setDisable(true);
    resetButton.setVisible(false);

    // If the player has not visited 3 rooms, return
    if (CrimeSceneController.visitedRooms.size() < 3) {
      timeOut.setVisible(true);
      resetButton.setDisable(false);
      resetButton.setVisible(true);
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

  @FXML
  // Handles the event when the rectangle is clicked for guessing the thief
  private void handleRectangleClicked(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    hasClicked = true;

    // Check if the rectangle clicked is the police officer
    if (clickedRectangle == manager || clickedRectangle == janitor) {
      text.appendText(
          "You did not guess correctly. You lost! The police officer was the thief! \n\n");
      btnSend.setDisable(true);
      wrongPerson.setVisible(true);
      resetButton.setDisable(false);
      resetButton.setVisible(true);
      return;
      // If the rectangle clicked is the police officer
    } else {
      text.appendText(
          "The officer has been arrested. Please give the detectives your reasoning.\n\n");
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
      chatCompletionRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.5)
              .setMaxTokens(100);
      // Send the initial system prompt
      runGpt(new ChatMessage("system", getSystemPrompt()));
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    map.put("profession", profession);
    String fileName = String.format("%s.txt", profession);
    return PromptEngineering.getPrompt(fileName, map);
  }

  private void runGpt(ChatMessage msg) {
    Thread thread =
        new Thread(
            () -> {
              try {

                chatCompletionRequest.addMessage(msg);
                ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
                Choice result = chatCompletionResult.getChoices().iterator().next();
                chatCompletionRequest.addMessage(result.getChatMessage());

                onAppendChatMessage(result.getChatMessage());
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
              // Append the message to the chat text area
              text.appendText("Game: " + msg.getContent() + "\n\n");
              // If the message contains the word "correct", show the won image
              if (msg.getContent().contains("correct")) {
                won.setVisible(true);
                // If the message contains the word "missing", show the wrong person image
              } else if (msg.getContent().contains("missing")) {
                wrongReason.setVisible(true);
              }
              // Disable the send button and enable the reset button
              btnSend.setDisable(true);
              resetButton.setDisable(false);
              resetButton.setVisible(true);
            });
        appendedMsg = true;
      }
    }
  }

  @FXML
  private void onResetGame(ActionEvent event) throws IOException {
    App.restartApp();
  }
}
