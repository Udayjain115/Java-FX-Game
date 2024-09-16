package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class RulebookController {

    @FXML
    private Pane room;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private ImageView book;

    @FXML
    private ImageView ruletext;

    @FXML
    private Canvas canvas;

    private GraphicsContext gc;

    @FXML
    public void initialize() {
        // Initialize the canvas
        gc = canvas.getGraphicsContext2D();

        // Draw a covering rectangle over the canvas (simulating dust)
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Enable erasing on the canvas with mouse events
        canvas.setOnMouseDragged(this::handleErase);
    }

    // This method handles erasing parts of the canvas to reveal the ruletext.png image
    private void handleErase(MouseEvent event) {
        // Erase on the canvas at the mouse pointer
        double size = 15; // Size of the "eraser"
        double x = event.getX() - size / 2;
        double y = event.getY() - size / 2;

        // Clear the specific area on the canvas
        gc.clearRect(x, y, size, size);

        // Check if most of the canvas has been cleared to hide the canvas completely
        if (isCanvasCleared()) {
            canvas.setVisible(false); // Hide the canvas when enough is erased
        }
    }

    // This method checks if the canvas is mostly cleared
    private boolean isCanvasCleared() {
        // Scan the pixels of the canvas to check how much has been erased
        Image snapshot = canvas.snapshot(null, null);
        int clearedPixels = 0;
        int totalPixels = (int) (canvas.getWidth() * canvas.getHeight());

        // Check how many pixels are transparent
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                if (snapshot.getPixelReader().getArgb(x, y) == 0x00000000) { // Transparent pixel
                    clearedPixels++;
                }
            }
        }

        // If more than 90% of the canvas is cleared, return true
        return clearedPixels > totalPixels * 0.9;
    }
}
