package sudoku.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sudoku.logic.SudokuBoard;
import sudoku.logic.SudokuBoardGenerator;

public class LoadPuzzleScreen {

    private Pane[][] cells = new Pane[9][9];
    private Pane selectedCell = null;

    public Scene getScene(Stage load) {

        // Create title of screen
        Label title = new Label("Input Your Puzzle Below");
        title.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-text-fill: #000000;");
        title.setAlignment(Pos.CENTER);

        // Create back button to go back to home screen
        Button backButton = new Button("Back");

        backButton.setStyle("-fx-font-size: 14; -fx-background-color: transparent; -fx-text-fill: #00264d; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
        backButton.setOnMouseEntered(e -> backButton.setUnderline(true));
        backButton.setOnMouseExited(e -> backButton.setUnderline(false));

        backButton.setOnAction(e -> {
            load.setScene(new HomeScreen().getScene(load));
        });

        Button submitButton = new Button("Submit");
        submitButton.setPrefSize(110, 40);

        submitButton.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-family: 'Verdana';" +
                        "-fx-background-color: #000000;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 24;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 3);" +
                        "-fx-cursor: hand;"
        );


        // Create grid pane for the sudoku board, including size and style
        GridPane board = UIComponents.createSudokuBoard();

        // Create cells for the 81 sudoku squares on the board, setting size and style
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Pane cell = new Pane();
                cell.setPrefSize(60, 60);
                cell.setStyle("-fx-border-color: black;" +
                        "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";" +
                        "-fx-font-size: 20;" + "-fx-background-color: white; ");
                cell.setCursor(Cursor.DEFAULT);

                cells[row][column] = cell;

                // Map coordinates to each cell
                cell.setUserData(new int[]{row, column});
                final int finalRow = row;
                final int finalColumn = column;

                // Wire highlight features to cells
                cell.setOnMouseClicked(e -> {
                    if (selectedCell != null) {
                        // Gather coordinates of previous highlighted cell
                        int[] previousCoordinates = (int[]) selectedCell.getUserData();
                        int previousRow = previousCoordinates[0];
                        int previousColumn = previousCoordinates[1];

                        // Unhighlight previous selected cell and set it back to normal using coordinates
                        selectedCell.setStyle("-fx-border-color: black;" +
                                "-fx-border-width: " + UIComponents.getBorderWidth(previousRow, previousColumn) + ";" +
                                "-fx-font-size: 20;" + "-fx-background-color: white; ");
                    }

                    // Highlight current selected cell
                    selectedCell = cell;
                    cell.setStyle("-fx-border-color: black;" +
                            "-fx-border-width: " + UIComponents.getBorderWidth(finalRow, finalColumn) + ";" +
                            "-fx-font-size: 20;" + "-fx-background-color: #00BFFF; ");
                });

                board.add(cell, column, row);
            }
        }

        // Create grid pane for number pad
        GridPane numberPad = UIComponents.createNumberPad();

        // Create buttons for numbers 1-9 and for number pad
        for (int num = 1; num <= 9; num++) {
            // Create button
            Button numButton = new Button(String.valueOf(num));
            numButton.setPrefSize(60, 60);

            numButton.setStyle(
                        "-fx-font-size: 24;" +
                            "-fx-background-color: white; " +
                            "-fx-text-fill: black; " +
                            "-fx-border-color: #7cfeff; " +
                            "-fx-border-radius: 3; " +
                            "-fx-background-radius: 3;"
            );

            numButton.setOnMouseEntered(eh -> numButton.setStyle(
                    "-fx-font-size: 24;" +
                            "-fx-background-color: #E0F7FF; " +
                            "-fx-text-fill: black; " +
                            "-fx-border-color: #7cfeff; " +
                            "-fx-border-radius: 3; " +
                            "-fx-background-radius: 3;"
            ));

            numButton.setOnMouseExited(eh -> numButton.setStyle(
                    "-fx-font-size: 24;" +
                            "-fx-background-color: white; " +
                            "-fx-text-fill: black; " +
                            "-fx-border-color: #7cfeff; " +
                            "-fx-border-radius: 3; " +
                            "-fx-background-radius: 3;"
            ));


            // Determine coordinates for number pad
            int row = (num <= 5) ? 0 : 1;
            int column = (num <= 5) ? (num - 1) : (num - 6);

            // Input number into highlighted square when button is clicked
            numButton.setOnAction(e -> {
                if (selectedCell != null) {
                    selectedCell.getChildren().clear();

                    Label numInput = new Label(numButton.getText());
                    numInput.setStyle("-fx-font-size: 30;");
                    numInput.setAlignment(Pos.CENTER);
                    numInput.setPrefSize(60, 60);

                    selectedCell.getChildren().add(numInput);
                }
            });

            numberPad.add(numButton, column, row);
        }

        // Create clear button and add to last slot in number pad
        Button clearButton = new Button("X");
        clearButton.setPrefSize(60, 60);

        clearButton.setStyle(
                "-fx-font-size: 24; " +
                        "-fx-text-fill: red; " +
                        "-fx-background-color: white; " +
                        "-fx-font-family: 'Courier New'; " +
                        "-fx-border-color: #DC143C; " +
                        "-fx-border-radius: 3; " +
                        "-fx-background-radius: 3;"
        );
        clearButton.setOnMouseEntered(eh -> clearButton.setStyle(
                "-fx-font-size: 24; " +
                        "-fx-text-fill: red; " +
                        "-fx-background-color: #ffd6d6; " +
                        "-fx-font-family: 'Courier New'; " +
                        "-fx-border-color: #DC143C; " +
                        "-fx-border-radius: 3; " +
                        "-fx-background-radius: 3;"
        ));

        clearButton.setOnMouseExited(eh -> clearButton.setStyle(
                "-fx-font-size: 24; " +
                        "-fx-text-fill: red; " +
                        "-fx-background-color: white; " +
                        "-fx-font-family: 'Courier New'; " +
                        "-fx-border-color: #DC143C; " +
                        "-fx-border-radius: 3; " +
                        "-fx-background-radius: 3;"
        ));


        // Delete number from highlighted square when clicked
        clearButton.setOnAction(e -> {
            if (selectedCell != null) {
                selectedCell.getChildren().clear();
            }
        });

        numberPad.add(clearButton, 4, 1);

        // When submit button is clicked, validate puzzle and move to solving screen if valid
        submitButton.setOnAction(e -> {

            // Convert inputted puzzle into 2d array
            int[][] userPuzzle = new int[9][9];
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells.length; j++) {
                    if (cells[i][j].getChildren().isEmpty()) {
                        userPuzzle[i][j] = 0;
                    } else {
                        Label value = (Label) cells[i][j].getChildren().get(0);
                        userPuzzle[i][j] = Integer.parseInt(value.getText());
                    }
                }
            }

            // Create sudoku board class to validate
            SudokuBoard userBoard = new SudokuBoard(userPuzzle);

            // Prepare potential error message
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Invalid Puzzle");
            errorAlert.setHeaderText(null);

            // Check if the board breaks any sudoku rules and is solvable. If not, send to solving screen
            if (!userBoard.isCorrect()) {
                errorAlert.setContentText("The puzzle is not valid. Check for duplicate numbers in the same row, " +
                        "column, or subgrid.");
                errorAlert.showAndWait();
            } else if (!userBoard.isSolvable()) {
                errorAlert.setContentText("The puzzle is not solvable. It either is too difficult or has more " +
                        "than one solution.");
                errorAlert.showAndWait();
            } else {
                SolvingScreen solvingScreen = new SolvingScreen();
                load.setScene(solvingScreen.getScene(load, userPuzzle, true));
                load.setMaximized(true);
            }
        });

        // Create layout containing only board and submit button in order to control centralization and padding
        HBox middleLayout = new HBox(20, board, submitButton);
        middleLayout.setAlignment(Pos.CENTER);
        middleLayout.setPadding(new Insets(0, 0, 0, 100));

        // Set whole layout and return screen
        VBox coreLayout = new VBox(20, title, middleLayout, numberPad);
        coreLayout.setAlignment(Pos.CENTER);
        BorderPane overallLayout = new BorderPane();
        overallLayout.setTop(backButton);
        overallLayout.setCenter(coreLayout);
        BorderPane.setAlignment(backButton, Pos.TOP_LEFT);
        BorderPane.setMargin(backButton, new javafx.geometry.Insets(10));

        overallLayout.setStyle("-fx-background-color: #3593ff;");

        return new Scene(overallLayout, 800, 600);
    }
}