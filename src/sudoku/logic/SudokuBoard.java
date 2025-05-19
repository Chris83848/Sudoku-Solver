package sudoku.logic;

public class SudokuBoard {
    private static int[][] board;
    private String difficulty;

    public SudokuBoard(int[][] unsolvedBoard) {
        board = unsolvedBoard;
    }


    public static boolean isValid() {
        if (isSolvable() && isCorrect()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSolvable() {
        return SudokuSolverApplication.boardHumanlySolvable(board, 3);
    }

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

    public int[][] getBoard() {
        return board;
    }

}
