package nz.ac.auckland.se206;

import java.util.HashMap;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import nz.ac.auckland.se206.controllers.CrimeSceneController;

import java.io.IOException;

public class SceneManager {
    public enum AppUi {
        START,
        RULEBOOK,
        CRIME_SCENE,
    }
    private static boolean isCrimeSceneOpened = false;
    private static HashMap<AppUi, Parent> sceneMap = new HashMap<>();

    public static void addUi(AppUi appUi, String fxml) throws IOException {
        if(!sceneMap.containsKey(appUi)) {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + fxml + ".fxml"));
        Parent root = loader.load();
        sceneMap.put(appUi, root);

        }
        
    }

    public static Parent getUiRoot(AppUi appUi) {
        Parent uiRoot = sceneMap.get(appUi);
        if(uiRoot == null) {
            throw new IllegalArgumentException("No scene found for " + appUi);
        }
        return uiRoot;
    }

    public static void reInitializeCrimeScene(String fxml) {
    //     System.out.println("hi");
    //     if(isCrimeSceneOpened) {
    //     if (fxml.equals("crimeScene"))  {
    //        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/crimeScene.fxml"));
    //         CrimeSceneController controller = loader.getController();
    //         System.out.println("reinitializing crime scene");
    //         controller.checkGuess();
    //     }
    // }
    // isCrimeSceneOpened = true;
    }
    

 


    
}