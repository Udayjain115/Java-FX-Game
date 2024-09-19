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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Timer;
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class SuspectRoomController {

  @FXML private TextArea text;
  @FXML private TextField textInput;
  @FXML private Button goToJanitor;
  @FXML private VBox menuBox; // Root layout of the scene
  @FXML private Button btnSend;
  @FXML private Label timerLbl;

  private String profession;
  private ChatCompletionRequest chatCompletionRequest;
  private String professionTalking;
  private String currentPersonTalking;
  private final List<String> chatMessages =
      Collections.synchronizedList(new CopyOnWriteArrayList<>());

  private boolean isMenuVisible = false; // Tracks menu visibility

  private Parent crimeSceneRoot = SceneManager.getUiRoot(SceneManager.AppUi.CRIME_SCENE);
  private CrimeSceneController crimeSceneController =
      (CrimeSceneController) SceneManager.getController(SceneManager.AppUi.CRIME_SCENE);

  @FXML
  private void initialize() {
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
  }

  @FXML
  private void managerSetTrue() {
    crimeSceneController.addVisitedRoom("bankManager");
  }

  @FXML
  private void janitorSetTrue() {
    crimeSceneController.addVisitedRoom("janitor");
  }

  @FXML
  private void policemanSetTrue() {
    crimeSceneController.addVisitedRoom("policeman");
  }

  @FXML
  // Function to toggle the visibility of the drop-down menu
  private void toggleMenu() {
    isMenuVisible = !isMenuVisible;
    menuBox.setVisible(isMenuVisible);
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

  // Switch to Room 3

  @FXML
  public void switchToCrimeScene(ActionEvent event) throws IOException {
    Parent crimeSceneRoot = SceneManager.getUiRoot(SceneManager.AppUi.CRIME_SCENE);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    stage.getScene().setRoot(crimeSceneRoot);
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
    } else if (profession.equals("janitor")) {
      professionTalking = "Janitor";
    } else if (profession.equals("bankManager")) {
      professionTalking = "Bank Manager";
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
                System.out.println(profession);
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
}
