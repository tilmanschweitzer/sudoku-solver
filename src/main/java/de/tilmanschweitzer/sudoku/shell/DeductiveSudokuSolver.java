package de.tilmanschweitzer.sudoku.shell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static de.tilmanschweitzer.sudoku.shell.Sudoku.isValidValue;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class DeductiveSudokuSolver implements SudokuSolver {

    private final Optional<SudokuSolver> fallbackSolver;

    public DeductiveSudokuSolver() {
        this(null);
    }
    public DeductiveSudokuSolver(SudokuSolver fallbackSolver) {
        this.fallbackSolver = Optional.ofNullable(fallbackSolver);
    }

    @Override
    public Sudoku solve(Sudoku originalSudoku) {
        final LogicSudokuSolverInternalModel sudoku = new LogicSudokuSolverInternalModel();

        for (SudokuPosition position : SudokuPosition.allPositions) {
            sudoku.setValue(position, originalSudoku.getValueForPosition(position));
        }

        boolean changedSomethingInTheLastIteration;
        do {
            changedSomethingInTheLastIteration = false;
            for (SudokuPosition position : SudokuPosition.allPositions) {
                boolean hasChanged = sudoku.check(position);
                if (hasChanged) {
                    changedSomethingInTheLastIteration = true;
                }
            }

        } while (!sudoku.internalSudoku.isCompleted() && changedSomethingInTheLastIteration);

        if (!sudoku.internalSudoku.isCompleted()) {
            if (fallbackSolver.isPresent()) {
                return fallbackSolver.get().solve(sudoku.internalSudoku);
            }
            throw new RuntimeException("Solver found no solution");
        }

        return sudoku.internalSudoku;
    }


    private static class LogicSudokuSolverInternalModel {

        private final Sudoku internalSudoku = Sudoku.empty();

        /*
         * Ruled out values from index 1-9
         * Index 0 = true indicates that the final value was found
         */
        final List<Integer>[][] possibleValues = new ArrayList[9][9];

        private LogicSudokuSolverInternalModel() {
            for (SudokuPosition positions : SudokuPosition.allPositions) {
                possibleValues[positions.getRow()][positions.getCol()] = range(1, 10).boxed().collect(toList());
            }
        }

        private boolean check(SudokuPosition position) {
            if (internalSudoku.isPositionValid(position)) {
                return false;
            }

            final List<Integer> possibleValuesForPosition = possibleValues[position.getRow()][position.getCol()];

            if (possibleValuesForPosition.size() > 1) {
                return false;
            }


            int lastPossibleValue = possibleValuesForPosition.get(0);
            setValue(position, lastPossibleValue);
            return true;
        }

        private boolean setValue(SudokuPosition position, int value) {
            if (internalSudoku.isPositionValid(position)) {
                return false;
            }

            if (isValidValue(value)) {
                internalSudoku.setValueForPosition(position, value);

                // rule out values
                IntStream.range(0, 9).forEach(index -> {
                    possibleValues[index][position.getCol()].remove((Integer) value); // remove value from column
                    possibleValues[position.getRow()][index].remove((Integer) value); // remove value from row

                    int fieldStartCol = (position.getCol() / 3) * 3;
                    int fieldStartRow = (position.getRow() / 3) * 3;

                    final int fieldCol = fieldStartCol + index / 3;
                    final int fieldRow = fieldStartRow + index % 3;
                    possibleValues[fieldRow][fieldCol].remove((Integer) value); // remove value from field

                    possibleValues[position.getRow()][position.getCol()].clear();
                    possibleValues[position.getRow()][position.getCol()].add(value);
                });
            }

            return false;
        }
    }
}
