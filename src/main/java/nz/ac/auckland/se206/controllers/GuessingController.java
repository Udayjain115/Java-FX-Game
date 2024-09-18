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
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.GameStateContext;
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

  private String profession;
  private GameStateContext context;
  private Boolean timeLeft = true;
  private Timer timer;

  private final List<String> chatMessages =
      Collections.synchronizedList(new CopyOnWriteArrayList<>());

  private ChatCompletionRequest chatCompletionRequest;

  public void initialize() {
    if (CrimeSceneController.visitedRooms.size() < 3) {
      System.out.println("**********************");
      return;
    }
    timer = Timer.getTimer();
    timer.reset(60);

    StringBinding timeLayout =
        Bindings.createStringBinding(
            () -> {
              int time = timer.getTimeLeft().get();
              int mins = time / 60;
              int secs = time % 60;
              return String.format("%s: %1d:%02d", "Time Left", mins, secs);
            },
            timer.getTimeLeft());

    timer
        .getTimeLeft()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue.intValue() == 0) {
                timeLeft = false;
                try {
                  onSendMessage(new ActionEvent());
                } catch (ApiProxyException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
            });

    timerLbl.textProperty().bind(timeLayout);
    timer.start();
  }

  @FXML
  private void handleRectangleClicked(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    if (clickedRectangle == manager || clickedRectangle == janitor) {
      text.appendText(
          "You did not guess correctly. You lost! The police officer was the thief! \n\n");
      btnSend.setDisable(true);
      context.setState(context.getGameOverState());
      return;
    } else {
      text.appendText(
          "You were correct and the officer has been arrested. Please give the detectives your"
              + " reasoning.\n\n");
      return;
    }
  }

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
    if (message.isEmpty() && timeLeft) {
      return;
    }
    timer.stop();
    timerLbl.setVisible(false);
    text.appendText("You: " + message + "\n\n");
    textInput.clear();
    setProfession("feedback");
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
            text.appendText("Game: " + msg.getContent() + "\n\n");
          });
    }
  }
}
