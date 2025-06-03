package sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoadPuzzleScreen {

    private Pane[][] cells = new Pane[9][9];
    private Pane selectedCell = null;

    public Scene getScene(Stage load) {

        Label title = new Label("Input Your Puzzle Below");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 14;");
        backButton.setOnAction(e -> {
            load.setScene(new HomeScreen().getScene(load));
        });

        // Create grid pane for the sudoku board, including size and style
        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setHgap(0);
        board.setVgap(0);
        board.setStyle("-fx-background-color: white;");

        // Create cells for the 81 sudoku squares on the board, setting size and style
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Pane cell = new Pane();
                cell.setPrefSize(60, 60);
                cell.setStyle("-fx-border-color: black;" +
                        "-fx-border-width: " + getBorderWidth(row, column) + ";" +
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
                                "-fx-border-width: " + getBorderWidth(previousRow, previousColumn) + ";" +
                                "-fx-font-size: 20;" + "-fx-background-color: white; ");
                    }

                    // Highlight current selected cell
                    selectedCell = cell;
                    cell.setStyle("-fx-border-color: black;" +
                            "-fx-border-width: " + getBorderWidth(finalRow, finalColumn) + ";" +
                            "-fx-font-size: 20;" + "-fx-background-color: lightblue; ");
                });

                board.add(cell, column, row);
            }
        }

        // Create grid pane for number pad
        GridPane numberPad = new GridPane();
        numberPad.setAlignment(Pos.CENTER);
        numberPad.setHgap(10);
        numberPad.setVgap(10);

        // Create buttons for numbers 1-9 and for number pad
        for (int num = 1; num <= 9; num++) {
            // Create button
            Button numButton = new Button(String.valueOf(num));
            numButton.setPrefSize(60, 60);
            numButton.setStyle("-fx-font-size: 18;");

            // Determine coordinates for number pad
            int row = (num <= 5) ? 0 : 1;
            int column = (num <= 5) ? (num - 1) : (num - 6);

            numButton.setOnAction(e -> {
                if (selectedCell != null) {
                    selectedCell.getChildren().clear();

                    Label numInput = new Label(numButton.getText());
                    numInput.setStyle("-fx-font-size: 20;");
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
        clearButton.setStyle("-fx-font-size: 20; -fx-text-fill: red;");

        clearButton.setOnAction(e -> {
            if (selectedCell != null) {
                selectedCell.getChildren().clear();
            }
        });

        numberPad.add(clearButton, 4, 1);

        // Set layout and return screen
        VBox coreLayout = new VBox(20, title, board, numberPad);
        coreLayout.setAlignment(Pos.CENTER);

        BorderPane overallLayout = new BorderPane();
        overallLayout.setTop(backButton);
        overallLayout.setCenter(coreLayout);
        BorderPane.setAlignment(backButton, Pos.TOP_LEFT);
        BorderPane.setMargin(backButton, new javafx.geometry.Insets(10));

        return new Scene(overallLayout, 800, 600);
    }

    // Determines which cells need highlighted borders to emphasize subgrids
    private String getBorderWidth(int row, int column) {
        int top = (row % 3 == 0) ? 3 : 1;
        int right = (column == 8) ? 3 : 1;
        int bottom = (row == 8) ? 3 : 1;
        int left = (column % 3 == 0) ? 3 : 1;
        return top + " " + right + " " + bottom + " " + left;
    }
}