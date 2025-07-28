package sudoku.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

// This class holds the needed methods for creating UI components for multiple screens.
public class UIComponents {
    // Return GridPane representing sudoku board.
    public static GridPane createSudokuBoard() {
        // Create grid pane for the sudoku board, including size and style.
        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setHgap(0);
        board.setVgap(0);
        board.setStyle("-fx-background-color: white;");
        return board;
    }
    // Return GridPane representing number pad for sudoku board.
    public static GridPane createNumberPad() {
        // Create grid pane for number pad.
        GridPane numberPad = new GridPane();
        numberPad.setAlignment(Pos.CENTER);
        numberPad.setHgap(10);
        numberPad.setVgap(10);

        return numberPad;
    }
    // Returns String representing which cells on sudoku board need highlighted borders to emphasize grids.
    public static String getBorderWidth(int row, int column) {
        int top = (row % 3 == 0) ? 3 : 1;
        int right = (column == 8) ? 3 : 1;
        int bottom = (row == 8) ? 3 : 1;
        int left = (column % 3 == 0) ? 3 : 1;
        return top + " " + right + " " + bottom + " " + left;
    }
    // Returns whether two arrays are equal.
    public static boolean arraysAreEqual(int[][] a, int[][] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            if (a[i].length != b[i].length) return false;
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j]) return false;
            }
        }
        return true;
    }
}