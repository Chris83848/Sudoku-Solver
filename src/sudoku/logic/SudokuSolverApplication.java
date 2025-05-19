package sudoku.logic;
import java.util.ArrayList;

public class SudokuSolverApplication {
    //Replace with SudokuBoard board if possible
    public static int[][] solveRecursively(int[][] sudokuBoard) {
        int[][] solvedBoard = SudokuSolverTechniques.solveRecursivelyHelper(sudokuBoard, 0, 0);
        return solvedBoard;
    }


    public static int[][] solveBoardHumanly(int[][] sudokuBoard, int difficultyLevel) {
        int length = sudokuBoard.length;;
        int[][] currentBoard = new int[9][9];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                currentBoard[i][j] = sudokuBoard[i][j];
            }
        }

        ArrayList<Integer>[][] cellCandidates = new ArrayList[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cellCandidates[i][j] = new ArrayList<>();
            }
        }
        SudokuCandidatesManager.findCandidates(currentBoard, cellCandidates);


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


    public static boolean boardHumanlySolvable(int[][] sudokuBoard, int difficultyLevel) {
        return boardComplete(solveBoardHumanly(sudokuBoard, difficultyLevel));
    }


    public static boolean solveEasy(int[][] currentBoard, ArrayList<Integer>[][] cellCandidates) {
        boolean changeMade = false;
        if (SudokuSolverTechniques.nakedSingles(currentBoard, cellCandidates)) {
            changeMade = true;
        }

        else if (SudokuSolverTechniques.hiddenSingles(currentBoard, cellCandidates)) {
            changeMade = true;
        }

        else if (SudokuSolverTechniques.pointingPairs(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        return changeMade;
    }

    public static boolean solveMedium(int[][] currentBoard, ArrayList<Integer>[][] cellCandidates) {
        boolean changeMade = false;
        if (solveEasy(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        else if (SudokuSolverTechniques.nakedPairs(currentBoard, cellCandidates)) {
            changeMade = true;
        }

        else if (SudokuSolverTechniques.nakedTriples(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        return changeMade;
    }

    public static boolean solveHard(int[][] currentBoard, ArrayList<Integer>[][] cellCandidates) {
        boolean changeMade = false;
        if (solveMedium(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        else if (SudokuSolverTechniques.nakedQuads(currentBoard, cellCandidates)) {
            changeMade = true;
        }
        return changeMade;
    }


    // Checks if given coordinates are valid in their row, column, and grid
    public static boolean checkBoardError(int[][] board, int i, int j) {
        if (checkColumnError(board, i, j) || checkRowError(board, i, j) || checkGridError(board, i, j)) {
            return true;
        } else {
            return false;
        }
    }


    // Checks if given coordinates are valid in that grid
    public static boolean checkGridError(int[][] board, int i, int j) {

        // Determines what grid coordinates are located in
        int rowStart = SudokuUtils.identifyGridCoordinate(i);
        int columnStart = SudokuUtils.identifyGridCoordinate(j);

        // Loops through grid and checks for errors within grid compared to given coordinates
        for (int row = rowStart; row <= rowStart + 2; row++) {
            for (int column = columnStart; column <= columnStart + 2; column++) {
                if ((row != i || column != j) && board[row][column] == board[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }


    // Checks if the given coordinates are valid in that column
    public static boolean checkColumnError(int[][] board, int i, int j) {

        //Loops through column and checks for errors within it compared to given coordinates
        for (int k = 0; k < board.length; k++) {
            if (k != i && board[k][j] == board[i][j]) {
                return true;
            }
        }
        return false;
    }


    // Checks if the given coordinates are valid in that row
    public static boolean checkRowError(int[][] board, int i, int j) {

        //Loops through row and checks for errors within it compared to given coordinates
        for (int k = 0; k < board[0].length; k++) {
            if (k != j && board[i][k] == board[i][j]) {
                return true;
            }
        }
        return false;
    }


    // Returns whether the given 2D array is full of occupants
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
