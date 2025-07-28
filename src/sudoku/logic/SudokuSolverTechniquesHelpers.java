package sudoku.logic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

// This class holds the low level methods for solving sudoku puzzles humanly.
public class SudokuSolverTechniquesHelpers {

    // Returns whether a number has been placed in the given cell on the board. Helper for recursive solver by
    // placing numbers incrementally until one is found or no numbers are left.
    public static boolean placeNumber(int[][] board, int i, int j) {
        // If each number has been checked and none fit, empty cell and return false.
        if (board[i][j] >= 9) {
            board[i][j] = 0;
            return false;
        }
        // Check next number.
        board[i][j]++;
        while (SudokuSolverApplication.checkBoardError(board, i, j) && board[i][j] <= 9) {
            if (board[i][j] != 9) {
                board[i][j]++;
            } else {
                // No numbers fit, so empty cell and return false.
                board[i][j] = 0;
                return false;
            }
        }
        // Number found, return true.
        return true;
    }


    // Returns whether a candidate appears only once in the given row, column, or grid.
    public static boolean hiddenSingleFound(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int i, int j, int num) {
        return (countByGrid(sudokuBoard, candidates, i, j, num) == 1 ||
                countByRow(sudokuBoard, candidates, i, num) == 1 ||
                countByColumn(sudokuBoard, candidates, j, num) == 1);
    }


    // Returns how many times a candidate appears in a given grid.
    public static int countByGrid(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int i, int j, int num) {
        int rowStart, columnStart, counter = 0;
        // Identify grid to check.
        rowStart = SudokuUtils.identifyGridCoordinate(i);
        columnStart = SudokuUtils.identifyGridCoordinate(j);
        // Loop through grid.
        for (int row = rowStart; row <= rowStart + 2; row++) {
            for (int column = columnStart; column <= columnStart + 2; column++) {
                // If cell is empty and contains candidate, update counter.
                if (sudokuBoard[row][column] == 0 && candidates[row][column].contains(num)) {
                    counter++;
                }
            }
        }
        return counter;
    }


    // Returns how many times a candidate appears in a given row.
    public static int countByRow(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int i, int num) {
        int counter = 0;
        // Loop through row.
        for (int j = 0; j < sudokuBoard.length; j++) {
            // If cell is empty and contains candidate, update counter.
            if (sudokuBoard[i][j] == 0 && candidates[i][j].contains(num)) {
                counter++;
            }
        }
        return counter;
    }


    // Returns how many times a candidate appears in a given column.
    public static int countByColumn(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int j, int num) {
        int counter = 0;
        // Loop through column.
        for (int i = 0; i < sudokuBoard.length; i++) {
            // If cell is empty and contains candidate, update counter.
            if (sudokuBoard[i][j] == 0 && candidates[i][j].contains(num)) {
                counter++;
            }
        }
        return counter;
    }


    // Finds locations of a certain candidate in a grid and returns those locations in terms of
    // what row they are in.
    public static ArrayList<Integer> findPossibleRow(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int rowStart, int columnStart, int num) {
        ArrayList<Integer> possibleRows = new ArrayList<>();
        // Loop through grid.
        for (int row = rowStart; row <= rowStart + 2; row++) {
            for (int column = columnStart; column <= columnStart + 2; column++) {
                // If cell is empty and contains candidate, add its row to array.
                if (sudokuBoard[row][column] == 0 && candidates[row][column].contains(num)) {
                    possibleRows.add(row);
                }
            }
        }
        return possibleRows;
    }


    // Returns locations of a certain candidate in a grid and returns those locations in terms of
    // what column they are in.
    public static ArrayList<Integer> findPossibleColumn(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int rowStart, int columnStart, int num) {
        ArrayList<Integer> possibleColumns = new ArrayList<>();
        // Loop through grid.
        for (int row = rowStart; row <= rowStart + 2; row++) {
            for (int column = columnStart; column <= columnStart + 2; column++) {
                // If cell is empty and contains candidate, add its column to array.
                if (sudokuBoard[row][column] == 0 && candidates[row][column].contains(num)) {
                    possibleColumns.add(column);
                }
            }
        }
        return possibleColumns;
    }


