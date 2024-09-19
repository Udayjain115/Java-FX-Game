package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class SceneManager {
  public enum AppUi {
    START,
    RULEBOOK,
    CRIME_SCENE,
    COP,
    JANITOR,
    BANK_MANAGER,
  }

  private static HashMap<AppUi, Parent> sceneMap = new HashMap<>();
  private static HashMap<AppUi, Object> controllerMap = new HashMap<>();

  public static void addUi(AppUi appUi, String fxml) throws IOException {
    if (!sceneMap.containsKey(appUi)) {
      FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + fxml + ".fxml"));
      Parent root = loader.load();
      sceneMap.put(appUi, root);
      controllerMap.put(appUi, loader.getController());
    }
  }

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

  public static void reInitializeCrimeScene(String fxml) {}
}
