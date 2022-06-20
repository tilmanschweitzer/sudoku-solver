package de.tilmanschweitzer.sudoku.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static de.tilmanschweitzer.sudoku.model.SudokuPosition.allPositions;
import static de.tilmanschweitzer.sudoku.model.SudokuUtils.hasDuplicateValues;
import static de.tilmanschweitzer.sudoku.model.SudokuUtils.isValidValue;
import static java.util.stream.Collectors.toUnmodifiableList;

public class Sudoku {
    private final int[][] sudoku = new int[9][9]; // int[row][col]

    protected Sudoku(int[][] sudoku) {
        for (SudokuPosition position : allPositions) {
            setValueForPosition(position, sudoku[position.getRow()][position.getCol()]);
        }
    }

    public static Sudoku fromString(String sudokuAsString) throws SudokuFormatException {
        return new Sudoku(parseSudoku(sudokuAsString));
    }

    public static Sudoku fromSudoku(Sudoku sudoku) {
        return new Sudoku(sudoku.sudoku);
    }

    public static Sudoku empty() {
        return new Sudoku(new int[9][9]);
    }

    public static int[][] parseSudoku(String sudokuAsString) throws SudokuFormatException {
        final int[][] sudoku = new int[9][9];
        final List<Integer> valuesFromString = Arrays.stream(sudokuAsString.split("")).map(Integer::parseInt).collect(toUnmodifiableList());
        if (valuesFromString.size() != allPositions.size()) {
            throw new SudokuFormatException();
        }
        for (SudokuPosition position : allPositions) {
            sudoku[position.getRow()][position.getCol()] = valuesFromString.get(position.getIndex());
        }

        return sudoku;
    }

    public boolean isValid() {
        return allRowsAreValid() && allColsAreValid() && allBoxesAreValid();
    }

    public int[][] getSudoku() {
        return Arrays.stream(sudoku).map(int[]::clone).toArray(int[][]::new);
    }

    public List<Integer> getRow(int row) {
        return Arrays.stream(sudoku[row]).boxed().collect(toUnmodifiableList());
    }
    public List<Integer> getCol(int col) {
        return IntStream.range(0, 9).map(row -> sudoku[row][col]).boxed().collect(toUnmodifiableList());
    }

    public List<Integer> getBox(int box) {
        return IntStream.range(0, 9).map(index -> {
            final int row = (box / 3) * 3 + index / 3;
            final int col = (box % 3) * 3 + index % 3;
            return sudoku[row][col];
        }).boxed().collect(toUnmodifiableList());
    }

    private boolean allRowsAreValid() {
        return IntStream.range(0, 9).boxed().filter(row -> hasDuplicateValues(getRow(row))).findAny().isEmpty();
    }
    private boolean allColsAreValid() {
        return IntStream.range(0, 9).boxed().filter(col -> hasDuplicateValues(getCol(col))).findAny().isEmpty();
    }
    private boolean allBoxesAreValid() {
        return IntStream.range(0, 9).boxed().filter(box -> hasDuplicateValues(getBox(box))).findAny().isEmpty();
    }



    public boolean isCompleted() {
        return allPositions.size() == allPositions.stream().filter(this::isPositionValid).count();
    }

    public boolean isPositionValid(SudokuPosition position) {
        return isValidValue(getValueForPosition(position));
    }

    public int getValueForPosition(SudokuPosition position) {
        return sudoku[position.getRow()][position.getCol()];
    }

    public void setValueForPosition(SudokuPosition position, int value) {
        sudoku[position.getRow()][position.getCol()] = value;
    }

    public Optional<SudokuPosition> getFirstUnsetPosition() {
        return getFirstUnsetPosition(Optional.empty());
    }

    public Optional<SudokuPosition> getFirstUnsetPosition(Optional<SudokuPosition> latestPosition) {
        final List<SudokuPosition> positions = latestPosition.isEmpty() ? allPositions : allPositions.stream()
                .skip(latestPosition.get().getIndex())
                .collect(toUnmodifiableList());
        for (SudokuPosition position: positions) {
            if (getValueForPosition(position) == 0) {
                return Optional.of(position);
            }
        }
        return Optional.empty();
    }

    public String toPrintableString(String horizontalFieldDelimiter, String spacer) {
        final StringBuffer sb = new StringBuffer();

        for (SudokuPosition position : allPositions) {
            int value = getValueForPosition(position);
            final String valueAsString = value == 0 ? " " : value + "";
            if (position.getCol() % 3 == 0 && !horizontalFieldDelimiter.isBlank()) {
                sb.append(horizontalFieldDelimiter).append(spacer);
            }
            sb.append(valueAsString).append(spacer);
            if (position.getCol() == 8) {
                sb.append(horizontalFieldDelimiter).append("\n");
                if (position.getRow() % 3 == 2) {
                    sb.append("| - - - | - - - | - - - |\n");
                }
            }
        }

        return sb.toString();
    }

    public String toString() {
        return toPrintableString("|", " ");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Sudoku)) {
            return false;
        }
        final Sudoku other = (Sudoku) obj;
        return allPositions.stream()
                .filter((position) -> getValueForPosition(position) != other.getValueForPosition(position))
                .findFirst()
                .isEmpty();
    }
}