    // Attempts to remove given candidate from given row outside given grid, if it exists.
    // Returns whether a candidate is removed.
    public static boolean removeFromRow(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int row, int candidate, int column) {
        boolean changeMade = false;
        int length = sudokuBoard.length;
        int gridStart = SudokuUtils.identifyGridCoordinate(column);
        int gridEnd = gridStart + 3;
        // Loop through row.
        for (int j = 0; j < length; j++) {
            // Ignore values in given grid.
            if (j >= gridStart && j <= gridEnd) {
                continue;
            }
            // If cell is empty and candidate exists, remove it.
            if (sudokuBoard[row][j] == 0 && candidates[row][j].contains(candidate)) {
                candidates[row][j].remove(Integer.valueOf(candidate));
                changeMade = true;
            }
        }
        // Return if any candidates were removed.
        return changeMade;
    }


    // Attempts to remove given candidate from given column outside of given grid, if it exists.
    // Returns whether a candidate is removed.
    public static boolean removeFromColumn(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int column, int candidate, int row) {
        boolean changeMade = false;
        int length = sudokuBoard.length;
        int gridStart = SudokuUtils.identifyGridCoordinate(row);
        int gridEnd = gridStart + 3;
        // Loop through column.
        for (int i = 0; i < length; i++) {
            // Ignore values in given grid.
            if (i >= gridStart && i <= gridEnd) {
                continue;
            }
            // If cell is empty and candidate exists, remove it.
            if (sudokuBoard[i][column] == 0 && candidates[i][column].contains(candidate)) {
                candidates[i][column].remove(Integer.valueOf(candidate));
                changeMade = true;
            }
        }
        // Return if any candidates were removed.
        return changeMade;
    }


