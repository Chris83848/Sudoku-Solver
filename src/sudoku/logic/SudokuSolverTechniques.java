package sudoku.logic;
import java.util.ArrayList;

// This class holds the low/medium level methods for solving sudoku puzzles humanly.
public class SudokuSolverTechniques {
    // Returns solved version of given board by using simple recursion, starting at given coordinates.
    public static int[][] solveRecursivelyHelper(int[][] sudokuBoard, int i, int j) {
        // If at end of row, move to next row.
        if (j == 9) {
            j = 0;
            i++;
        }
        // Attempt to place number if cell is empty, otherwise move on to next cell.
        if (sudokuBoard[i][j] == 0) {
            // Attempt to place number. If failure, backtrack.
            if (!SudokuSolverTechniquesHelpers.placeNumber(sudokuBoard, i, j)) {
                return sudokuBoard;
            } else {
                // If board is complete, end recursion and backtrack all the way back to return solved board.
                if (SudokuSolverApplication.boardComplete(sudokuBoard)) {
                    return sudokuBoard;
                } else {
                    // Move to next cell using new board instance.
                    int[][] currentBoard = solveRecursivelyHelper(sudokuBoard, i, j + 1);
                    // While board incomplete, keep recursing.
                    while (!SudokuSolverApplication.boardComplete(currentBoard)) {
                        // Attempt to place number. If failure, backtrack.
                        if (!SudokuSolverTechniquesHelpers.placeNumber(sudokuBoard, i, j)) {
                            return sudokuBoard;
                        } else {
                            // Otherwise move on until board is solved.
                            currentBoard = solveRecursivelyHelper(sudokuBoard, i, j + 1);
                        }
                    }
                    // End recursion and backtrack all the way back to return solved board.
                    return currentBoard;
                }
            }
        } else {
            return solveRecursivelyHelper(sudokuBoard, i, j + 1);
        }
    }

    // Returns whether naked singles method yields cell update. Checks each candidate list of empty cells in given puzzle,
    // and if any list has a length of one, then that candidate is placed in that empty cell.
    public static boolean nakedSingles(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        boolean changeMade = false;
        int length = sudokuBoard.length;

        // Loop through board.
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                // If cell is empty and has only one candidate, update cell.
                if (sudokuBoard[i][j] == 0 && candidates[i][j].size() == 1) {
                    sudokuBoard[i][j] = candidates[i][j].get(0);
                    SudokuCandidatesManager.updateCandidates(sudokuBoard, candidates, i, j);
                    changeMade = true;
                }
            }
        }
        return changeMade;
    }


    // Returns whether hidden singles method yields cell update. Checks each candidate list of empty cells in
    // given puzzle, and if that is the only list one candidate is included in its row, column, or grid,
    // then that candidate is placed in that cell.
    public static boolean hiddenSingles(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        boolean changeMade = false;
        int length = sudokuBoard.length;

        // Loop through board.
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                // Loop through each candidate of each empty cell.
                if (sudokuBoard[i][j] == 0) {
                    for (int index = 0; index < candidates[i][j].size(); index++) {
                        // Check if candidate is a hidden single
                        if (SudokuSolverTechniquesHelpers.hiddenSingleFound(sudokuBoard, candidates, i, j, candidates[i][j].get(index))) {
                            sudokuBoard[i][j] = candidates[i][j].get(index);
                            SudokuCandidatesManager.updateCandidates(sudokuBoard, candidates, i, j);
                            changeMade = true;
                        }
                    }
                }
            }
        }
        return changeMade;
    }


    // Returns whether pointing pairs method yields candidate update. Identifies candidates that can only go
    // in a certain row/column of a grid, and removes those candidates from the row/column outside the grid.
    public static boolean pointingPairs(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        boolean changeMade = false;
        int rowStart = 0, columnStart = 0;
        ArrayList<Integer> possibleRow, possibleColumn;
        // Loop through each grid.
        for (int grid = 1; grid <= 9; grid++) {
            for (int num = 1; num <= 9; num++) {
                possibleRow = SudokuSolverTechniquesHelpers.findPossibleRow(sudokuBoard, candidates, rowStart, columnStart, num);
                possibleColumn = SudokuSolverTechniquesHelpers.findPossibleColumn(sudokuBoard, candidates, rowStart, columnStart, num);

                if (!possibleRow.isEmpty() && possibleRow.size() <= 3 && SudokuUtils.allEqual(possibleRow)) {
                    if (SudokuSolverTechniquesHelpers.removeFromRow(sudokuBoard, candidates, possibleRow.get(0), num, possibleColumn.get(0))) {
                        changeMade = true;
                    }
                }
                if (!possibleColumn.isEmpty() && possibleColumn.size() <= 3 && SudokuUtils.allEqual(possibleColumn)) {
                    if (SudokuSolverTechniquesHelpers.removeFromColumn(sudokuBoard, candidates, possibleColumn.get(0), num, possibleRow.get(0))) {
                        changeMade = true;
                    }
                }
            }
            columnStart += 3;
            if (columnStart == 9) {
                columnStart = 0;
                rowStart += 3;
            }
        }
        return changeMade;
    }


    // Returns whether naked pairs method yields candidate update. Checks for naked pairs in each
    // row, column, and grid and removes them from the rest of that unit.
    public static boolean nakedPairs(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        boolean changeMade = false;
        if (SudokuSolverTechniquesHelpers.nakedPairsRows(sudokuBoard, candidates)) {
            changeMade = true;
        }
        if (SudokuSolverTechniquesHelpers.nakedPairsColumns(sudokuBoard, candidates)) {
            changeMade = true;
        }
        if (SudokuSolverTechniquesHelpers.nakedPairsGrids(sudokuBoard, candidates)) {
            changeMade = true;
        }
        return changeMade;
    }


    // Returns whether naked triples method yields candidate update. Checks for naked triples in each
    // row, column, and grid and removes them from the rest of that unit.
    public static boolean nakedTriples(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        boolean changeMade = false;
        if (SudokuSolverTechniquesHelpers.nakedTriplesRows(sudokuBoard, candidates)) {
            changeMade = true;
        }
        if (SudokuSolverTechniquesHelpers.nakedTriplesColumns(sudokuBoard, candidates)) {
            changeMade = true;
        }
        if (SudokuSolverTechniquesHelpers.nakedTriplesGrids(sudokuBoard, candidates)) {
            changeMade = true;
        }
        return changeMade;
    }


    // Returns whether naked quads method yields candidate update. Checks for naked quads in each
    // row, column, and grid and removes them from the rest of that unit.
    public static boolean nakedQuads(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        boolean changeMade = false;
        if (SudokuSolverTechniquesHelpers.nakedQuadsRows(sudokuBoard, candidates)) {
            changeMade = true;
        }
        if (SudokuSolverTechniquesHelpers.nakedQuadsColumns(sudokuBoard, candidates)) {
            changeMade = true;
        }
        if (SudokuSolverTechniquesHelpers.nakedQuadsGrids(sudokuBoard, candidates)) {
            changeMade = true;
        }
        return changeMade;
    }
}