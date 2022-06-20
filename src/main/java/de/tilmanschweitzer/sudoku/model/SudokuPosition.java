package de.tilmanschweitzer.sudoku.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toUnmodifiableList;

public class SudokuPosition {
    public static int SUDOKU_WIDTH = 9;
    public static int SUDOKU_SIZE = SUDOKU_WIDTH * SUDOKU_WIDTH;

    public static final List<SudokuPosition> allPositions = IntStream.range(0, SUDOKU_SIZE).boxed().map(SudokuPosition::new).collect(Collectors.toUnmodifiableList());

    private static final Map<Integer, List<SudokuPosition>> positionsByRow = new HashMap<>();
    private static final Map<Integer, List<SudokuPosition>> positionsByCol = new HashMap<>();
    private static final Map<Integer, List<SudokuPosition>> positionsByBox = new HashMap<>();

    private static final Map<SudokuPosition, Set<SudokuPosition>> positionsToBeRuledOut = new HashMap<>();

    static {
        IntStream.range(0, 9).forEach(fixedValue -> {
            positionsByRow.put(fixedValue, IntStream.range(0, SUDOKU_WIDTH).boxed().map(col -> SudokuPosition.of(fixedValue, col)).collect(Collectors.toUnmodifiableList()));
            positionsByCol.put(fixedValue, IntStream.range(0, SUDOKU_WIDTH).boxed().map(row -> SudokuPosition.of(row, fixedValue)).collect(Collectors.toUnmodifiableList()));
            positionsByBox.put(fixedValue, IntStream.range(0, 9).boxed().map(index -> {
                final int row = (fixedValue / 3) * 3 + index / 3;
                final int col = (fixedValue % 3) * 3 + index % 3;
                return SudokuPosition.of(row, col);
            }).collect(toUnmodifiableList()));
        });
        allPositions.forEach(position -> {
            final Set<SudokuPosition> positionsToBeRuledOutForPosition = new HashSet<>();
            positionsToBeRuledOutForPosition.addAll(position.getPositionsInSameRow());
            positionsToBeRuledOutForPosition.addAll(position.getPositionsInSameColumn());
            positionsToBeRuledOutForPosition.addAll(position.getPositionsInSameBox());
            positionsToBeRuledOutForPosition.remove(position);
            positionsToBeRuledOut.put(position, positionsToBeRuledOutForPosition);
        });
    }

    private final int index;

    private SudokuPosition(int index) {
        this.index = index;
    }

    public static SudokuPosition of(int row, int col) {
        return allPositions.get(row * SUDOKU_WIDTH + col);
    }

    public int getIndex() {
        return index;
    }

    public int getRow() {
        return index / SUDOKU_WIDTH;
    }

    public int getCol() {
        return index % SUDOKU_WIDTH;
    }

    public int getBox() {
        return (getRow() / 3) * 3 + getCol() / 3;
    }

    public List<SudokuPosition> getPositionsInSameRow() {
        return positionsByRow.get(getRow());
    }

    public List<SudokuPosition> getPositionsInSameColumn() {
        final int fixedCol = getCol();
        return IntStream.range(0, SUDOKU_WIDTH).boxed().map(indexRow -> SudokuPosition.of(indexRow, fixedCol)).collect(Collectors.toUnmodifiableList());
    }

    public List<SudokuPosition> getPositionsInSameBox() {
        return positionsByBox.get(getBox());
    }

    public Set<SudokuPosition> getPositionsToBeRuledOut() {
        return positionsToBeRuledOut.get(this);
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SudokuPosition)) {
            return false;
        }
        return index == ((SudokuPosition) obj).index;
    }

    @Override
    public String toString() {
        return "SudokuPosition{" +
                "row=" + getRow() +
                ", col=" + getCol() +
                '}';
    }
}
