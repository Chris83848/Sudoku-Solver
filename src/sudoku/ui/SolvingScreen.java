package sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sudoku.logic.SudokuSolverApplication;
import sudoku.logic.SudokuUtils;

public class SolvingScreen {

    private Pane[][] cells = new Pane[9][9];
    private Pane selectedCell = null;
    public Scene getScene(Stage solve, int[][] puzzle) {

        // Make copy of puzzle to then make solved puzzle from it
        int[][] tempPuzzle = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                tempPuzzle[i][j] = puzzle[i][j];
            }
        }
        int[][] solvedPuzzle = SudokuSolverApplication.solveRecursively(tempPuzzle);

        // Initialize basic board and number pad components
        GridPane board = UIComponents.createSudokuBoard();
        GridPane numberPad = UIComponents.createNumberPad();

        // Loops through given puzzle and map to board UI
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                int value = puzzle[row][column];

                // Lock cells that already have assigned values
                if (value != 0) {
                    Label lockedCell = new Label(String.valueOf(value));
                    lockedCell.setStyle("-fx-font-size: 30;");

                    // Put in wrapper to maintai board borders
                    StackPane wrapper = new StackPane(lockedCell);
                    wrapper.setPrefSize(60, 60);
                    wrapper.setStyle(
                            "-fx-background-color: #d3d3d3;" +
                                    "-fx-border-color: black;" +
                                    "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";"
                    );
                    cells[row][column] = wrapper;
                    board.add(wrapper, column, row);

                } else {

                    // Create panes for editable cells
                    Pane cell = new Pane();
                    cell.setPrefSize(60, 60);
                    cell.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" +
                            "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";");
                    cells[row][column] = cell;

                    // Map coordinates to each cell
                    cell.setUserData(new int[]{row, column});
                    final int finalRow = row;
                    final int finalColumn = column;

                    // Add highlighting feature to cells
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
                                "-fx-font-size: 20;" + "-fx-background-color: lightblue; ");
                    });

                    board.add(cell, column, row);
                }
            }
        }

        // Create buttons for numbers 1-9 and for number pad
        for (int num = 1; num <= 9; num++) {

            // Create button
            Button numButton = new Button(String.valueOf(num));
            numButton.setPrefSize(60, 60);
            numButton.setStyle("-fx-font-size: 18;");

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

                    // Convert inputted puzzle into 2d array
                    // CHECK CODE LATER
                    int[][] currentPuzzle = new int[9][9];

                    for (int i = 0; i < cells.length; i++) {
                        for (int j = 0; j < cells[i].length; j++) {
                            currentPuzzle[i][j] = 0; // default value

                            for (javafx.scene.Node node : cells[i][j].getChildren()) {
                                if (node instanceof Label label) {
                                    String text = label.getText().trim();
                                    if (!text.isEmpty()) {
                                        try {
                                            currentPuzzle[i][j] = Integer.parseInt(text);
                                        } catch (NumberFormatException x) {
                                            System.out.println("Invalid number in cell [" + i + "][" + j + "]: " + text);
                                        }
                                    }
                                    break; // Stop after finding first Label
                                }
                            }
                        }
                    }


                    // CLEAN UP AND OPTIMIZE
                    if (UIComponents.arraysAreEqual(currentPuzzle, solvedPuzzle)) {
                        System.out.println("Checking if puzzle is solved.");
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("yay");
                        success.setHeaderText(null);
                        success.setContentText("You win");
                        success.showAndWait();
                    }
                }
            });

            numberPad.add(numButton, column, row);
        }

        // Create clear button and add to last slot in number pad
        Button clearButton = new Button("X");
        clearButton.setPrefSize(60, 60);
        clearButton.setStyle("-fx-font-size: 20; -fx-text-fill: red;");

        // Delete number from highlighted square when clicked
        clearButton.setOnAction(e -> {
            if (selectedCell != null) {
                selectedCell.getChildren().clear();
            }
        });
        numberPad.add(clearButton, 4, 1);

        VBox layout = new VBox(20, board, numberPad);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 800, 600);
    }
}
