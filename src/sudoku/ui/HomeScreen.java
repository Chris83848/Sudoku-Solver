package sudoku.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeScreen {

    public Scene getScene(Stage home) {

        // Create title label
        Label titleLabel = new Label("Sudoku Game");
        titleLabel.getStyleClass().add("title-label");

        // Create buttons for solving different puzzles
        Button loadButton = new Button("Solve Own Puzzle");
        Button generateButton = new Button("Solve Random Puzzle");

        loadButton.getStyleClass().add("home-button");
        generateButton.getStyleClass().add("home-button");

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
        VBox layout = new VBox(30, titleLabel, loadButton, generateButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(50));
        layout.getStyleClass().add("home-root");

        Scene scene = new Scene(layout, 800, 600);


        return scene;
    }
}