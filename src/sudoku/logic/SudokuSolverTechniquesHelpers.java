package sudoku.logic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SudokuSolverTechniquesHelpers {

    // This method places numbers for the recursive solver until it finds a possible one.
    public static boolean placeNumber(int[][] board, int i, int j) {
        if (board[i][j] >= 9) {
            board[i][j] = 0;
            return false;
        }
        board[i][j]++;
        while (SudokuSolverApplication.checkBoardError(board, i, j) && board[i][j] <= 9) {
            if (board[i][j] != 9) {
                board[i][j]++;
            } else {
                board[i][j] = 0;
                return false;
            }
        }
        return true;
    }


    // Checks if a candidate appears only once in the given row, column, or grid
    public static boolean hiddenSingleFound(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int i, int j, int num) {
        if (countByGrid(sudokuBoard, candidates, i, j, num) == 1 || countByRow(sudokuBoard, candidates, i, num) == 1 || countByColumn(sudokuBoard, candidates, j, num) == 1) {
            return true;
        }
        return false;
    }


    // Counts how many times a candidate appears in a given grid
    public static int countByGrid(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int i, int j, int num) {
        int rowStart, columnStart, counter = 0;

        rowStart = SudokuUtils.identifyGridCoordinate(i);
        columnStart = SudokuUtils.identifyGridCoordinate(j);

        for (int row = rowStart; row <= rowStart + 2; row++) {
            for (int column = columnStart; column <= columnStart + 2; column++) {
                if (sudokuBoard[row][column] == 0 && candidates[row][column].contains(num)) {
                    counter++;
                }
            }
        }
        return counter;
    }


    // Counts how many times a candidate appears in a given row
    public static int countByRow(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int i, int num) {
        int counter = 0;
        for (int j = 0; j < sudokuBoard.length; j++) {
            if (sudokuBoard[i][j] == 0 && candidates[i][j].contains(num)) {
                counter++;
            }
        }
        return counter;
    }


    // Counts how many times a candidate appears in a given column
    public static int countByColumn(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int j, int num) {
        int counter = 0;
        for (int i = 0; i < sudokuBoard.length; i++) {
            if (sudokuBoard[i][j] == 0 && candidates[i][j].contains(num)) {
                counter++;
            }
        }
        return counter;
    }


    // Finds location of a certain candidate in a grid and returns those locations
    public static ArrayList<Integer> findPossibleRow(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int rowStart, int columnStart, int num) {
        ArrayList<Integer> possibleRows = new ArrayList<>();
        for (int row = rowStart; row <= rowStart + 2; row++) {
            for (int column = columnStart; column <= columnStart + 2; column++) {
                if (sudokuBoard[row][column] == 0 && candidates[row][column].contains(num)) {
                    possibleRows.add(row);
                }
            }
        }
        return possibleRows;
    }


    // Finds location of a certain candidate in a grid and returns those locations
    public static ArrayList<Integer> findPossibleColumn(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int rowStart, int columnStart, int num) {
        ArrayList<Integer> possibleColumns = new ArrayList<>();
        for (int row = rowStart; row <= rowStart + 2; row++) {
            for (int column = columnStart; column <= columnStart + 2; column++) {
                if (sudokuBoard[row][column] == 0 && candidates[row][column].contains(num)) {
                    possibleColumns.add(column);
                }
            }
        }
        return possibleColumns;
    }


    // Removes given candidate from given row outside given grid
    public static boolean removeFromRow(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int row, int candidate, int column) {
        boolean changeMade = false;
        int length = sudokuBoard.length;
        int gridStart = SudokuUtils.identifyGridCoordinate(column);
        int gridEnd = gridStart + 3;

        for (int j = 0; j < length; j++) {
            if (j >= gridStart && j <= gridEnd) {
                continue;
            }
            if (sudokuBoard[row][j] == 0 && candidates[row][j].contains(candidate)) {
                candidates[row][j].remove(Integer.valueOf(candidate));
                changeMade = true;
            }
        }
        return changeMade;
    }


    // Removes given candidate from given column outside of grid given
    public static boolean removeFromColumn(int[][] sudokuBoard, ArrayList<Integer>[][] candidates, int column, int candidate, int row) {
        boolean changeMade = false;
        int length = sudokuBoard.length;
        int gridStart = SudokuUtils.identifyGridCoordinate(row);
        int gridEnd = gridStart + 3;

        for (int i = 0; i < length; i++) {
            if (i >= gridStart && i <= gridEnd) {
                continue;
            }
            if (sudokuBoard[i][column] == 0 && candidates[i][column].contains(candidate)) {
                candidates[i][column].remove(Integer.valueOf(candidate));
                changeMade = true;
            }
        }
        return changeMade;
    }


    // Checks for naked pairs in each row and removes those candidates from the rest of the row
    // Returns boolean on if any changes were made to the candidate lists of any row
    public static boolean nakedPairsRows(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each separate unit
        for (int i = 0; i < length; i++) {

            // Find indices of matching requirements in unit
            indices = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                if (sudokuBoard[i][j] == 0 && candidates[i][j].size() == 2) {
                    indices.add(new int[]{i, j});
                }
            }

            // Acquire combination if there is one
            int[][] nakedPair = nakedDoublePair(indices);
            if (nakedPair == null) {
                continue;
            }

            // Remove combination from rest of unit
            int idx1 = nakedPair[0][1];
            int idx2 = nakedPair[1][1];

            for (int column = 0; column < length; column++) {
                if (column != idx1 && column != idx2 && sudokuBoard[i][column] == 0 && candidates[i][column].removeAll(candidates[i][idx1])) {
                    changeMade = true;
                }
            }
        }
        return changeMade;
    }


    // Checks for naked pairs in each column and removes those candidates from the rest of the column
    // Returns boolean on if any changes were made to the candidate lists of any column
    public static boolean nakedPairsColumns(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each separate unit
        for (int j = 0; j < length; j++) {

            // Find indices of matching requirements in unit
            indices = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                if (sudokuBoard[i][j] == 0 && candidates[i][j].size() == 2) {
                    indices.add(new int[]{i, j});
                }
            }

            // Acquire combination if there is one
            int[][] nakedPair = nakedDoublePair(indices);
            if (nakedPair == null) {
                continue;
            }

            // Remove combination from rest of unit
            int idx1 = nakedPair[0][0];
            int idx2 = nakedPair[1][0];

            for (int row = 0; row < length; row++) {
                if (row != idx1 && row != idx2 && sudokuBoard[row][j] == 0 &&
                        candidates[row][j].removeAll(candidates[row][idx1])) {
                    changeMade = true;
                }
            }
        }
        return changeMade;
    }


    // Checks for naked pairs in each grid and removes those candidates from the rest of the grid
    // Returns boolean on if any changes were made to the candidate lists of any grid
    public static boolean nakedPairsGrids(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int rowStart = 0, columnStart = 0;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each separate unit
        for (int grid = 1; grid <= 9; grid++) {

            // Find indices of matching requirements in unit
            indices = new ArrayList<>();
            for (int row = rowStart; row <= rowStart + 2; row++) {
                for (int column = columnStart; column <= columnStart + 2; column++) {
                    if (sudokuBoard[row][column] == 0 && candidates[row][column].size() == 2) {
                        indices.add(new int[]{row, column});
                    }
                }
            }

            // Acquire combination if there is one
            int[][] nakedPair = nakedDoublePair(indices);
            if (nakedPair == null) {
                continue;
            }

            // Remove combination from rest of unit
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

            // Increments to next grid
            columnStart += 3;
            if (columnStart == 9) {
                columnStart = 0;
                rowStart += 3;
            }
        }
        return changeMade;
    }


    // Loops through index list to find if any two indices match
    // Returns matching indices in 2D array or returns null if no matching indices found
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


    // Checks for naked triples in each row and removes those candidates from the rest of the row
    // Returns boolean on if any changes were made to the candidate lists of any row
    public static boolean nakedTriplesRows(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each row
        for (int i = 0; i < length; i++) {

            // Collect indices of each candidate list of size two or three
            indices = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                if (sudokuBoard[i][j] == 0 && (candidates[i][j].size() == 2 || candidates[i][j].size() == 3)) {
                    indices.add(new int[]{i, j});
                }
            }
            if (indices.size() < 3) {
                continue;
            }

            // Find a naked triple pair if one exists
            int[][] nakedTriple = nakedTriplePair(indices, candidates);
            if (nakedTriple == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedTriple[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of row
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
        return changeMade;
    }


    // Checks for naked triples in each column and removes those candidates from the rest of the column
    // Returns boolean on if any changes were made to the candidate lists of any column
    public static boolean nakedTriplesColumns(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each column
        for (int j = 0; j < length; j++) {

            // Collect indices of each candidate list of size two or three
            indices = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                if (sudokuBoard[i][j] == 0 && (candidates[i][j].size() == 2 || candidates[i][j].size() == 3)) {
                    indices.add(new int[]{i, j});
                }
            }
            if (indices.size() < 3) {
                continue;
            }

            // Find a naked triple pair if one exists
            int[][] nakedTriple = nakedTriplePair(indices, candidates);
            if (nakedTriple == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedTriple[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of column
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
        return changeMade;
    }

    // Checks for naked triples in each grid and removes those candidates from the rest of the grid
    // Returns boolean on if any changes were made to the candidate lists of any grid
    public static boolean nakedTriplesGrids(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;
        int rowStart = 0, columnStart = 0;

        // Loop through each grid
        for (int grid = 1; grid <= length; grid++) {

            // Collect indices of each candidate list of size two or three
            indices = new ArrayList<>();
            for (int row = rowStart; row <= rowStart + 2; row++) {
                for (int column = columnStart; column <= columnStart + 2; column++) {
                    if (sudokuBoard[row][column] == 0 && (candidates[row][column].size() == 2 || candidates[row][column].size() == 3)) {
                        indices.add(new int[]{row, column});
                    }
                }
            }
            if (indices.size() < 3) {
                continue;
            }

            // Find a naked triple pair if one exists
            int[][] nakedTriple = nakedTriplePair(indices, candidates);
            if (nakedTriple == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedTriple[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of grid
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

            // Increments to next grid
            columnStart += 3;
            if (columnStart == 9) {
                columnStart = 0;
                rowStart += 3;
            }
        }
        return changeMade;
    }


    // Loops through index list to find if any three indices form naked triple
    // Returns matching union and indices in 2D array or returns null if no naked triple found
    public static int[][] nakedTriplePair(ArrayList<int[]> indices, ArrayList<Integer>[][] candidates) {

        // Loops through index list to find three given indices at a time
        for (int index1 = 0; index1 < indices.size() - 2; index1++) {
            int[] firstIndex = indices.get(index1);
            for (int index2 = index1 + 1; index2 < indices.size() - 1; index2++) {
                int[] secondIndex = indices.get(index2);
                for (int index3 = index2 + 1; index3 < indices.size(); index3++) {
                    int[] thirdIndex = indices.get(index3);

                    // Finds union of indices
                    int[] union = findUnion(
                            candidates[firstIndex[0]][firstIndex[1]],
                            candidates[secondIndex[0]][secondIndex[1]],
                            candidates[thirdIndex[0]][thirdIndex[1]]
                    );

                    // If length of union is 3, naked triple found
                    if (union.length == 3) {
                        return new int[][]{union, firstIndex, secondIndex, thirdIndex};
                    }
                }
            }
        }
        return null;
    }


    // Returns the union of the given Array Lists
    public static int[] findUnion(ArrayList<Integer> list1, ArrayList<Integer> list2, ArrayList<Integer> list3) {
        ArrayList<Integer> candidates = new ArrayList<>();

        addUniqueCandidates(candidates, list1);
        addUniqueCandidates(candidates, list2);
        addUniqueCandidates(candidates, list3);

        int[] union = new int[candidates.size()];
        for (int i = 0; i < union.length; i++) {
            union[i] = candidates.get(i);
        }
        return union;
    }


    // Checks for naked quads in each row and removes those candidates from the rest of the row
    // Returns boolean on if any changes were made to the candidate lists of any row
    public static boolean nakedQuadsRows(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each row
        for (int i = 0; i < length; i++) {

            // Collect indices of each candidate list of size 2-4
            indices = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                if (sudokuBoard[i][j] == 0 && (candidates[i][j].size() >= 2 && candidates[i][j].size() <= 4)) {
                    indices.add(new int[]{i, j});
                }
            }
            if (indices.size() < 4) {
                continue;
            }

            // Find a naked quad pair if one exists
            int[][] nakedQuad = nakedQuadPair(indices, candidates);
            if (nakedQuad == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedQuad[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of row
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
        return changeMade;
    }


    // Checks for naked quads in each column and removes those candidates from the rest of the column
    // Returns boolean on if any changes were made to the candidate lists of any column
    public static boolean nakedQuadsColumns(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;

        // Loop through each column
        for (int j = 0; j < length; j++) {

            // Collect indices of each candidate list of size 2-4
            indices = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                if (sudokuBoard[i][j] == 0 && (candidates[i][j].size() >= 2 && candidates[i][j].size() <= 4)) {
                    indices.add(new int[]{i, j});
                }
            }
            if (indices.size() < 4) {
                continue;
            }

            // Find a naked quad pair if one exists
            int[][] nakedQuad = nakedQuadPair(indices, candidates);
            if (nakedQuad == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedQuad[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of column
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
        return changeMade;
    }


    // Checks for naked quads in each grid and removes those candidates from the rest of the grid
    // Returns boolean on if any changes were made to the candidate lists of any grid
    public static boolean nakedQuadsGrids(int[][] sudokuBoard, ArrayList<Integer>[][] candidates) {
        int length = sudokuBoard.length;
        boolean changeMade = false;
        ArrayList<int[]> indices;
        int rowStart = 0, columnStart = 0;

        // Loop through each grid
        for (int grid = 1; grid <= length; grid++) {

            // Collect indices of each candidate list of size 2-4
            indices = new ArrayList<>();
            for (int row = rowStart; row <= rowStart + 2; row++) {
                for (int column = columnStart; column <= columnStart + 2; column++) {
                    if (sudokuBoard[row][column] == 0 && (candidates[row][column].size() >= 2 && candidates[row][column].size() <= 4)) {
                        indices.add(new int[]{row, column});
                    }
                }
            }
            if (indices.size() < 4) {
                continue;
            }

            // Find a naked quad pair if one exists
            int[][] nakedQuad = nakedQuadPair(indices, candidates);
            if (nakedQuad == null) {
                continue;
            }
            ArrayList<Integer> union = Arrays.stream(nakedQuad[0])
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));


            // Remove numbers from union from rest of grid
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

            // Increments to next grid
            columnStart += 3;
            if (columnStart == 9) {
                columnStart = 0;
                rowStart += 3;
            }
        }
        return changeMade;
    }


    // Loops through index list to find if any four indices form naked quad
    // Returns matching union and indices in 2D array or returns null if no naked triple found
    public static int[][] nakedQuadPair(ArrayList<int[]> indices, ArrayList<Integer>[][] candidates) {

        // Loops through index list to find four given indices at a time
        for (int index1 = 0; index1 < indices.size() - 3; index1++) {
            int[] firstIndex = indices.get(index1);
            for (int index2 = index1 + 1; index2 < indices.size() - 2; index2++) {
                int[] secondIndex = indices.get(index2);
                for (int index3 = index2 + 1; index3 < indices.size() - 1; index3++) {
                    int[] thirdIndex = indices.get(index3);
                    for (int index4 = index3 + 1; index4 < indices.size(); index4++) {
                        int[] fourthIndex = indices.get(index4);

                        // Finds union of indices
                        int[] union = findUnion(
                                candidates[firstIndex[0]][firstIndex[1]],
                                candidates[secondIndex[0]][secondIndex[1]],
                                candidates[thirdIndex[0]][thirdIndex[1]],
                                candidates[fourthIndex[0]][fourthIndex[1]]
                        );

                        // If length of union is 4, naked quad found
                        if (union.length == 4) {
                            return new int[][]{union, firstIndex, secondIndex, thirdIndex, fourthIndex};
                        }
                    }
                }
            }
        }
        return null;
    }


    // Returns the union of the given Array Lists
    public static int[] findUnion(ArrayList<Integer> list1, ArrayList<Integer> list2, ArrayList<Integer> list3, ArrayList<Integer> list4) {
        ArrayList<Integer> candidates = new ArrayList<>();

        addUniqueCandidates(candidates, list1);
        addUniqueCandidates(candidates, list2);
        addUniqueCandidates(candidates, list3);
        addUniqueCandidates(candidates, list4);

        int[] union = new int[candidates.size()];
        for (int i = 0; i < union.length; i++) {
            union[i] = candidates.get(i);
        }
        return union;
    }


    // Adds unique candidates to union
    public static void addUniqueCandidates(ArrayList<Integer> union, ArrayList<Integer> candidateList) {
        for (int candidate : candidateList) {
            if (!union.contains(candidate)) {
                union.add(candidate);
            }
        }
    }
}
