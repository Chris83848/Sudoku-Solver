package sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sudoku.logic.SudokuBoard;

import java.awt.*;

import static java.lang.String.*;

public class SolvingScreen {

    private Pane[][] cells = new Pane[9][9];
    public Scene getScene(Stage solve, int[][] puzzle) {

        GridPane board = UIComponents.createSudokuBoard();

        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                int value = puzzle[row][column];
                if (puzzle[row][column] != 0) {
                    Label givenCell = new Label(String.valueOf(value));
                    givenCell.setStyle("-fx-font-size: 30;");
                    StackPane wrapper = new StackPane(givenCell);
                    wrapper.setPrefSize(60, 60);
                    wrapper.setStyle(
                            "-fx-background-color: #d3d3d3;" +
                                    "-fx-border-color: black;" +
                                    "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";"
                    );
                    board.add(wrapper, column, row);

                } else {

                    Pane cell = new Pane();
                    cell.setPrefSize(60, 60);
                    cell.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" +
                            "-fx-border-width: " + UIComponents.getBorderWidth(row, column) + ";");
                    cells[row][column] = cell;

                    board.add(cell, column, row);
                }
            }
        }

        VBox layout = new VBox(20, board);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 800, 600);


    }
}
