package sudoku.logic;
import java.util.ArrayList;

// This class holds the high level methods for solving sudoku puzzles humanly and checking for errors within them.
public class SudokuSolverApplication {
    // Returns solved board using recursion.
    public static int[][] solveRecursively(int[][] sudokuBoard) {
        int[][] solvedBoard = SudokuSolverTechniques.solveRecursivelyHelper(sudokuBoard, 0, 0);
        return solvedBoard;
    }

    // Returns solved board, or as much of it as possible, using human techniques.
    public static int[][] solveBoardHumanly(int[][] sudokuBoard, int difficultyLevel) {
        int length = sudokuBoard.length;
        int[][] currentBoard = new int[9][9];

        // Make a copy of given board.
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                currentBoard[i][j] = sudokuBoard[i][j];
            }
        }

        // Initialize cell candidates of puzzle.
        ArrayList<Integer>[][] cellCandidates = new ArrayList[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cellCandidates[i][j] = new ArrayList<>();
            }
        }
        SudokuCandidatesManager.findCandidates(currentBoard, cellCandidates);

        // Continue solving board one cell at a time until it is complete or cannot be solved further, then return it.
        boolean changeMade;
        do {
            changeMade = false;
            switch (difficultyLevel) {
                case 1:
                    if (solveEasy(currentBoard, cellCandidates)) {
                        changeMade = true;
                    }
                    break;
                case 2:
                    if (solveMedium(currentBoard, cellCandidates)) {
                        changeMade = true;
                    }
                    break;
                case 3:
                    if (solveHard(currentBoard, cellCandidates)) {
                        changeMade = true;
                    }
                    break;
            }
        } while(changeMade);

        return currentBoard;
    }

    // Returns whether board is humanly solvable by testing if it is solved after attempting to solve it humanly.
    public static boolean boardHumanlySolvable(int[][] sudokuBoard, int difficultyLevel) {
        return boardComplete(solveBoardHumanly(sudokuBoard, difficultyLevel));
    }

    // Returns whether a cell is filled after trying easy methods to solve puzzle.
    public static boolean solveEasy(int[][] currentBoard, ArrayList<Integer>[][] cellCandidates) {
        boolean changeMade = false;
        // Try naked singles on puzzle.
        if (SudokuSolverTechniques.nakedSingles(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        // Try hidden singles on puzzle.
        else if (SudokuSolverTechniques.hiddenSingles(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        // Try pointing pairs on puzzle.
        else if (SudokuSolverTechniques.pointingPairs(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        return changeMade;
    }

    // Returns whether a cell is filled after trying medium methods to solve puzzle.
    public static boolean solveMedium(int[][] currentBoard, ArrayList<Integer>[][] cellCandidates) {
        boolean changeMade = false;
        // Try easy methods on puzzle first.
        if (solveEasy(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        // Try naked pairs on puzzle.
        else if (SudokuSolverTechniques.nakedPairs(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        // Try naked triples on puzzle.
        else if (SudokuSolverTechniques.nakedTriples(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        return changeMade;
    }

    // Returns whether a cell is filled after trying hard methods to solve puzzle.
    public static boolean solveHard(int[][] currentBoard, ArrayList<Integer>[][] cellCandidates) {
        boolean changeMade = false;
        // Try medium methods, and easy ones as a result, on puzzle.
        if (solveMedium(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        // Try naked quads on puzzle.
        else if (SudokuSolverTechniques.nakedQuads(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        return changeMade;
    }

    // Returns whether cell at given coordinates is valid in its row, column, and grid.
    public static boolean checkBoardError(int[][] board, int i, int j) {
        if (checkColumnError(board, i, j) || checkRowError(board, i, j) || checkGridError(board, i, j)) {
            return true;
        } else {
            return false;
        }
    }

    // Returns whether cell at given coordinates is valid in its grid.
    public static boolean checkGridError(int[][] board, int i, int j) {

        // Determines what grid coordinates are located in.
        int rowStart = SudokuUtils.identifyGridCoordinate(i);
        int columnStart = SudokuUtils.identifyGridCoordinate(j);

        // Loops through grid and checks for errors within grid compared to cell at given coordinates.
        for (int row = rowStart; row <= rowStart + 2; row++) {
            for (int column = columnStart; column <= columnStart + 2; column++) {
                if ((row != i || column != j) && board[row][column] == board[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    // Returns whether cell at given coordinates is valid in its column.
    public static boolean checkColumnError(int[][] board, int i, int j) {

        // Loops through column and checks for errors within it compared to cell at given coordinates.
        for (int k = 0; k < board.length; k++) {
            if (k != i && board[k][j] == board[i][j]) {
                return true;
            }
        }
        return false;
    }

    // Returns whether cell at given coordinates is valid in its row.
    public static boolean checkRowError(int[][] board, int i, int j) {

        // Loops through row and checks for errors within it compared to cell at given coordinates.
        for (int k = 0; k < board[0].length; k++) {
            if (k != j && board[i][k] == board[i][j]) {
                return true;
            }
        }
        return false;
    }

    // Returns whether the given board is full and contains no 0s/empty cells.
    public static boolean boardComplete(int[][] board) {
        for (int[] row: board) {
            for (int column: row) {
                if (column == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
