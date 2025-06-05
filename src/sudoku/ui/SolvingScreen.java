package sudoku.ui;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SolvingScreen {
    public Scene getScene(Stage solve, int[][] puzzle) {
        VBox test = new VBox();
        return new Scene(test);
    }
}
