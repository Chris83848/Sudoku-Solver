package sudoku.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sudoku.logic.SudokuCandidatesManager;
import sudoku.logic.SudokuSolverApplication;
import sudoku.logic.SudokuSolverTechniques;
import sudoku.logic.SudokuSolverTechniquesHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

public class SolvingScreen {

    // Create Panes for cells and selected cell value
    private Pane[][] cells = new Pane[9][9];
    private Pane selectedCell = null;
    private boolean isSolving = false;

    public Scene getScene(Stage solve, int[][] puzzle, String difficulty, boolean custom) {

        // Create title of screen
        Label title = new Label(difficulty);
        title.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-text-fill: #000000;");
        title.setAlignment(Pos.CENTER);

        // Create back button to go back to home screen
        Button backButton = new Button("Back");

        backButton.setStyle("-fx-font-size: 14; -fx-background-color: transparent; -fx-text-fill: #00264d; -fx-font-weight: bold; -fx-font-family: 'Courier New';");
        backButton.setOnMouseEntered(e -> backButton.setUnderline(true));
        backButton.setOnMouseExited(e -> backButton.setUnderline(false));

        backButton.setOnAction(e -> {
            if (custom) {
                solve.setScene(new LoadPuzzleScreen().getScene(solve));
            } else {
                solve.setScene(new DifficultySelectionScreen().getScene(solve));
            }
        });

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

                        if (isSolving) {
                            return;
                        }

                        // Reset cell styles
                        if (selectedCell != null) {
                            resetCells();
                        }

                        selectedCell = wrapper;
                        updateHighlights(wrapper, finalRow1, finalColumn1);
                    });
                    cells[row][column] = wrapper;
                    cells[row][column].setAccessibleText(String.valueOf(value));
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

                        if (isSolving) {
                            return;
                        }

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

            // Create button and stylize
            Button numButton = new Button(String.valueOf(num));
            numButton.setPrefSize(60, 60);
            numButton.setStyle(
                    "-fx-font-size: 24; " +
                            "-fx-background-color: white; " +
                            "-fx-text-fill: black; " +
                            "-fx-border-color: #7cfeff; " +
                            "-fx-border-radius: 3; " +
                            "-fx-background-radius: 3;"
            );

            numButton.setOnMouseEntered(eh -> numButton.setStyle(
                    "-fx-font-size: 24; " +
                            "-fx-background-color: #E0F7FF; " +
                            "-fx-text-fill: black; " +
                            "-fx-border-color: #7cfeff; " +
                            "-fx-border-radius: 3; " +
                            "-fx-background-radius: 3;"
            ));

            numButton.setOnMouseExited(eh -> numButton.setStyle(
                    "-fx-font-size: 24; " +
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
                if (selectedCell != null && findCellType(selectedCell).equals("free")) {

                    // Clear cell
                    selectedCell.getChildren().clear();
                    selectedCell.setAccessibleText(null);

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
                    selectedCell.setAccessibleText(numButton.getText());
                    resetCells();
                    updateHighlights(selectedCell, currentRow, currentColumn);


                    greyOut(numberPad);


                    if (checkCompletion(solvedPuzzle)) {
                        Completion(solve, difficulty, numberPad);
                    }
                }
            });

            numberPad.add(numButton, column, row);
        }

        // Create clear button, stylize, and add to last slot in number pad
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
            if (selectedCell != null && findCellType(selectedCell).equals("free")) {

                // Find coordinates, update highlights, and clear cell
                int[] coords = findCellCoordinates(selectedCell);
                resetCells();
                addVisibleHighlights(coords[0], coords[1]);
                selectedCell.getChildren().clear();
                selectedCell.setAccessibleText(null);
                selectedCell.setStyle("-fx-border-color: black;" +
                        "-fx-border-width: " + UIComponents.getBorderWidth(coords[0], coords[1]) + ";" +
                        "-fx-font-size: 20;" + "-fx-background-color: #00BFFF; ");
                greyOut(numberPad);
            }
        });
        numberPad.add(clearButton, 4, 1);


        // Create column for extra buttons to go on
        VBox buttonColumn = new VBox(10); // spacing of 10 between buttons
        buttonColumn.setAlignment(Pos.TOP_CENTER); // align buttons to top-center

        // Add all utility buttons
        utilityButtonCreation(buttonColumn, numberPad, backButton, solvedPuzzle, solve, difficulty);

        // Format middle layout
        HBox middleLayout = new HBox(30, board, buttonColumn);
        middleLayout.setAlignment(Pos.CENTER);
        middleLayout.setPadding(new Insets(0, 0, 0, 210));

        // Wrap everything in vertical layout
        VBox layout = new VBox(20, title, middleLayout, numberPad);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #3593ff;");

        // Create AnchorPane for the back button
        AnchorPane anchor = new AnchorPane(backButton);
        AnchorPane.setTopAnchor(backButton, 10.0);
        AnchorPane.setLeftAnchor(backButton, 10.0);
        anchor.setPickOnBounds(false);

        // Create final layout
        StackPane root = new StackPane(layout, anchor);
        root.setStyle("-fx-background-color: #3593ff;");

        return new Scene(root, 800, 600);
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
    private void revealCell(Pane cell, int[][] solvedBoard, GridPane numPad, Stage solve, String difficulty) {
        if (findCellType(cell).equals("free")) {
            cell.getChildren().clear();
            int[] coords = findCellCoordinates(cell);
            int numValue = solvedBoard[coords[0]][coords[1]];
            String printValue = String.valueOf(numValue);
            Label valueInput = new Label(printValue);
            valueInput.setStyle("-fx-font-size: 30;" + "-fx-text-fill: green;");
            valueInput.setAlignment(Pos.CENTER);
            valueInput.setPrefSize(60, 60);
            cell.getChildren().add(valueInput);
            cell.setAccessibleText(printValue);
            greyOut(numPad);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("coords", findCellCoordinates(cell));
            dataMap.put("type", "locked");
            cell.setUserData(dataMap);

            if (checkCompletion(solvedBoard)) {
                Completion(solve, difficulty, numPad);
            }
        }
    }

    // Reveal entire puzzle using revealCell method
    private void revealPuzzle(int[][] solvedBoard, GridPane numPad, Stage solve, String difficulty) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Pane cell = cells[row][column];
                revealCell(cell, solvedBoard, numPad, solve, difficulty);
            }
        }
    }

    // Set puzzle back to original version
    private void resetPuzzle(GridPane numPad) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Pane cell = cells[row][column];
                String dataType = findCellType(cell);
                if (dataType.equals("free")) {
                    cell.getChildren().clear();
                    cell.setAccessibleText(null);
                } else if (dataType.equals("locked")) {
                    cell.getChildren().clear();
                    cell.setAccessibleText(null);
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("coords", findCellCoordinates(cell));
                    dataMap.put("type", "free");
                    cell.setUserData(dataMap);
                }
            }
        }
        greyOut(numPad);
        resetCells();
        selectedCell = null;
    }

    private void hint() {

        // Create current puzzle with candidates
        int[][] currentPuzzle = findCurrentPuzzle();
        if (SudokuSolverApplication.boardComplete(currentPuzzle)) {
            return;
        }
        ArrayList<Integer>[][] boardCandidates = new ArrayList[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                boardCandidates[i][j] = new ArrayList<>();
            }
        }

        // Create copy of current puzzle for later comparison
        int[][] currentCopy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                currentCopy[i][j] = currentPuzzle[i][j];
            }
        }

        // Go through each solving method and apply to find which cell can be found next
        int[] hintCoordinate;
        SudokuCandidatesManager.findCandidates(currentPuzzle, boardCandidates);
        if (SudokuSolverTechniques.nakedSingles(currentPuzzle, boardCandidates)) {
            hintCoordinate = findCoordinateChange(currentCopy, currentPuzzle);
        } else if (SudokuSolverTechniques.hiddenSingles(currentPuzzle, boardCandidates)) {
            hintCoordinate = findCoordinateChange(currentCopy, currentPuzzle);
        } else if (SudokuSolverTechniques.pointingPairs(currentPuzzle, boardCandidates)) {
            hintCoordinate = findCoordinateChange(currentCopy, currentPuzzle);
        } else if (SudokuSolverTechniques.nakedPairs(currentPuzzle, boardCandidates)) {
            hintCoordinate = findCoordinateChange(currentCopy, currentPuzzle);
        } else if (SudokuSolverTechniques.nakedTriples(currentPuzzle, boardCandidates)) {
            hintCoordinate = findCoordinateChange(currentCopy, currentPuzzle);
        } else {
            SudokuSolverTechniques.nakedQuads(currentPuzzle, boardCandidates);
            hintCoordinate = findCoordinateChange(currentCopy, currentPuzzle);
        }
        Pane cell = cells[hintCoordinate[0]][hintCoordinate[1]];
        // Update highlights
        resetCells();
        selectedCell = cell;
        updateHighlights(selectedCell, hintCoordinate[0], hintCoordinate[1]);
    }

    private int[] findCoordinateChange(int[][] a, int[][] b) {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (a[row][column] != b[row][column]) {
                    return new int[]{row, column};
                }
            }
        }
        return null;
    }

    private boolean checkCompletion(int[][] solvedPuzzle) {
        int[][] currentPuzzle = findCurrentPuzzle();
        return UIComponents.arraysAreEqual(currentPuzzle, solvedPuzzle);
    }

    private int[][] findCurrentPuzzle() {
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
        return currentPuzzle;
    }

    private void utilityButtonCreation(VBox buttonColumn, GridPane pad, Button back, int[][] solvedPuzzle, Stage solve, String difficulty) {

        // Create stylizations for each button
        String style = "-fx-font-size: 16px;" +
                "-fx-font-family: 'Verdana';" +
                "-fx-background-color: #000000;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 24;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 3);" +
                "-fx-cursor: hand;";

        VBox checkButtons = new VBox();
        checkButtons.setPadding(new Insets(0, 0, 0, 0));

        // Create check cell button and format
        Button checkCell = new Button("Check Cell");
        checkCell.setPrefSize(160, 40);
        checkCell.setStyle(style);

        // Check cell correctness when clicked
        checkCell.setOnAction(e -> {
            checkCell(selectedCell, solvedPuzzle);
        });

        checkButtons.getChildren().add(checkCell);

        // Create check puzzle button and format
        Button checkPuzzle = new Button("Check Puzzle");
        checkPuzzle.setPrefSize(160, 40);
        checkPuzzle.setStyle(style);

        // Check puzzle correctness when clicked
        checkPuzzle.setOnAction(e -> {
            checkPuzzle(solvedPuzzle);
        });

        checkButtons.getChildren().add(checkPuzzle);

        checkButtons.setSpacing(10);

        buttonColumn.getChildren().add(checkButtons);

        VBox revealButtons = new VBox();
        revealButtons.setPadding(new Insets(20, 0, 0, 0));

        // Create reveal cell button and format
        Button revealCell = new Button("Reveal Cell");
        revealCell.setPrefSize(160, 40);
        revealCell.setStyle(style);

        // Reveal selected cell when clicked
        revealCell.setOnAction(e -> {
            revealCell(selectedCell, solvedPuzzle, pad, solve, difficulty);
        });

        revealButtons.getChildren().add(revealCell);

        // Create reveal puzzle button and format
        Button revealPuzzle = new Button("Reveal Puzzle");
        revealPuzzle.setPrefSize(160, 40);
        revealPuzzle.setStyle(style);

        // Reveal puzzle when clicked
        revealPuzzle.setOnAction(e -> {
            revealPuzzle(solvedPuzzle, pad, solve, difficulty);
        });

        revealButtons.getChildren().add(revealPuzzle);

        revealButtons.setSpacing(10);

        buttonColumn.getChildren().add(revealButtons);

        VBox utilityButtons = new VBox();
        utilityButtons.setPadding(new Insets(20, 0, 0, 0));

        // Create hint button and format
        Button hintButton = new Button("Hint");
        hintButton.setPrefSize(160, 40);
        hintButton.setStyle(style);

        // Show next easy cell to solve when clicked
        hintButton.setOnAction(e -> {
            hint();
        });

        utilityButtons.getChildren().add(hintButton);

        // Create reset puzzle button and format
        Button resetPuzzle = new Button("Reset Puzzle");
        resetPuzzle.setPrefSize(160, 40);
        resetPuzzle.setStyle(style);

        // Reset puzzle when clicked
        resetPuzzle.setOnAction(e -> {
            resetPuzzle(pad);
            selectedCell = null;
        });

        utilityButtons.getChildren().add(resetPuzzle);

        utilityButtons.setSpacing(10);

        buttonColumn.getChildren().add(utilityButtons);

        VBox solvingButtons = new VBox();
        solvingButtons.setPadding(new Insets(20, 0, 0, 0));

        // Create recursion button and format
        Button recursionButton = new Button("Solve Recursively");
        recursionButton.setPrefSize(190, 40);
        recursionButton.setStyle(style);

        solvingButtons.getChildren().add(recursionButton);

        // Create human button and format
        Button humanButton = new Button("Solve Humanly");
        humanButton.setPrefSize(190, 40);
        humanButton.setStyle(style);

        solvingButtons.getChildren().add(humanButton);

        solvingButtons.setSpacing(10);

        buttonColumn.getChildren().add(solvingButtons);

        // Solves by recursion when clicked
        recursionButton.setOnAction(e -> {

            isSolving = true;

            // Disable other buttons while solving
            checkCell.setDisable(true);
            checkPuzzle.setDisable(true);
            revealCell.setDisable(true);
            revealPuzzle.setDisable(true);
            resetPuzzle.setDisable(true);
            hintButton.setDisable(true);
            recursionButton.setDisable(true);
            humanButton.setDisable(true);
            back.setDisable(true);
            for (Node node : pad.getChildren()) {
                if (node instanceof Button) {
                    node.setDisable(true);  // or false to re-enable
                }
            }


            // Recursively solve puzzle in real time
            resetPuzzle(pad);
            int[][] currentPuzzle = findCurrentPuzzle();
            new Thread(() -> {
                recursive(currentPuzzle, 0, 0);

                isSolving = false;

                Platform.runLater(() -> {
                    // Reactivate buttons
                    checkCell.setDisable(false);
                    checkPuzzle.setDisable(false);
                    revealCell.setDisable(false);
                    revealPuzzle.setDisable(false);
                    resetPuzzle.setDisable(false);
                    hintButton.setDisable(false);
                    recursionButton.setDisable(false);
                    humanButton.setDisable(false);
                    back.setDisable(false);
                    for (Node node : pad.getChildren()) {
                        if (node instanceof Button) {
                            node.setDisable(false);  // or false to re-enable
                        }
                    }
                    Completion(solve, difficulty, pad);
                });

            }).start();

        });

        // Solves humanly when clicked
        humanButton.setOnAction(e -> {

            isSolving = true;

            // Disable other buttons while solving
            checkCell.setDisable(true);
            checkPuzzle.setDisable(true);
            revealCell.setDisable(true);
            revealPuzzle.setDisable(true);
            resetPuzzle.setDisable(true);
            hintButton.setDisable(true);
            recursionButton.setDisable(true);
            humanButton.setDisable(true);
            back.setDisable(true);
            for (Node node : pad.getChildren()) {
                if (node instanceof Button) {
                    node.setDisable(true);  // or false to re-enable
                }
            }


            // Humanly solve puzzle in real time
            resetPuzzle(pad);
            new Thread(() -> {
                human(solvedPuzzle, pad, solve, difficulty);

                isSolving = false;

                // Reactivate buttons
                checkCell.setDisable(false);
                checkPuzzle.setDisable(false);
                revealCell.setDisable(false);
                revealPuzzle.setDisable(false);
                resetPuzzle.setDisable(false);
                hintButton.setDisable(false);
                recursionButton.setDisable(false);
                humanButton.setDisable(false);
                back.setDisable(false);
                for (Node node : pad.getChildren()) {
                    if (node instanceof Button) {
                        node.setDisable(false);  // or false to re-enable
                    }
                }
                Completion(solve, difficulty, pad);

            }).start();

        });
        buttonColumn.setPadding(new Insets(20, 0, 0, 0));
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


    private int[][] recursive(int[][] currentPuzzle, int i, int j) {
        if (j == 9) {
            j = 0;
            i++;
        }

        if (i == 9) {
            return currentPuzzle; // Puzzle complete
        }

        if (currentPuzzle[i][j] == 0) {
            if (!placeUpdateNumber(currentPuzzle, i, j)) {
                return currentPuzzle;
            } else {

                if (SudokuSolverApplication.boardComplete(currentPuzzle)) {
                    return currentPuzzle;
                } else {
                    int[][] currentBoard = recursive(currentPuzzle, i, j + 1);
                    while (!SudokuSolverApplication.boardComplete(currentBoard)) {
                        if (!placeUpdateNumber(currentPuzzle, i, j)) {
                            return currentPuzzle;
                        } else {
                            currentBoard = recursive(currentPuzzle, i, j + 1);
                        }
                    }
                    return currentBoard;
                }
            }
        } else {
            return recursive(currentPuzzle, i, j + 1);
        }
    }

    private void human(int[][] solvedPuzzle, GridPane numPad, Stage solve, String difficulty) {
        while (!checkCompletion(solvedPuzzle)) {
            hint();
            sleep(1000);

            Platform.runLater(() -> {
                revealCell(selectedCell, solvedPuzzle, numPad, solve, difficulty);
            });
            sleep(1000);
        }
    }

    // This method places numbers for the recursive solver until it finds a possible one.
    private boolean placeUpdateNumber(int[][] board, int i, int j) {
        if (board[i][j] >= 9) {
            board[i][j] = 0;
            updateBoard(board);
            sleep(5);
            return false;
        }
        board[i][j]++;
        updateBoard(board);
        sleep(5);
        while (SudokuSolverApplication.checkBoardError(board, i, j) && board[i][j] <= 9) {
            if (board[i][j] != 9) {
                board[i][j]++;
                updateBoard(board);
                sleep(5);
            } else {
                board[i][j] = 0;
                updateBoard(board);
                sleep(5);
                return false;
            }
        }
        return true;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void updateBoard(int[][] board) {
        Platform.runLater(() -> {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Pane cell = cells[row][col];
                    String type = findCellType(cell);

                    // Only update free (editable) cells
                    if (type.equals("free")) {
                        cell.getChildren().clear();

                        int value = board[row][col];
                        if (value != 0) {
                            Label label = new Label(String.valueOf(value));
                            label.setStyle("-fx-font-size: 30;");
                            label.setAlignment(Pos.CENTER);
                            label.setPrefSize(60, 60);
                            cell.getChildren().add(label);
                        }
                    }
                }
            }
        });
    }

    // Grey out or ungrey out numbers that have been used on number pad
    public void greyOut(GridPane numPad) {
        Map<String, Integer> countMap = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String val = cells[i][j].getAccessibleText();
                if (val != null && !val.isBlank()) {
                    countMap.put(val, countMap.getOrDefault(val, 0) + 1);
                }
            }
        }

        for (Node node : numPad.getChildren()) {
            if (node instanceof Button btn) {
                String number = btn.getText();
                if (number.equals("X")) {
                    continue;
                }
                int count = countMap.getOrDefault(number, 0);
                if (count >= 9) {
                    // Grey out
                    btn.setStyle(
                            "-fx-font-size: 24; " +
                                    "-fx-background-color: white; " +
                                    "-fx-text-fill: grey; " +
                                    "-fx-border-color: #7cfeff; " +
                                    "-fx-border-radius: 3; " +
                                    "-fx-background-radius: 3;"
                    );
                    btn.setOnMouseEntered(eh -> btn.setStyle(
                            "-fx-font-size: 24; " +
                                    "-fx-background-color: #E0F7FF; " +
                                    "-fx-text-fill: grey; " +
                                    "-fx-border-color: #7cfeff; " +
                                    "-fx-border-radius: 3; " +
                                    "-fx-background-radius: 3;"
                    ));
                    btn.setOnMouseExited(eh -> btn.setStyle(
                            "-fx-font-size: 24; " +
                                    "-fx-background-color: white; " +
                                    "-fx-text-fill: grey; " +
                                    "-fx-border-color: #7cfeff; " +
                                    "-fx-border-radius: 3; " +
                                    "-fx-background-radius: 3;"
                    ));
                } else {
                    // Restore normal style
                    btn.setStyle(
                            "-fx-font-size: 24; " +
                                    "-fx-background-color: white; " +
                                    "-fx-text-fill: black; " +
                                    "-fx-border-color: #7cfeff; " +
                                    "-fx-border-radius: 3; " +
                                    "-fx-background-radius: 3;"
                    );
                    btn.setOnMouseEntered(eh -> btn.setStyle(
                            "-fx-font-size: 24; " +
                                    "-fx-background-color: #E0F7FF; " +
                                    "-fx-text-fill: black; " +
                                    "-fx-border-color: #7cfeff; " +
                                    "-fx-border-radius: 3; " +
                                    "-fx-background-radius: 3;"
                    ));
                    btn.setOnMouseExited(eh -> btn.setStyle(
                            "-fx-font-size: 24; " +
                                    "-fx-background-color: white; " +
                                    "-fx-text-fill: black; " +
                                    "-fx-border-color: #7cfeff; " +
                                    "-fx-border-radius: 3; " +
                                    "-fx-background-radius: 3;"
                    ));
                }
            }
        }
    }

    public void Completion(Stage solve, String difficulty, GridPane numPad) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(solve);
        popupStage.setTitle("Puzzle Complete!");

        Label message = new Label("Congratulations! You solved a " + difficulty + " puzzle!");
        message.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        Button homeButton = new Button("Return home");
        Button resetButton = new Button("Retry puzzle");
        Button difficultyButton = new Button("Choose new puzzle");
        Button customButton = new Button("Load custom puzzle");
        Button quitButton = new Button("Close Application");

        String buttonStyle =
                "-fx-font-size: 18px;" +
                        "-fx-font-family: 'Verdana';" +
                        "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 24;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 3);" +
                        "-fx-cursor: hand;";

        homeButton.setOnAction(e -> {
            popupStage.close();
            HomeScreen homeScreen = new HomeScreen();
            solve.setScene(homeScreen.getScene(solve));
        });
        homeButton.setStyle(buttonStyle);

        resetButton.setOnAction(e -> {
            popupStage.close();
            resetPuzzle(numPad);
        });
        resetButton.setStyle(buttonStyle);

        difficultyButton.setOnAction(e -> {
            popupStage.close();
            DifficultySelectionScreen difficultySelectionScreen = new DifficultySelectionScreen();
            solve.setScene(difficultySelectionScreen.getScene(solve));
        });
        difficultyButton.setStyle(buttonStyle);

        customButton.setOnAction(e -> {
            popupStage.close();
            LoadPuzzleScreen loadPuzzleScreen = new LoadPuzzleScreen();
            solve.setScene(loadPuzzleScreen.getScene(solve));
        });
        customButton.setStyle(buttonStyle);

        quitButton.setOnAction(e -> {
            Platform.exit();
        });
        quitButton.setStyle(buttonStyle);

        VBox layout = new VBox(15, message, homeButton, resetButton, difficultyButton, quitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #3593ff; -fx-border-color: #0096c9; -fx-border-width: 2px;");
        layout.setPrefSize(600, 500);

        popupStage.setScene(new Scene(layout));
        popupStage.setResizable(false);
        popupStage.show();
    }

}

