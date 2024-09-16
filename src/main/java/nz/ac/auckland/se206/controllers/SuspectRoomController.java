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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class SuspectRoomController {

  @FXML private TextArea text;
  @FXML private TextField textInput;
  @FXML private Button goToJanitor;
  @FXML private VBox menuBox; // Root layout of the scene

  private String profession;
  private ChatCompletionRequest chatCompletionRequest;
  private String professionTalking;
  private String currentPersonTalking;
  private final List<String> chatMessages =
      Collections.synchronizedList(new CopyOnWriteArrayList<>());
  private VBox dropDownMenu; // The container for the drop-down items
  private boolean isMenuVisible = false; // Tracks menu visibility

  public void initialize() {
    // Create the menu button
    Button menuButton = new Button("≡"); // This is the "hamburger" button
    menuButton.setStyle("-fx-font-size: 20px; -fx-background-color: transparent;");

    // Create the drop-down menu container
    dropDownMenu = new VBox(10);
    dropDownMenu.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px;");
    dropDownMenu.setVisible(false); // Initially hidden

    // Add menu items to the drop-down menu
    Button homeButton = new Button("Home");
    Button aboutButton = new Button("About");
    Button servicesButton = new Button("Services");
    Button contactButton = new Button("Contact");

    // Add buttons to the drop-down menu
    dropDownMenu.getChildren().addAll(homeButton, aboutButton, servicesButton, contactButton);

    // Toggle visibility of the drop-down menu on button click
    menuButton.setOnAction(e -> toggleMenu());

    // Add the menu button and the drop-down menu to the root container
    menuBox.getChildren().addAll(menuButton, dropDownMenu);
  }

  @FXML
  // Function to toggle the visibility of the drop-down menu
  private void toggleMenu() {
    isMenuVisible = !isMenuVisible;
    dropDownMenu.setVisible(isMenuVisible);
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
  public void switchToCrimeScene() throws IOException {
    App.setRoot("crimeScene");
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
