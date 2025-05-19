package sudoku.logic;
import java.util.ArrayList;

public class SudokuCandidatesManager {

    // Finds the candidate lists of each empty cell in the sudoku Board and puts them in their own data structure
    public static void findCandidates(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        for (int i = 0; i < sudokuBoard.length; i++) {
            for (int j = 0; j < sudokuBoard.length; j++) {
                if (sudokuBoard[i][j] == 0) {
                    for (int candidate = 1; candidate <= 9; candidate++) {
                        sudokuBoard[i][j] = candidate;
                        if (!SudokuSolverApplication.checkBoardError(sudokuBoard, i, j)) {
                            candidates[i][j].add(candidate);
                        }
                    }
                    sudokuBoard[i][j] = 0;
                }
            }
        }
    }


    // Updates the candidates in each empty cell in the given grid, row, and column
    public static void updateCandidates(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int i, int j) {
        candidates[i][j].clear();
        updateRow(sudokuBoard, candidates, i);
        updateColumn(sudokuBoard, candidates, j);
        updateGrid(sudokuBoard, candidates, i, j);
    }


    // Updates the candidates in each empty cell in the given row
    public static void updateRow(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int i) {
        int length = sudokuBoard.length;

        for (int j = 0; j < length; j++) {
            if (sudokuBoard[i][j] == 0) {
                for (int candidate: candidates[i][j]) {
                    sudokuBoard[i][j] = candidate;
                    if (SudokuSolverApplication.checkBoardError(sudokuBoard, i, j)) {
                        candidates[i][j].remove(Integer.valueOf(candidate));
                        break;
                    }
                }
                sudokuBoard[i][j] = 0;
            }
        }
    }


    // Updates the candidates in each empty cell in the given column
    public static void updateColumn(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int j) {
        int length = sudokuBoard.length;

        for (int i = 0; i < length; i++) {
            if (sudokuBoard[i][j] == 0) {
                for (int candidate: candidates[i][j]) {
                    sudokuBoard[i][j] = candidate;
                    if (SudokuSolverApplication.checkBoardError(sudokuBoard, i, j)) {
                        candidates[i][j].remove(Integer.valueOf(candidate));
                        break;
                    }
                }
                sudokuBoard[i][j] = 0;
            }
        }
    }


    // Updates the candidates in each empty cell in the given grid
    public static void updateGrid(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int i, int j) {
        int rowStart, columnStart;

        rowStart = SudokuUtils.identifyGridCoordinate(i);
        columnStart = SudokuUtils.identifyGridCoordinate(j);

        for (int row = rowStart; row <= rowStart + 2; row++) {
            for (int column = columnStart; column <= columnStart + 2; column++) {
                if (sudokuBoard[row][column] == 0) {
                    for (int candidate: candidates[row][column]) {
                        sudokuBoard[row][column] = candidate;
                        if (SudokuSolverApplication.checkBoardError(sudokuBoard, row, column)) {
                            candidates[row][column].remove(Integer.valueOf(candidate));
                            break;
                        }
                    }
                    sudokuBoard[row][column] = 0;
                }
            }
        }
    }
}
