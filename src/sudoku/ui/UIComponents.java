package sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class UIComponents {

    public static GridPane createSudokuBoard() {
        // Create grid pane for the sudoku board, including size and style
        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setHgap(0);
        board.setVgap(0);
        board.setStyle("-fx-background-color: white;");
        return board;
    }

    public static GridPane createNumberPad(Pane sc) {
        // Create grid pane for number pad
        GridPane numberPad = new GridPane();
        numberPad.setAlignment(Pos.CENTER);
        numberPad.setHgap(10);
        numberPad.setVgap(10);

        return numberPad;
    }

    // Determines which cells need highlighted borders to emphasize subgrids
    public static String getBorderWidth(int row, int column) {
        int top = (row % 3 == 0) ? 3 : 1;
        int right = (column == 8) ? 3 : 1;
        int bottom = (row == 8) ? 3 : 1;
        int left = (column % 3 == 0) ? 3 : 1;
        return top + " " + right + " " + bottom + " " + left;
    }
}

