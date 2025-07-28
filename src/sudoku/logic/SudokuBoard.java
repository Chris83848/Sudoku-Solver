package sudoku.logic;

// This class holds a sudoku board and its related methods.
public class SudokuBoard {
    private static int[][] board;

    // Constructor
    public SudokuBoard(int[][] unsolvedBoard) {
        board = unsolvedBoard;
    }

    // Returns whether board is a valid and solvable puzzle
    public static boolean isValid() {
        return (isSolvable() && isCorrect());
    }

    // Returns whether board is a solvable puzzle
    public static boolean isSolvable() {
        return (SudokuSolverApplication.boardHumanlySolvable(board, 3) && SudokuBoardGenerator.isUnique(board));
    }

    // Returns whether board is a valid puzzle
    public static boolean isCorrect() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != 0) {
                    if (SudokuSolverApplication.checkBoardError(board, i, j)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Converts board to a string output
    @Override
    public String toString() {
        String result = "";
        result += "\n-------------------------";
        for (int i = 0; i < board.length; i++) {
            result += "| ";
            for (int j = 0; j < board.length; j++) {
                result += board[i][j] + " ";
                if (j == 2 || j == 5 || j == 8) {
                    result += "| ";
                }
            }
            if (i == 2 || i == 5) {
                result += "\n-------------------------";
            } else {
                result += "\n";
            }
        }
        result += "-------------------------";
        return result;
    }

    // Returns board
    public int[][] getBoard() {
        return board;
    }

}
