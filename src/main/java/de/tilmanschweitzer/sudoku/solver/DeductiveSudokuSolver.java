package de.tilmanschweitzer.sudoku.solver;

import de.tilmanschweitzer.sudoku.model.Sudoku;
import de.tilmanschweitzer.sudoku.model.SudokuPosition;

import java.util.*;
import java.util.stream.Collectors;

import static de.tilmanschweitzer.sudoku.model.Sudoku.isValidValue;
import static de.tilmanschweitzer.sudoku.model.SudokuPosition.allPositions;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class DeductiveSudokuSolver implements SudokuSolver {

    private final SudokuSolver fallbackSolver;
    private final boolean failWhenUnsolved;

    private DeductiveSudokuSolver() {
        this(null, false);
    }

    private DeductiveSudokuSolver(SudokuSolver fallbackSolver, boolean failWhenUnsolved) {
        this.fallbackSolver = fallbackSolver;
        this.failWhenUnsolved = failWhenUnsolved;
    }

    public static DeductiveSudokuSolver createWithFallbackSolver(SudokuSolver fallbackSolver) {
        return new DeductiveSudokuSolver(fallbackSolver, true);
    }

    public static DeductiveSudokuSolver withFailWhenUnsolved(boolean failWhenUnsolved) {
        return new DeductiveSudokuSolver(null, failWhenUnsolved);
    }

    private enum DeductionLevel {
        LEVEL_1,
        LEVEL_2
    }

    @Override
    public Sudoku solve(Sudoku originalSudoku) {
        final LogicSudokuSolverInternalModel sudoku = new LogicSudokuSolverInternalModel();

        final List<SudokuPosition> openPositions = new ArrayList<>();

        for (SudokuPosition position : SudokuPosition.allPositions) {
            if (originalSudoku.isPositionValid(position)) {
                sudoku.setValue(position, originalSudoku.getValueForPosition(position));
            } else {
                openPositions.add(position);
            }
        }

        boolean changedSomethingInTheLastIteration;
        DeductionLevel currentLevel = DeductionLevel.LEVEL_1;

        do {
            changedSomethingInTheLastIteration = false;
            for (SudokuPosition openPosition : openPositions.stream().collect(Collectors.toUnmodifiableList())) {
                if (sudoku.checkAndRuleOut(openPosition, currentLevel)) {
                    changedSomethingInTheLastIteration = true;
                    openPositions.remove(openPosition);
                }
            }
            if (!changedSomethingInTheLastIteration && currentLevel == DeductionLevel.LEVEL_1) {
                currentLevel = DeductionLevel.LEVEL_2;
                changedSomethingInTheLastIteration = true;
            } else {
                currentLevel = DeductionLevel.LEVEL_1;
            }
        } while (!sudoku.internalSudoku.isCompleted() && (changedSomethingInTheLastIteration));

        if (!sudoku.internalSudoku.isCompleted()) {
            if (failWhenUnsolved) {
                System.out.println(sudoku);
                throw new RuntimeException("Solver found no solution");
            }
            if (fallbackSolver != null) {
                return fallbackSolver.solve(sudoku.internalSudoku);
            }
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

        private boolean alreadySet(SudokuPosition position) {
            return internalSudoku.isPositionValid(position);
        }

        private List<Integer> getPossibleValuesForPosition(SudokuPosition position) {
            return possibleValues[position.getRow()][position.getCol()];
        }

        private boolean checkAndRuleOut(SudokuPosition position, DeductionLevel currentLevel) {
            final List<Integer> possibleValuesForPosition = getPossibleValuesForPosition(position);
            if (possibleValuesForPosition.size() == 1) {
                int lastPossibleValue = possibleValuesForPosition.get(0);
                setValue(position, lastPossibleValue);
                return true;
            }
            if (currentLevel == DeductionLevel.LEVEL_2) {
                for (Integer possibleValueForPosition : possibleValuesForPosition) {
                    if (checkIfValueIsUniquePosition(position, possibleValueForPosition)) {
                        setValue(position, possibleValueForPosition);
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean checkIfValueIsUniquePosition(SudokuPosition position, int possibleValueForPosition) {
            return countPossiblePositionsForValueInRow(position, possibleValueForPosition) == 1
                    || countPossiblePositionsForValueInCol(position, possibleValueForPosition) == 1
                    || countPossiblePositionsForValueInField(position, possibleValueForPosition) == 1;
        }

        private long countPossiblePositionsForValueInRow(SudokuPosition position, int possibleValueForPosition) {
            return position.getPositionsInSameRow().stream().map(this::getPossibleValuesForPosition)
                    .filter(possibleValues -> possibleValues.contains(possibleValueForPosition))
                    .count();
        }

        private long countPossiblePositionsForValueInCol(SudokuPosition position, int possibleValueForPosition) {
            return position.getPositionsInSameColumn().stream().map(this::getPossibleValuesForPosition)
                    .filter(possibleValues -> possibleValues.contains(possibleValueForPosition))
                    .count();
        }

        private long countPossiblePositionsForValueInField(SudokuPosition position, int possibleValueForPosition) {
            return position.getPositionsInSameBox().stream().map(this::getPossibleValuesForPosition)
                    .filter(possibleValues -> possibleValues.contains(possibleValueForPosition))
                    .count();
        }

        private boolean setValue(SudokuPosition position, int value) {
            if (internalSudoku.isPositionValid(position)) {
                return false;
            }

            if (isValidValue(value)) {
                internalSudoku.setValueForPosition(position, value);
                position.getPositionsToBeRuledOut().forEach(positionInSameRow -> {
                    getPossibleValuesForPosition(positionInSameRow).remove((Integer) value);
                });
                possibleValues[position.getRow()][position.getCol()].clear();
            }

            return false;
        }

        public String toPrintableString() {
            final String horizontalFieldDelimiter = "|";
            final String spacer = " ";
            final String longSpacer = "      ";
            final String lineSeparator = "|| --- --- --- | --- --- --- | --- --- --- || --- --- --- | --- --- --- | --- --- --- || --- --- --- | --- --- --- | --- --- --- ||\n";
            final String lineFiller = lineSeparator.replaceAll("-", " ");
            final String boxSeparator = lineSeparator.replaceAll("-", "=");
            final String possibleValuesTemplate = "123 456 789";

            final StringBuffer sb = new StringBuffer();
            final StringBuffer possibleValuesSb = new StringBuffer();

            sb.append(boxSeparator);

            for (SudokuPosition position : allPositions) {
                int value = internalSudoku.getValueForPosition(position);
                final String valueAsString = value == 0 ? " " : value + "";
                if (position.getCol() % 3 == 0 && !horizontalFieldDelimiter.isBlank()) {
                    sb.append(horizontalFieldDelimiter).append(horizontalFieldDelimiter).append(longSpacer);
                    possibleValuesSb.append(horizontalFieldDelimiter).append(horizontalFieldDelimiter).append(spacer);
                } else {
                    sb.append(horizontalFieldDelimiter).append(longSpacer);
                    possibleValuesSb.append(horizontalFieldDelimiter).append(spacer);

                }
                sb.append(ANSI_RED).append(valueAsString).append(ANSI_RESET).append(longSpacer);

                if (value == 0) {
                    final List<Integer> possibleValuesForPosition = possibleValues[position.getRow()][position.getCol()];
                    final String possibleValuesAsString = Arrays.stream(possibleValuesTemplate.split("")).map(c -> {
                        if (c.isBlank() || possibleValuesForPosition.contains(Integer.parseInt(c))) {
                            return c;
                        }
                        return " ";
                    }).collect(Collectors.joining());

                    possibleValuesSb.append(possibleValuesAsString).append(spacer);
                } else {
                    possibleValuesSb.append(longSpacer).append(longSpacer);
                }

                if (position.getCol() == 8) {
                    sb.append(horizontalFieldDelimiter).append("|\n");
                    sb.append(lineFiller);
                    sb.append(possibleValuesSb).append("||\n");
                    possibleValuesSb.delete(0, possibleValuesSb.length());
                    sb.append(lineFiller);
                    if (position.getRow() % 3 == 2) {
                        sb.append(boxSeparator);
                    } else {
                        sb.append(lineSeparator);
                    }
                }
            }

            return sb.toString();
        }

        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_RED = "\u001B[31m";

        public String toString() {
            return toPrintableString();
        }

    }
}