    // Checks for naked pairs in each row and removes those candidates from the rest of the row, if they exist.
    // Returns whether any candidate lists are updated.
    public static boolean nakedPairsRows(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;
        // Loop through rows.
        for (int i = 0; i < length; i++) {
            // Reset indices of potential matching requirements in row.
            indices = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                // If cell is empty and candidate list size equals two, add to list of potential naked pairs.
                if (sudokuBoard[i][j] == 0 && candidates[i][j].size() == 2) {
                    indices.add(new int[]{i, j});
                }
            }

            // Acquire combination if there is one.
            int[][] nakedPair = nakedDoublePair(indices);
            if (nakedPair == null) {
                continue;
            }

            // Remove combination from rest of row.
            int idx1 = nakedPair[0][1];
            int idx2 = nakedPair[1][1];
            for (int column = 0; column < length; column++) {
                if (column != idx1 && column != idx2 && sudokuBoard[i][column] == 0 &&
                        candidates[i][column].removeAll(candidates[i][idx1])) {
                    changeMade = true;
                }
            }
        }
        // Return whether any changes were made.
        return changeMade;
    }


    // Checks for naked pairs in each column and removes those candidates from the rest of the column, if they exist.
    // Returns whether any candidate lists are updated.
    public static boolean nakedPairsColumns(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through columns.
        for (int j = 0; j < length; j++) {
            // Reset indices of potential matching requirements in column.
            indices = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                // If cell is empty and candidate list size equals two, add to list of potential naked pairs.
                if (sudokuBoard[i][j] == 0 && candidates[i][j].size() == 2) {
                    indices.add(new int[]{i, j});
                }
            }

            // Acquire combination if there is one.
            int[][] nakedPair = nakedDoublePair(indices);
            if (nakedPair == null) {
                continue;
            }

            // Remove combination from rest of column.
            int idx1 = nakedPair[0][0];
            int idx2 = nakedPair[1][0];
            for (int row = 0; row < length; row++) {
                if (row != idx1 && row != idx2 && sudokuBoard[row][j] == 0 &&
                        candidates[row][j].removeAll(candidates[row][idx1])) {
                    changeMade = true;
                }
            }
        }
        // Return whether any changes were made.
        return changeMade;
    }


    // Checks for naked pairs in each grid and removes those candidates from the rest of the grid, if they exist.
    // Returns whether any candidate lists are updated.
    public static boolean nakedPairsGrids(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int rowStart = 0, columnStart = 0;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through grids.
        for (int grid = 1; grid <= 9; grid++) {
            // Reset indices of potential matching requirements in grid.
            indices = new ArrayList<>();
            for (int row = rowStart; row <= rowStart + 2; row++) {
                for (int column = columnStart; column <= columnStart + 2; column++) {
                    // If cell is empty and candidate list size equals two, add to list of potential naked pairs.
                    if (sudokuBoard[row][column] == 0 && candidates[row][column].size() == 2) {
                        indices.add(new int[]{row, column});
                    }
                }
            }

            // Acquire combination if there is one.
            int[][] nakedPair = nakedDoublePair(indices);
            if (nakedPair == null) {
                continue;
            }

            // Remove combination from rest of grid.
            int row1 = nakedPair[0][0];
            int column1 = nakedPair[0][1];
            int row2 = nakedPair[1][0];
            int column2 = nakedPair[1][1];
            for (int row = rowStart; row <= rowStart + 2; row++) {
                for (int column = columnStart; column <= columnStart + 2; column++) {
                    if ((row != row1 || column != column1) && (row != row2 || column != column2) &&
                            sudokuBoard[row][column] == 0 &&
                            candidates[row][column].removeAll(candidates[row1][column1])) {
                        changeMade = true;
                    }
                }
            }

            // Increment to next grid.
            columnStart += 3;
            if (columnStart == 9) {
                columnStart = 0;
                rowStart += 3;
            }
        }
        // Return whether any changes were made.
        return changeMade;
    }


    // Loop through index list to find if any two indices match.
    // Returns matching indices in 2D array or returns null if no matching indices found.
    public static int[][] nakedDoublePair(ArrayList<int[]> indices) {
        for (int i = 0; i < indices.size() - 1; i++) {
            for (int j = i + 1; j < indices.size(); j++) {
                if (indices.get(i).equals(indices.get(j))) {
                    return new int[][]{indices.get(i), indices.get(j)};
                }
            }
        }
        return null;
    }


    // Checks for naked triples in each row and removes those candidates from the rest of the row, if they exist.
    // Returns whether any candidate lists are updated.
    public static boolean nakedTriplesRows(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each row.
        for (int i = 0; i < length; i++) {
            // Reset indices of potential matching requirements in row.
            indices = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                // If cell is empty and candidate list size equals two or three, add to potential naked triples.
                if (sudokuBoard[i][j] == 0 && (candidates[i][j].size() == 2 || candidates[i][j].size() == 3)) {
                    indices.add(new int[]{i, j});
                }
            }
            // No naked triples possible if less than three indices.
            if (indices.size() < 3) {
                continue;
            }

            // Find a naked triple pair if one exists.
            int[][] nakedTriple = nakedTriplePair(indices, candidates);
            if (nakedTriple == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedTriple[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of row.
            int[] columns = new int[3];
            columns[0] = nakedTriple[1][nakedTriple[1].length - 1];
            columns[1] = nakedTriple[2][nakedTriple[2].length - 1];
            columns[2] = nakedTriple[3][nakedTriple[3].length - 1];
            for (int column = 0; column < length; column++) {
                if (sudokuBoard[i][column] == 0 && !SudokuUtils.arrayContainsNum(columns, column)) {
                    if (candidates[i][column].removeAll(union)) {
                        changeMade = true;
                    }
                }
            }
        }
        // Return whether any changes were made.
        return changeMade;
    }


    // Checks for naked triples in each column and removes those candidates from the rest of the column, if they exist.
    // Returns whether any candidate lists are updated.
    public static boolean nakedTriplesColumns(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each column.
        for (int j = 0; j < length; j++) {
            // Reset indices of potential matching requirements in column.
            indices = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                // If cell is empty and candidate list size equals two or three, add to potential naked triples.
                if (sudokuBoard[i][j] == 0 && (candidates[i][j].size() == 2 || candidates[i][j].size() == 3)) {
                    indices.add(new int[]{i, j});
                }
            }
            // No naked triples possible if less than three indices.
            if (indices.size() < 3) {
                continue;
            }

            // Find a naked triple pair if one exists.
            int[][] nakedTriple = nakedTriplePair(indices, candidates);
            if (nakedTriple == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedTriple[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of column.
            int[] rows = new int[3];
            rows[0] = nakedTriple[1][0];
            rows[1] = nakedTriple[2][0];
            rows[2] = nakedTriple[3][0];
            for (int row = 0; row < length; row++) {
                if (sudokuBoard[row][j] == 0 && !SudokuUtils.arrayContainsNum(rows, row)) {
                    if (candidates[row][j].removeAll(union)) {
                        changeMade = true;
                    }
                }
            }
        }
        // Return whether any changes were made.
        return changeMade;
    }


    // Checks for naked triples in each grid and removes those candidates from the rest of the grid, if they exist.
    // Returns whether any candidate lists are updated.
    public static boolean nakedTriplesGrids(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;
        int rowStart = 0, columnStart = 0;

        // Loop through each grid.
        for (int grid = 1; grid <= length; grid++) {
            // Reset indices of potential matching requirements in grid.
            indices = new ArrayList<>();
            for (int row = rowStart; row <= rowStart + 2; row++) {
                for (int column = columnStart; column <= columnStart + 2; column++) {
                    // If cell is empty and candidate list size equals two or three, add to potential naked triples.
                    if (sudokuBoard[row][column] == 0 && (candidates[row][column].size() == 2 || candidates[row][column].size() == 3)) {
                        indices.add(new int[]{row, column});
                    }
                }
            }
            // No naked triples possible if less than three indices.
            if (indices.size() < 3) {
                continue;
            }

            // Find a naked triple pair if one exists.
            int[][] nakedTriple = nakedTriplePair(indices, candidates);
            if (nakedTriple == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedTriple[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of grid.
            ArrayList<int[]> nakedTripleIndices = new ArrayList<>();
            nakedTripleIndices.add(nakedTriple[1]);
            nakedTripleIndices.add(nakedTriple[2]);
            nakedTripleIndices.add(nakedTriple[3]);
            for (int row = rowStart; row <= rowStart + 2; row++) {
                for (int column = columnStart; column <= columnStart + 2; column++) {
                    int[] index = new int[]{row, column};
                    if (sudokuBoard[row][column] == 0 && !SudokuUtils.arrayListContainsArray(nakedTripleIndices, index)) {
                        if (candidates[row][column].removeAll(union)) {
                            changeMade = true;
                        }
                    }
                }
            }

            // Increment to next grid.
            columnStart += 3;
            if (columnStart == 9) {
                columnStart = 0;
                rowStart += 3;
            }
        }
        // Return whether any changes were made.
        return changeMade;
    }


    // Loop through index list to find if any three indices form naked triple.
    // Returns matching union and indices in 2D array or returns null if no naked triple found.
    public static int[][] nakedTriplePair(ArrayList<int[]> indices, ArrayList<Integer>[][] candidates) {
        // Loop through index list to find three given indices at a time.
        for (int index1 = 0; index1 < indices.size() - 2; index1++) {
            int[] firstIndex = indices.get(index1);
            for (int index2 = index1 + 1; index2 < indices.size() - 1; index2++) {
                int[] secondIndex = indices.get(index2);
                for (int index3 = index2 + 1; index3 < indices.size(); index3++) {
                    int[] thirdIndex = indices.get(index3);

                    // Find union of indices.
                    int[] union = findUnion(
                            candidates[firstIndex[0]][firstIndex[1]],
                            candidates[secondIndex[0]][secondIndex[1]],
                            candidates[thirdIndex[0]][thirdIndex[1]]
                    );

                    // If length of union is 3, return newly found naked triple.
                    if (union.length == 3) {
                        return new int[][]{union, firstIndex, secondIndex, thirdIndex};
                    }
                }
            }
        }
        // No naked triples exist, return null.
        return null;
    }


    // Return the union of the given Array Lists.
    public static int[] findUnion(ArrayList<Integer> list1, ArrayList<Integer> list2, ArrayList<Integer> list3) {
        // Add all unique candidates to single Array List.
        ArrayList<Integer> candidates = new ArrayList<>();
        addUniqueCandidates(candidates, list1);
        addUniqueCandidates(candidates, list2);
        addUniqueCandidates(candidates, list3);
        // Convert candidates to int[] union and return it.
        int[] union = new int[candidates.size()];
        for (int i = 0; i < union.length; i++) {
            union[i] = candidates.get(i);
        }
        return union;
    }


    // Checks for naked quads in each row and removes those candidates from the rest of the row, if they exist.
    // Returns whether any candidate lists are updated.
    public static boolean nakedQuadsRows(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each row.
        for (int i = 0; i < length; i++) {
            // Reset indices of potential matching requirements in row.
            indices = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                // If cell is empty and candidate list size equals two, three, or four, add to potential naked quads.
                if (sudokuBoard[i][j] == 0 && (candidates[i][j].size() >= 2 && candidates[i][j].size() <= 4)) {
                    indices.add(new int[]{i, j});
                }
            }
            // No naked quads possible if less than four indices.
            if (indices.size() < 4) {
                continue;
            }

            // Find a naked quad pair if one exists.
            int[][] nakedQuad = nakedQuadPair(indices, candidates);
            if (nakedQuad == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedQuad[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of row.
            int[] columns = new int[4];
            columns[0] = nakedQuad[1][nakedQuad[1].length - 1];
            columns[1] = nakedQuad[2][nakedQuad[2].length - 1];
            columns[2] = nakedQuad[3][nakedQuad[3].length - 1];
            columns[3] = nakedQuad[4][nakedQuad[4].length - 1];
            for (int column = 0; column < length; column++) {
                if (sudokuBoard[i][column] == 0 && !SudokuUtils.arrayContainsNum(columns, column)) {
                    if (candidates[i][column].removeAll(union)) {
                        changeMade = true;
                    }
                }
            }
        }
        // Return whether any changes were made.
        return changeMade;
    }


    // Checks for naked quads in each column and removes those candidates from the rest of the column, if they exist.
    // Returns whether any candidate lists are updated.
    public static boolean nakedQuadsColumns(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each column.
        for (int j = 0; j < length; j++) {
            // Reset indices of potential matching requirements in column.
            indices = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                // If cell is empty and candidate list size equals two, three, or four, add to potential naked quads.
                if (sudokuBoard[i][j] == 0 && (candidates[i][j].size() >= 2 && candidates[i][j].size() <= 4)) {
                    indices.add(new int[]{i, j});
                }
            }
            // No naked quads possible if less than four indices.
            if (indices.size() < 4) {
                continue;
            }

            // Find a naked quad pair if one exists.
            int[][] nakedQuad = nakedQuadPair(indices, candidates);
            if (nakedQuad == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedQuad[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of column.
            int[] rows = new int[4];
            rows[0] = nakedQuad[1][0];
            rows[1] = nakedQuad[2][0];
            rows[2] = nakedQuad[3][0];
            rows[3] = nakedQuad[4][0];
            for (int row = 0; row < length; row++) {
                if (sudokuBoard[row][j] == 0 && !SudokuUtils.arrayContainsNum(rows, row)) {
                    if (candidates[row][j].removeAll(union)) {
                        changeMade = true;
                    }
                }
            }
        }
        // Return whether any changes were made.
        return changeMade;
    }


    // Checks for naked quads in each grid and removes those candidates from the rest of the grid, if they exist.
    // Returns whether any candidate lists are updated.
    public static boolean nakedQuadsGrids(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;
        int rowStart = 0, columnStart = 0;

        // Loop through each grid.
        for (int grid = 1; grid <= length; grid++) {
            // Reset indices of potential matching requirements in grid.
            indices = new ArrayList<>();
            for (int row = rowStart; row <= rowStart + 2; row++) {
                for (int column = columnStart; column <= columnStart + 2; column++) {
                    // If cell is empty and candidate list size equals two, three, or four, add to potential naked quads.
                    if (sudokuBoard[row][column] == 0 && (candidates[row][column].size() >= 2 && candidates[row][column].size() <= 4)) {
                        indices.add(new int[]{row, column});
                    }
                }
            }
            // No naked quads possible if less than four indices.
            if (indices.size() < 4) {
                continue;
            }

            // Find a naked quad pair if one exists.
            int[][] nakedQuad = nakedQuadPair(indices, candidates);
            if (nakedQuad == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedQuad[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of grid.
            ArrayList<int[]> nakedQuadIndices = new ArrayList<>();
            nakedQuadIndices.add(nakedQuad[1]);
            nakedQuadIndices.add(nakedQuad[2]);
            nakedQuadIndices.add(nakedQuad[3]);
            nakedQuadIndices.add(nakedQuad[4]);
            for (int row = rowStart; row <= rowStart + 2; row++) {
                for (int column = columnStart; column <= columnStart + 2; column++) {
                    int[] index = new int[]{row, column};
                    if (sudokuBoard[row][column] == 0 && !SudokuUtils.arrayListContainsArray(nakedQuadIndices, index)) {
                        if (candidates[row][column].removeAll(union)) {
                            changeMade = true;
                        }
                    }
                }
            }
            // Increment to next grid.
            columnStart += 3;
            if (columnStart == 9) {
                columnStart = 0;
                rowStart += 3;
            }
        }
        // Return whether any changes were made.
        return changeMade;
    }


    // Loop through index list to find if any four indices form naked quad.
    // Returns matching union and indices in 2D array or returns null if no naked triple found.
    public static int[][] nakedQuadPair(ArrayList<int[]> indices, ArrayList<Integer>[][] candidates) {

        // Loop through index list to find four given indices at a time.
        for (int index1 = 0; index1 < indices.size() - 3; index1++) {
            int[] firstIndex = indices.get(index1);
            for (int index2 = index1 + 1; index2 < indices.size() - 2; index2++) {
                int[] secondIndex = indices.get(index2);
                for (int index3 = index2 + 1; index3 < indices.size() - 1; index3++) {
                    int[] thirdIndex = indices.get(index3);
                    for (int index4 = index3 + 1; index4 < indices.size(); index4++) {
                        int[] fourthIndex = indices.get(index4);

                        // Find union of indices.
                        int[] union = findUnion(
                                candidates[firstIndex[0]][firstIndex[1]],
                                candidates[secondIndex[0]][secondIndex[1]],
                                candidates[thirdIndex[0]][thirdIndex[1]],
                                candidates[fourthIndex[0]][fourthIndex[1]]
                        );

                        // If length of union is four, return newly found naked quad.
                        if (union.length == 4) {
                            return new int[][]{union, firstIndex, secondIndex, thirdIndex, fourthIndex};
                        }
                    }
                }
            }
        }
        // No naked quads exist, return null.
        return null;
    }


    // Returns the union of the given Array Lists.
    public static int[] findUnion(ArrayList<Integer> list1, ArrayList<Integer> list2, ArrayList<Integer> list3, ArrayList<Integer> list4) {
        // Add all unique candidates to single Array List.
        ArrayList<Integer> candidates = new ArrayList<>();
        addUniqueCandidates(candidates, list1);
        addUniqueCandidates(candidates, list2);
        addUniqueCandidates(candidates, list3);
        addUniqueCandidates(candidates, list4);
        // Convert candidates to int[] union and return it.
        int[] union = new int[candidates.size()];
        for (int i = 0; i < union.length; i++) {
            union[i] = candidates.get(i);
        }
        return union;
    }


    // Adds unique candidates to union.
    public static void addUniqueCandidates(ArrayList<Integer> union, ArrayList<Integer> candidateList) {
        for (int candidate : candidateList) {
            if (!union.contains(candidate)) {
                union.add(candidate);
            }
        }
    }
}
