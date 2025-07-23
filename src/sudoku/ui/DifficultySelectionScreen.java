package sudoku.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sudoku.logic.SudokuBoardGenerator;

public class DifficultySelectionScreen {

    public Scene getScene(Stage difficulties) {

        // Create title and stylize
        Label label = new Label("Select Difficulty");
        label.setStyle("-fx-font-size: 36px; -fx-font-family: 'Segoe UI'; -fx-text-fill: #000000; -fx-font-weight: bold");

        // Create buttons for different difficulties
        Button easyButton = new Button("Easy");
        Button mediumButton = new Button("Medium");
        Button hardButton = new Button("Hard");
        Button backButton = new Button("Back");

        // Style buttons
        styleButton(easyButton);
        styleButton(mediumButton);
        styleButton(hardButton);
        styleButton(backButton);

        // Create easy sudoku puzzle and send to solving screen
        easyButton.setOnAction(e -> {
            int[][] easyPuzzle = SudokuBoardGenerator.generateEasyBoardPuzzle();
            SolvingScreen solvingScreen = new SolvingScreen();
            difficulties.setScene(solvingScreen.getScene(difficulties, easyPuzzle, false));
            difficulties.setMaximized(true);
        });

        // Create medium sudoku puzzle and send to solving screen
        mediumButton.setOnAction(e -> {
            int[][] mediumPuzzle = SudokuBoardGenerator.generateMediumBoardPuzzle();
            SolvingScreen solvingScreen = new SolvingScreen();
            difficulties.setScene(solvingScreen.getScene(difficulties, mediumPuzzle, false));
            difficulties.setMaximized(true);
        });

        // Call previous screen (home screen) when back button is clicked
        backButton.setOnAction(e -> {
            HomeScreen homeScreen = new HomeScreen();
            difficulties.setScene(homeScreen.getScene(difficulties));
            difficulties.setMaximized(true);
        });

        // Disable hard button for now
        hardButton.setDisable(true);
        Label note = new Label("*Hard mode coming soon*");
        note.setStyle("-fx-font-size: 14px; -fx-font-family: 'Segoe UI'; -fx-text-fill: red; -fx-font-weight: bold");


        // Set layouts and return screen
        HBox hard = new HBox(10, hardButton, note);
        hard.setAlignment(Pos.CENTER);
        hard.setPadding(new Insets(0, 0, 0, 180));

        VBox layout = new VBox(20, label, easyButton, mediumButton, hard, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        StackPane root = new StackPane(layout);
        root.setStyle("-fx-background-color: #3593ff;"); // Soft Azure

        return new Scene(root, 800, 600);
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-font-size: 18px;" +
                "-fx-font-family: 'Verdana';" +
                "-fx-background-color: #000000;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 24;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 3);" +
                "-fx-cursor: hand;");
    }
}

