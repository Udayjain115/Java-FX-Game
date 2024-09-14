package nz.ac.auckland.se206.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import nz.ac.auckland.se206.prompts.PromptEngineering;

public class GuessingController {
  @FXML private TextArea text;
  @FXML private TextField textInput;
  @FXML private Rectangle police;
  @FXML private Rectangle manager;
  @FXML private Rectangle janitor;

  private GameStateContext context;

  
  

  @FXML
  private void handleRectangleClicked(MouseEvent event){
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    if(clickedRectangle == manager || clickedRectangle == janitor){
      text.appendText("You did not guess correctly. You lost! The police officer was the thief!");
      context.setState(context.getGameOverState());
    }else{
      text.appendText("You were correct and the officer has been arrested. Please give the detectives your reasoning.");
    }
  }

  
} 
  
  


