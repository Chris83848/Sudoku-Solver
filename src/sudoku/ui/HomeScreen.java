package sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeScreen {

    public Scene getScene(Stage home) {

        // Create buttons for solving different puzzles
        Button loadButton = new Button("Solve Own Puzzle");
        Button generateButton = new Button("Solve Random Puzzle");

        // Call difficulty screen when generate button is clicked
        generateButton.setOnAction(e -> {
            DifficultySelectionScreen difficultySelectionScreen = new DifficultySelectionScreen();
            home.setScene(difficultySelectionScreen.getScene(home));
            home.setMaximized(true);
        });

        // Call load screen when load button is clicked
        loadButton.setOnAction(e -> {
            LoadPuzzleScreen loadPuzzleScreen = new LoadPuzzleScreen();
            home.setScene(loadPuzzleScreen.getScene(home));
            home.setMaximized(true);
        });

        // Set layout and return screen
        VBox layout = new VBox(20, loadButton, generateButton);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 800, 600);
    }
}
