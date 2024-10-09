package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * This class is the scene manager for the application. It is a singleton class that manages the
 * different UIs in the application. It has a method to add a UI to the scene manager, a method to
 * get the root node of the UI for a specified AppUi, and a method to get the controller for a
 * specified AppUi.
 */
public class SceneManager {

  /** Enum to represent the different UIs in the application. */
  public enum AppUi {
    START,
    RULEBOOK,
    CRIME_SCENE,
    COP,
    JANITOR,
    BANK_MANAGER,
    INTRO_BANK,
    INTRO_UNTIL,
    INTRO_PHONE
  }

  private static HashMap<AppUi, Parent> sceneMap = new HashMap<>();
  private static HashMap<AppUi, Object> controllerMap = new HashMap<>();

  /**
   * Adds the UI for the specified AppUi to the scene manager.
   *
   * @param appUi the AppUi to add the UI for
   * @param fxml the FXML file to add
   * @throws IOException if the FXML file is not found
   */
  public static void addUi(AppUi appUi, String fxml) throws IOException {
    if (!sceneMap.containsKey(appUi)) {
      FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + fxml + ".fxml"));
      Parent root = loader.load();
      sceneMap.put(appUi, root);
      controllerMap.put(appUi, loader.getController());
    }
  }

  /**
   * Returns the root node of the UI for the specified AppUi.
   *
   * @param appUi the AppUi to get the root node for
   * @return the root node of the UI for the specified AppUi
   */
  public static Parent getUiRoot(AppUi appUi) {
    Parent uiRoot = sceneMap.get(appUi);
    if (uiRoot == null) {
      throw new IllegalArgumentException("No scene found for " + appUi);
    }
    return uiRoot;
  }

  public static Object getController(AppUi appUi) {
    return controllerMap.get(appUi);
  }

  public static void delete() {
    sceneMap.clear();
    controllerMap.clear();
  }

  /*
   * Sets the root node of the scene to the specified AppUi.
   */
  public static void reInitializeCrimeScene(String fxml) {}
}
