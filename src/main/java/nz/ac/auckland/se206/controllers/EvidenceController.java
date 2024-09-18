package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Timer;



public class EvidenceController {
  
  @FXML Label timerLbl; 

  public void initialize(){
    Timer timer = Timer.getTimer();
    StringBinding timeLayout = Bindings.createStringBinding(() -> {
      int time = timer.getTimeLeft().get();
      int mins = time/60;
      int secs = time % 60;
      return String.format("%s: %1d:%02d","Time Left", mins, secs);
    },timer.getTimeLeft());

    timerLbl.textProperty().bind(timeLayout);
    timer.start();
  }

    public void toSuspectFingerprints() throws IOException {
        App.setRoot("suspectFingerprint");
    }

    public void toVaultFingerprints() throws IOException {
        App.setRoot("vaultFingerprint");
    }

    public void toForensicsPolicies() throws IOException {
        App.setRoot("forensicsRules");
    }

    public void toInvestigationLog() throws IOException {
        App.setRoot("investigationLog");
    }

    public void backToEvidence() throws IOException {
        App.setRoot("evidence");
    }

    @FXML
    public void shutDown(ActionEvent event) throws IOException {
    Parent crimeSceneRoot = SceneManager.getUiRoot(SceneManager.AppUi.CRIME_SCENE);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

  

    stage.getScene().setRoot(crimeSceneRoot);
    
  }


    
}
