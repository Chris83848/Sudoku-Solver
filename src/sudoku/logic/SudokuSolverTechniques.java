package sudoku.logic;
import java.util.ArrayList;

public class SudokuSolverTechniques {

    public static int[][] solveRecursivelyHelper(int[][] sudokuBoard, int i, int j) {
        if (j == 9) {
            j = 0;
            i++;
        }

        if (sudokuBoard[i][j] == 0) {
            if (!SudokuSolverTechniquesHelpers.placeNumber(sudokuBoard, i, j)) {
                return sudokuBoard;
            } else {
                if (SudokuSolverApplication.boardComplete(sudokuBoard)) {
                    return sudokuBoard;
                } else {
                    int[][] currentBoard = solveRecursivelyHelper(sudokuBoard, i, j + 1);
                    while (!SudokuSolverApplication.boardComplete(currentBoard)) {
                        if (!SudokuSolverTechniquesHelpers.placeNumber(sudokuBoard, i, j)) {
                            return sudokuBoard;
                        } else {
                            currentBoard = solveRecursivelyHelper(sudokuBoard, i, j + 1);
                        }
                    }
                    return currentBoard;
                }
            }
        } else {
            return solveRecursivelyHelper(sudokuBoard, i, j + 1);
        }
    }


    // Checks each candidate list, and if any list has a length of one, then that candidate is placed in
    // that empty cell
    public static boolean nakedSingles(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        boolean changeMade = false;
        int length = sudokuBoard.length;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (sudokuBoard[i][j] == 0 && candidates[i][j].size() == 1) {
                    sudokuBoard[i][j] = candidates[i][j].get(0);
                    SudokuCandidatesManager.updateCandidates(sudokuBoard, candidates, i, j);
                    changeMade = true;
                }
            }
        }
        return changeMade;
    }


    // Checks each candidate list in each empty cell, and if that is the only cell one candidate is included
    // in its row, column, or grid, then that candidate is placed in that cell
    public static boolean hiddenSingles(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        boolean changeMade = false;
        int length = sudokuBoard.length;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (sudokuBoard[i][j] == 0) {
                    for (int index = 0; index < candidates[i][j].size(); index++) {
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


    // Identifies candidates that go only go in a certain row/column of a grid, and removes those candidates
    // from the row/column outside the grid
    public static boolean pointingPairs(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        boolean changeMade = false;
        int rowStart = 0, columnStart = 0;
        ArrayList<Integer> possibleRow, possibleColumn;

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


    // Checks for naked pairs in each row, column, and grid and removes them from the rest of that unit
    // Returns boolean on if any changes were made to the candidate lists in the board
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


    // Checks for naked triples in each row, column, and grid and removes them from the rest of that unit
    // Returns boolean on if any changes were made to the candidate lists in the board
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


    // Checks for naked quads in each row, column, and grid and removes them from the rest of that unit
    // Returns boolean on if any changes were made to the candidate lists in the board
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
