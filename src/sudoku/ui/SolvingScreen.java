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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SolvingScreen {

    // Create Panes for cells and selected cell value
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
                    Label fixedCell = new Label(String.valueOf(value));
                    fixedCell.setStyle("-fx-font-size: 30;");

                    // Put in wrapper to maintain board borders
                    StackPane wrapper = new StackPane(fixedCell);
                    wrapper.setPrefSize(60, 60);
                    wrapper.setStyle(
                            "-fx-background-color: #d3d3d3;" +
                                    "-fx-border-color: black;" +
                                    "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";"
                    );

                    // Tag cell as fixed and add coordinates
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("coords", new int[]{row, column});
                    dataMap.put("type", "fixed");
                    wrapper.setUserData(dataMap);


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


                    // Map coordinates and cell type to each cell
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("coords", new int[]{row, column});
                    dataMap.put("type", "free");
                    cell.setUserData(dataMap);


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
                    cells[row][column] = cell;
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
                if (selectedCell != null && findCellType(selectedCell).equals("free")) {

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


                    if (checkCompletion(solvedPuzzle)) {
                        System.out.println("Checking if puzzle is solved.");
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("yay");
                        success.setHeaderText(null);
                        success.setContentText("You win");
                        success.showAndWait();
                        // Make new screen later
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
            if (selectedCell != null && findCellType(selectedCell).equals("free")) {

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

        // Add all utility buttons
        utilityButtonCreation(buttonColumn, solvedPuzzle);

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

    // Return the value inside a cell
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

    // Set cell back to original highlights--white or grey depending on fixed or not
    private void resetCellStyle(int row, int column) {
        Pane cell = cells[row][column];

        String backgroundColor = "white";
        if (findCellType(cell).equals("fixed")) {
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

    // Turn text of cell green if correct, red if not
    private void checkCell(Pane cell, int[][] solvedPuzzle) {
        if (cell.getChildren().isEmpty()) {
            return;
        }

        int[] cellCoords = findCellCoordinates(cell);
        int cellValue = getCellValue(cell);
        if (findCellType(cell).equals("free")) {
            Label value = (Label) cell.getChildren().get(0);
            if (cellValue == solvedPuzzle[cellCoords[0]][cellCoords[1]]) {
                value.setStyle("-fx-text-fill: green;" + "-fx-font-size: 30;");

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("coords", findCellCoordinates(cell));
                dataMap.put("type", "locked");
                cell.setUserData(dataMap);
            } else {
                value.setStyle("-fx-font-size: 30;" + "-fx-text-fill: red;");
            }
        }
    }

    // Check entire puzzle using checkCell method
    private void checkPuzzle(int[][] solvedPuzzle) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Pane cell = cells[row][column];
                checkCell(cell, solvedPuzzle);
            }
        }
    }

    // Reveal correct value inside of empty cell
    private void revealCell(Pane cell, int[][] solvedBoard) {
        if (findCellType(cell).equals("free")) {
            int[] coords = findCellCoordinates(cell);
            int numValue = solvedBoard[coords[0]][coords[1]];
            String printValue = String.valueOf(numValue);
            Label valueInput = new Label(printValue);
            valueInput.setStyle("-fx-font-size: 30;" + "-fx-text-fill: green;");
            valueInput.setAlignment(Pos.CENTER);
            valueInput.setPrefSize(60, 60);
            cell.getChildren().add(valueInput);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("coords", findCellCoordinates(cell));
            dataMap.put("type", "locked");
            cell.setUserData(dataMap);

            if (checkCompletion(solvedBoard)) {
                System.out.println("Checking if puzzle is solved.");
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("yay");
                success.setHeaderText(null);
                success.setContentText("You win");
                success.showAndWait();
                // Make new screen later
            }
        }
    }

    // Reveal entire puzzle using revealCell method
    private void revealPuzzle(int[][] solvedBoard) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Pane cell = cells[row][column];
                revealCell(cell, solvedBoard);
            }
        }
    }

    // Set puzzle back to original version
    private void resetPuzzle() {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Pane cell = cells[row][column];
                String dataType = findCellType(cell);
                if (dataType.equals("free")) {
                    cell.getChildren().clear();
                } else if (dataType.equals("locked")) {
                    cell.getChildren().clear();
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("coords", findCellCoordinates(cell));
                    dataMap.put("type", "free");
                    cell.setUserData(dataMap);
                }
            }
        }
        resetCells();
    }

    private void hint() {

    }

    private boolean checkCompletion(int[][] solvedPuzzle) {
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
        return UIComponents.arraysAreEqual(currentPuzzle, solvedPuzzle);
    }

    private void utilityButtonCreation(VBox buttonColumn, int[][] solvedPuzzle) {

        // Create check cell button and format
        Button checkCell = new Button("Check Cell");
        checkCell.setPrefSize(80, 40);
        checkCell.setStyle("-fx-font-size: 12;");
        buttonColumn.getChildren().add(checkCell);

        // Check cell correctness when clicked
        checkCell.setOnAction(e -> {
            checkCell(selectedCell, solvedPuzzle);
        });

        // Create check puzzle button and format
        Button checkPuzzle = new Button("Check Puzzle");
        checkPuzzle.setPrefSize(80, 40);
        checkPuzzle.setStyle("-fx-font-size: 12;");
        buttonColumn.getChildren().add(checkPuzzle);

        // Check puzzle correctness when clicked
        checkPuzzle.setOnAction(e -> {
            checkPuzzle(solvedPuzzle);
        });

        // Create reveal cell button and format
        Button revealCell = new Button("Reveal Cell");
        revealCell.setPrefSize(80, 40);
        revealCell.setStyle("-fx-font-size: 12;");
        buttonColumn.getChildren().add(revealCell);

        // Reveal selected cell when clicked
        revealCell.setOnAction(e -> {
            revealCell(selectedCell, solvedPuzzle);
        });

        // Create reveal puzzle button and format
        Button revealPuzzle = new Button("Reveal Puzzle");
        revealPuzzle.setPrefSize(80, 40);
        revealPuzzle.setStyle("-fx-font-size: 12;");
        buttonColumn.getChildren().add(revealPuzzle);

        // Reveal puzzle when clicked
        revealPuzzle.setOnAction(e -> {
            revealPuzzle(solvedPuzzle);
        });

        // Create reset puzzle button and format
        Button resetPuzzle = new Button("Reset Puzzle");
        resetPuzzle.setPrefSize(80, 40);
        resetPuzzle.setStyle("-fx-font-size: 12;");
        buttonColumn.getChildren().add(resetPuzzle);

        // Reset puzzle when clicked
        resetPuzzle.setOnAction(e -> {
            resetPuzzle();
            selectedCell = null;
        });
    }

    // Returns the coordinates of a cell
    private int[] findCellCoordinates(Pane cell) {
        Map<String, Object> dataMap = (Map<String, Object>) cell.getUserData();
        int[] coords = (int[]) dataMap.get("coords");

        return coords;
    }

    private String findCellType(Pane cell) {
        Map<String, Object> dataMap = (Map<String, Object>) cell.getUserData();
        String type = (String) dataMap.get("type");

        return type;
    }

    // given a coordinate, highlight squares
    // then just call method from when a cell is clicked and when a number is inserted/deleted


}