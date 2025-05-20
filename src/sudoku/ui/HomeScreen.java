package sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeScreen {

    public Scene getScene(Stage home) {
        Button loadButton = new Button("Solve Own Puzzle");
        Button generateButton = new Button("Solve Random Puzzle");

        generateButton.setOnAction(e -> {
            DifficultySelectionScreen difficultySelectionScreen = new DifficultySelectionScreen();
            home.setScene(difficultySelectionScreen.getScene(home));
            home.setMaximized(true);
        });

        VBox layout = new VBox(20, loadButton, generateButton);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 800, 600);
    }
}
