package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.Timer;

/**
 * This class is the controller for the evidence view. It handles the logic for the evidence view.
 * It allows the player to view different evidence in the crime scene. The player can view the
 * suspect's fingerprints, the vault's fingerprints, the forensics policies, and the investigation
 * log. The player can also exit the evidence view and return to the crime scene. The player can
 * also view the timer on the evidence view.
 */
public class EvidenceController {
  @FXML private Pane suspectFingerprintPane;
  @FXML private Pane vaultFingerprintPane;

  @FXML private Label timerLbl;

  /**
   * Initializes the evidence view. This method is called when the evidence view is loaded. It
   * initializes the timer and binds the timer label to the time left.
   */
  public void initialize() {
    suspectFingerprintPane.setVisible(false);
    vaultFingerprintPane.setVisible(false);

    // Get the timer instance
    Timer timer = Timer.getTimer();
    StringBinding timeLayout =

        // Create a string binding that updates the time left every second
        Bindings.createStringBinding(
            () -> {
              int time = timer.getTimeLeft().get();
              int mins = time / 60;
              int secs = time % 60;
              return String.format("%1d:%02d", mins, secs);
            },
            timer.getTimeLeft());

    // Bind the timer label to the time layout
    timerLbl.textProperty().bind(timeLayout);

    // Start the timer
    timer.start();
  }

  /**
   * This method is called when the player clicks the suspect's fingerprints button. It changes the
   * viewable scene to the suspect's fingerprints view.
   *
   * @throws IOException if the FXML file is not found
   */
  public void toSuspectFingerprints() throws IOException {
    suspectFingerprintPane.setVisible(true);
  }

  /**
   * This method is called when the player clicks the vault's fingerprints button. It changes the
   * viewable scene to the vault's fingerprints view.
   *
   * @throws IOException if the FXML file is not found
   */
  public void toVaultFingerprints() throws IOException {
    vaultFingerprintPane.setVisible(true);
  }

  public void closeSuspectFingerprint() {
    suspectFingerprintPane.setVisible(false);
  }

  public void closeVaultFingerprint() {
    vaultFingerprintPane.setVisible(false);
  }

  /**
   * This method is called when the player clicks the forensics policies button. It changes the
   * viewable scene to the forensics policies view.
   *
   * @throws IOException if the FXML file is not found
   */
  public void toForensicsPolicies() throws IOException {
    App.setRoot("forensicsRules");
  }

  /**
   * This method is called when the player clicks the investigation log button. It changes the
   * viewable scene to the investigation log view.
   *
   * @throws IOException if the FXML file is not found
   */
  public void toInvestigationLog() throws IOException {
    App.setRoot("investigationLog");
  }

  /**
   * This method is called when the player clicks the back button. It changes the viewable scene to
   * the crime scene.
   *
   * @throws IOException if the FXML file is not found
   */
  public void backToEvidence() throws IOException {
    App.setRoot("evidence");
  }

  /**
   * This method is called when the player clicks the shut down button. It changes the viewable
   * scene to the crime scene.
   *
   * @param event the action event triggered by clicking the exit button
   * @throws IOException if the FXML file is not found
   */
  @FXML
  private void onClickShutDown(ActionEvent event) throws IOException {
    suspectFingerprintPane.setVisible(false);
    vaultFingerprintPane.setVisible(false);

    Parent crimeSceneRoot = SceneManager.getUiRoot(SceneManager.AppUi.CRIME_SCENE);
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    stage.getScene().setRoot(crimeSceneRoot);
  }
}
