package sudoku.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sudoku.logic.SudokuSolverApplication;

public class SolvingScreen {

    private Pane[][] cells = new Pane[9][9];
    private Pane selectedCell = null;
    public Scene getScene(Stage solve, int[][] puzzle, boolean custom) {

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

                    // Put in wrapper to maintain board borders
                    StackPane wrapper = new StackPane(lockedCell);
                    wrapper.setPrefSize(60, 60);
                    wrapper.setStyle(
                            "-fx-background-color: #d3d3d3;" +
                                    "-fx-border-color: black;" +
                                    "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";"
                    );

                    // Tag cells as locked
                    wrapper.setUserData("locked");

                    int finalRow1 = row;
                    int finalColumn1 = column;

                    // Update selected cell when clicked and adjust highlights accordingly
                    wrapper.setOnMouseClicked(e -> {

                        // Reset cell styles
                        if (selectedCell != null) {
                            resetCells();
                        }

                        selectedCell = wrapper;
                        updateHighlights(wrapper, finalRow1, finalColumn1);
                    });
                    cells[row][column] = wrapper;
                    board.add(wrapper, column, row);

                } else {

                    // Create panes for editable cells
                    Pane cell = new Pane();
                    cell.setPrefSize(60, 60);
                    cell.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" +
                            "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";");
                    cell.setUserData("free");
                    cells[row][column] = cell;

                    // Map coordinates to each cell
                    cell.setUserData(new int[]{row, column});

                    // Add highlighting feature to cells
                    int finalRow = row;
                    int finalColumn = column;
                    cell.setOnMouseClicked(e -> {
                        if (selectedCell != null) {
                            resetCells();
                        }

                        selectedCell = cell;
                        updateHighlights(cell, finalRow, finalColumn);
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
            int finalNum = num;
            numButton.setOnAction(e -> {
                if (selectedCell != null && !selectedCell.getUserData().equals("locked")) {

                    // Clear cell
                    selectedCell.getChildren().clear();

                    // Replace with new number
                    Label numInput = new Label(numButton.getText());
                    numInput.setStyle("-fx-font-size: 30;");
                    numInput.setAlignment(Pos.CENTER);
                    numInput.setPrefSize(60, 60);

                    // Get coordinates and update highlights accordingly
                    int[] coords = findCellCoordinates(selectedCell);
                    int currentRow = coords[0];
                    int currentColumn = coords[1];
                    selectedCell.getChildren().add(numInput);
                    resetCells();
                    updateHighlights(selectedCell, currentRow, currentColumn);


                    // Convert inputted puzzle into 2d array
                    // CHECK CODE LATER and ask chatgpt
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
            if (selectedCell != null && !selectedCell.getUserData().equals("locked")) {

                // Find coordinates, update highlights, and clear cell
                int[] coords = findCellCoordinates(selectedCell);
                resetCells();
                addVisibleHighlights(coords[0], coords[1]);
                selectedCell.getChildren().clear();
                selectedCell.setStyle("-fx-border-color: black;" +
                        "-fx-border-width: " + UIComponents.getBorderWidth(coords[0], coords[1]) + ";" +
                        "-fx-font-size: 20;" + "-fx-background-color: #00BFFF; ");
            }
        });
        numberPad.add(clearButton, 4, 1);


        // Create column for extra buttons to go on
        VBox buttonColumn = new VBox(10); // spacing of 10 between buttons
        buttonColumn.setAlignment(Pos.TOP_CENTER); // align buttons to top-center

        // Create check cell button and format
        Button checkCell = new Button("Check Cell");
        checkCell.setPrefSize(80, 40); // Small rectangular buttons
        checkCell.setStyle("-fx-font-size: 12;");
        buttonColumn.getChildren().add(checkCell);

        //
        checkCell.setOnAction(e -> {

        });





        // Format middle layout
        HBox middleLayout = new HBox(30, board, buttonColumn);
        middleLayout.setAlignment(Pos.CENTER);
        middleLayout.setPadding(new Insets(0, 0, 0, 100));

        // Wrap everything in vertical layout
        VBox layout = new VBox(20, middleLayout, numberPad);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 800, 600);
    }


    // Add visible highlights as well as number highlights if needed
    private void updateHighlights(Pane cell, int currentRow, int currentColumn) {
        addVisibleHighlights(currentRow, currentColumn);
        int cellValue = getCellValue(cell);
        if (cellValue >= 1) {
            highlightNumbers(cellValue);
        }
        cell.setStyle("-fx-border-color: black;" +
                "-fx-border-width: " + UIComponents.getBorderWidth(currentRow, currentColumn) + ";" +
                "-fx-font-size: 20;" + "-fx-background-color: #00BFFF; ");
    }

    // Highlight cells in the same row, column, or subgrid of given coordinates
    private void addVisibleHighlights(int currentRow, int currentColumn) {

        // Highlight row
        for (int row = 0; row < 9; row++) {
            Pane cell = cells[row][currentColumn];
            cell.setStyle("-fx-border-color: black;" +
                    "-fx-border-width: " + UIComponents.getBorderWidth(row, currentColumn) + ";" +
                    "-fx-font-size: 20;" + "-fx-background-color: lightblue; ");
        }

        // Highlight column
        for (int column = 0; column < 9; column++) {
            Pane cell = cells[currentRow][column];
            cell.setStyle("-fx-border-color: black;" +
                    "-fx-border-width: " + UIComponents.getBorderWidth(currentRow, column) + ";" +
                    "-fx-font-size: 20;" + "-fx-background-color: lightblue; ");
        }

        // Highlight subgrid
        int rowStart = (currentRow / 3) * 3;
        int columnStart = (currentColumn / 3) * 3;

        for (int row = rowStart; row <= rowStart + 2; row++) {
            for (int column = columnStart; column <= columnStart + 2; column++) {
                Pane cell = cells[row][column];
                cell.setStyle("-fx-border-color: black;" +
                        "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";" +
                        "-fx-font-size: 20;" + "-fx-background-color: lightblue; ");
            }
        }

    }

    // Highlight identical numbers in puzzle
    private void highlightNumbers(int num) {

        // Loop through board and highlight the same numbers
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Pane cell = cells[row][column];
                int value = getCellValue(cell);
                if (value == num) {
                    cell.setStyle("-fx-border-color: black;" +
                            "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";" +
                            "-fx-font-size: 20;" + "-fx-background-color: #7CD6F5; ");
                }
            }
        }
    }

