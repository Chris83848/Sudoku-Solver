package sudoku.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeScreen {

    public Scene getScene(Stage home) {

        // Create title label and stylize
        Label titleLabel = new Label("Sudoku");
        titleLabel.setStyle(
                "-fx-font-size: 48px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #00264d;" +
                        "-fx-font-family: 'Segoe UI';"
        );

        // Create subheading label and stylize
        Label subheadingLabel = new Label("Choose Your Puzzle:");
        subheadingLabel.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-text-fill: #003366;" +
                        "-fx-font-family: 'Arial';"
        );

        // Create buttons for solving different puzzles and stylize
        Button loadButton = new Button("Solve Own Puzzle");
        Button generateButton = new Button("Solve Random Puzzle");

        String buttonStyle =
                "-fx-font-size: 18px;" +
                        "-fx-font-family: 'Verdana';" +
                        "-fx-background-color: #007acc;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 24;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 3);" +
                        "-fx-cursor: hand;";

        loadButton.setStyle(buttonStyle);
        generateButton.setStyle(buttonStyle);

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

        // Set button layout
        HBox buttonBox = new HBox(40, loadButton, generateButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Set main layout and return scene
        VBox layout = new VBox(40, titleLabel, subheadingLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(60));
        layout.setStyle("-fx-background-color: #b3daff;"); // Blue background

        return new Scene(layout, 800, 600);
    }
}