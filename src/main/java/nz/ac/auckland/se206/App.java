package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.CrimeSceneController;
import nz.ac.auckland.se206.controllers.SuspectRoomController;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {

  private static Scene scene;
  private static FXMLLoader loader;

  /**
   * The main method that launches the JavaFX application.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch();
  }

  /**
   * Sets the root of the scene to the specified FXML file.
   *
   * @param fxml the name of the FXML file (without extension)
   * @throws IOException if the FXML file is not found
   */
  public static void setRoot(String fxml) throws IOException {

    scene.setRoot(loadFxml(fxml));
    SceneManager.reInitializeCrimeScene(fxml);
  }

  /**
   * Loads the FXML file and returns the associated node. The method expects that the file is
   * located in "src/main/resources/fxml".
   *
   * @param fxml the name of the FXML file (without extension)
   * @return the root node of the FXML file
   * @throws IOException if the FXML file is not found
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    loader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
    return loader.load();
  }

  /**
   * Opens the chat view and sets the profession in the chat controller.
   *
   * @param event the mouse event that triggered the method
   * @param profession the profession to set in the chat controller
   * @throws IOException if the FXML file is not found
   */
  public static void openChat(MouseEvent event, String profession) throws IOException {

    SuspectRoomController suspectRoomController = loader.getController();
    suspectRoomController.setProfession(profession);
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "room" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/room.fxml" file is not found
   */
  @Override
  public void start(final Stage stage) throws IOException {
    Platform.setImplicitExit(false);

    SceneManager.addUi(SceneManager.AppUi.CRIME_SCENE, "crimeScene");
    SceneManager.addUi(SceneManager.AppUi.RULEBOOK, ("ruleBook"));
    SceneManager.addUi(SceneManager.AppUi.START, ("start"));
    SceneManager.addUi(SceneManager.AppUi.COP, ("copRoom"));
    SceneManager.addUi(SceneManager.AppUi.JANITOR, ("janitorRoom"));
    SceneManager.addUi(SceneManager.AppUi.BANK_MANAGER, ("bankManagerRoom"));
    SceneManager.addUi(SceneManager.AppUi.INTRO_BANK, ("introBank"));
    SceneManager.addUi(SceneManager.AppUi.INTRO_UNTIL, ("introUntil"));
    SceneManager.addUi(SceneManager.AppUi.INTRO_PHONE, ("introPhone"));

    Parent root = SceneManager.getUiRoot(SceneManager.AppUi.START);
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    root.requestFocus();

    stage.setOnCloseRequest(event -> Platform.exit());
  }

  /**
   * Restarts the application by clearing the visited rooms and deleting the scene manager. It then
   * adds all the UIs to the scene manager again, so that they can be accessed again freshly.
   * Finally, it sets the root of the scene to the "start" FXML file.
   *
   * @throws IOException if the FXML file is not found
   */
  public static void restartApp() throws IOException {
    CrimeSceneController.visitedRooms.clear();
    SceneManager.delete();
    // Adds all the UIs to the scene manager again, so that they can be accessed again freshly
    SceneManager.addUi(SceneManager.AppUi.CRIME_SCENE, "crimeScene");
    SceneManager.addUi(SceneManager.AppUi.RULEBOOK, ("ruleBook"));
    SceneManager.addUi(SceneManager.AppUi.COP, ("copRoom"));
    SceneManager.addUi(SceneManager.AppUi.JANITOR, ("janitorRoom"));
    SceneManager.addUi(SceneManager.AppUi.BANK_MANAGER, ("bankManagerRoom"));

    setRoot("start");
  }
}