    // Return the value inside of a cell
    private int getCellValue(Pane cell) {
        int cellValue = -1;
        if (!cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof Label) {
            Label label = (Label) cell.getChildren().get(0);
            String text = label.getText();

            if (!text.isEmpty()) {
                try {
                    cellValue = Integer.parseInt(text);
                } catch (NumberFormatException e) {

                }
            }
        }
        return cellValue;
    }

    // Set cell back to original highlights--white or grey depending on locked or not
    private void resetCellStyle(int row, int column) {
        Pane cell = cells[row][column];

        String backgroundColor = "white";
        if (cell.getUserData() == "locked") {
            backgroundColor = "#d3d3d3";
        }

        cell.setStyle("-fx-border-color: black;" +
                "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";" +
                "-fx-font-size: 20;" +
                "-fx-background-color: " + backgroundColor + ";");
    }

    // Reset styles of all cells in puzzle
    private void resetCells() {
        // Unhighlight cell and set it back to normal using coordinates
        for (int currentRow = 0; currentRow < 9; currentRow++) {
            for (int currentColumn = 0; currentColumn < 9; currentColumn++) {
                resetCellStyle(currentRow, currentColumn);
            }
        }
    }

    private void unhighlightNumbers(int num) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Pane cell = cells[row][column];
                int value = getCellValue(cell);
                if (value == num) {
                    String backgroundColor = "white";
                    if (cell.getUserData() == "locked") {
                        backgroundColor = "#d3d3d3";
                    }
                    cell.setStyle("-fx-border-color: black;" +
                            "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";" +
                            "-fx-font-size: 20;" + "-fx-background-color: " + backgroundColor + "; ");
                }
            }
        }
    }

    private void checkCell(Pane cell) {
        int[] coords = findCellCoordinates(cell);
    }

    // Returns the coordinates of a cell
    private int[] findCellCoordinates(Pane cell) {
        return (int[]) cell.getUserData();
    }

    // given a coordinate, highlight squares
    // then just call method from when a cell is clicked and when a number is inserted/deleted


}
