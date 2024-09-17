package nz.ac.auckland.se206.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;

public class EvidenceController {

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
