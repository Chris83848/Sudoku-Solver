package sudoku.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sudoku.logic.SudokuBoard;



public class LoadPuzzleScreen {

    private static final int GRID_SIZE = 9;
    private TextField[][] cells = new TextField[GRID_SIZE][GRID_SIZE];

    public Scene getScene(Stage load) {
        BorderPane grid = new BorderPane();

        // Title
        Label title = new Label("Input Your Sudoku Puzzle");
        title.setStyle("-fx-font-size: 24px; -fx-padding: 20px;");
        grid.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        // Sudoku Grid
        GridPane board = new GridPane();
        board.setPadding(new Insets(30));
        board.setHgap(0);
        board.setVgap(0);
        board.setAlignment(Pos.CENTER);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                TextField field = new TextField();
                field.setPrefWidth(60);
                field.setPrefHeight(60);
                field.setAlignment(Pos.CENTER);
                field.setStyle("-fx-font-size: 30px;");

                // Limit input to 1-9
                field.setTextFormatter(new TextFormatter<String>(change -> {
                    if (change.getControlNewText().matches("[1-9]?")) {
                        return change;
                    }
                    return null;
                }));

                // Borders for grid lines
                StringBuilder borderStyle = new StringBuilder("-fx-border-color: black; ");

                // Thicker borders for 3x3 subgrids
                borderStyle.append("-fx-border-width: ");
                borderStyle.append((row % 3 == 0) ? "3px " : "1px "); // top
                borderStyle.append((column == GRID_SIZE - 1) ? "3px " : ((column + 1) % 3 == 0) ? "3px " : "1px "); // right
                borderStyle.append((row == GRID_SIZE - 1) ? "3px " : ((row + 1) % 3 == 0) ? "3px " : "1px "); // bottom
                borderStyle.append((column % 3 == 0) ? "3px;" : "1px;"); // left

                field.setStyle(field.getStyle() + borderStyle.toString());

                cells[row][column] = field;
                board.add(field, column, row);
            }
        }

        grid.setCenter(board);

        // Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setPrefWidth(120);
        submitButton.setStyle("-fx-font-size: 16px;");

        Button backButton = new Button("Back");
        backButton.setPrefWidth(60);

        submitButton.setOnAction(e -> {
            SudokuBoard inputtedPuzzle = handleSubmit();
            if (inputtedPuzzle != null) {
                // load next screen
            }
        });

        backButton.setOnAction(e -> {
            HomeScreen homeScreen = new HomeScreen();
            load.setScene(homeScreen.getScene(load));
            load.setMaximized(true);
        });

        VBox bottomBox = new VBox(backButton, submitButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));

        grid.setBottom(bottomBox);

        return new Scene(grid, 600, 700);
    }

    private SudokuBoard handleSubmit() {
        int[][] userPuzzle = new int[9][9];
        try {
            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < 9; column++) {
                    String cell = cells[row][column].getText();
                    if (cell.isEmpty()) {
                        userPuzzle[row][column] = 0;
                    } else {
                        int value = Integer.parseInt(cell);
                        userPuzzle[row][column] = value;
                    }
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "Please enter numbers 1-9 only.");
        }
        SudokuBoard userBoard = new SudokuBoard(userPuzzle);
        if (!userBoard.isCorrect()) {
            showAlert("Invalid puzzle", "Your puzzle has an error.");
            return null;
        } else if (!userBoard.isSolvable()) {
            showAlert("Invalid puzzle", "Your puzzle is too difficult or has more than one solution.");
            return null;
        }
        return userBoard;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


