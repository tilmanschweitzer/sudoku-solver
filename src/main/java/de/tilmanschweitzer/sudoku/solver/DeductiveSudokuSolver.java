package de.tilmanschweitzer.sudoku.solver;

import de.tilmanschweitzer.sudoku.model.Sudoku;
import de.tilmanschweitzer.sudoku.model.SudokuPosition;

import java.util.*;
import java.util.stream.Collectors;

import static de.tilmanschweitzer.sudoku.model.SudokuPosition.allPositions;
import static de.tilmanschweitzer.sudoku.model.SudokuUtils.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
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

    private class DeductionLevel {
        private static final int LEVEL_1 = 1;
        private static final int LEVEL_2 = 2;
        private static final int LEVEL_3 = 3;
    }

    @Override
    public Sudoku solve(Sudoku originalSudoku) {
        final LogicSudokuSolverInternalModel sudoku = new LogicSudokuSolverInternalModel();

        final List<SudokuPosition> openPositions = allPositions.stream()
                .filter(position -> isUnsetValue(originalSudoku.getValueForPosition(position)))
                .collect(toList());

        allPositions.forEach(position -> sudoku.setValue(position, originalSudoku.getValueForPosition(position)));

        boolean changedSomethingInTheLastIteration;
        int currentDeductionLevel = DeductionLevel.LEVEL_1;

        do {
            changedSomethingInTheLastIteration = false;
            final List<SudokuPosition> previousOpenPositions = openPositions.stream().collect(toUnmodifiableList());

            for (SudokuPosition openPosition : previousOpenPositions) {
                if (sudoku.checkAndRuleOut(openPosition, currentDeductionLevel)) {
                    changedSomethingInTheLastIteration = true;
                    openPositions.remove(openPosition);
                }
            }
            if (!changedSomethingInTheLastIteration && currentDeductionLevel == DeductionLevel.LEVEL_3) {
                for (SudokuPosition openPosition : previousOpenPositions) {
                    if (sudoku.findAndRuleOutConjugatePairs(openPosition, 2)) {
                        changedSomethingInTheLastIteration = true;
                    }
                    if (sudoku.findAndRuleOutXYWings(openPosition, previousOpenPositions)) {
                        changedSomethingInTheLastIteration = true;
                    }
                }
            }
            if (!changedSomethingInTheLastIteration) {
                currentDeductionLevel++;
            } else {
                currentDeductionLevel = DeductionLevel.LEVEL_1;
            }
        } while (!sudoku.internalSudoku.isCompleted() && (changedSomethingInTheLastIteration || currentDeductionLevel <= DeductionLevel.LEVEL_3));

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
            for (SudokuPosition positions : allPositions) {
                possibleValues[positions.getRow()][positions.getCol()] = range(1, 10).boxed().collect(toList());
            }
        }

        private boolean alreadySet(SudokuPosition position) {
            return internalSudoku.isPositionValid(position);
        }

        private List<Integer> getPossibleValuesForPosition(SudokuPosition position) {
            return possibleValues[position.getRow()][position.getCol()];
        }

        private int numberOfPossibleValueForPosition(SudokuPosition position) {
            return getPossibleValuesForPosition(position).size();
        }

        private boolean matchesNumberOfPossibleValuesForPosition(int expectedNumber, SudokuPosition position) {
            return numberOfPossibleValueForPosition(position) == expectedNumber;
        }

        private boolean checkAndRuleOut(SudokuPosition position, int currentDeductionLevel) {
            final List<Integer> possibleValuesForPosition = getPossibleValuesForPosition(position);
            if (possibleValuesForPosition.size() == 1) {
                int lastPossibleValue = possibleValuesForPosition.get(0);
                setValue(position, lastPossibleValue);
                return true;
            }
            if (currentDeductionLevel == DeductionLevel.LEVEL_2) {
                for (Integer possibleValueForPosition : possibleValuesForPosition) {
                    if (checkIfValueIsUniquePosition(position, possibleValueForPosition)) {
                        setValue(position, possibleValueForPosition);
                        return true;
                    }
                }
            }

            return false;
        }

        private boolean findAndRuleOutConjugatePairs(SudokuPosition position, int pairMembers) {
            final List<Integer> possibleValuesForPosition = getPossibleValuesForPosition(position);
            if (possibleValuesForPosition.size() == pairMembers) {
                boolean foundNewConjugatePairInRows = findAndRuleOutConjugatePairs(position.getPositionsInSameRow(), possibleValuesForPosition, pairMembers);
                boolean foundNewConjugatePairInColumns = findAndRuleOutConjugatePairs(position.getPositionsInSameColumn(), possibleValuesForPosition, pairMembers);
                boolean foundNewConjugatePairInBoxes = findAndRuleOutConjugatePairs(position.getPositionsInSameBox(), possibleValuesForPosition, pairMembers);
                return foundNewConjugatePairInRows || foundNewConjugatePairInColumns || foundNewConjugatePairInBoxes;
            }
            return false;
        }

        private boolean findAndRuleOutConjugatePairs(List<SudokuPosition> positions, List<Integer> possibleValuesForPosition, int pairMembers) {
            final List<SudokuPosition> conjugatePairs = findConjugatePairs(positions, possibleValuesForPosition);
            if (conjugatePairs.size() != pairMembers) {
                return false;
            }
            return ruleOutConjugatePairsInPositions(positions, conjugatePairs, possibleValuesForPosition);
        }

        private List<SudokuPosition> findConjugatePairs(List<SudokuPosition> positions, List<Integer> possibleValuesForPosition) {
            return positions.stream()
                    .filter(positionToBeChecked -> getPossibleValuesForPosition(positionToBeChecked).equals(possibleValuesForPosition))
                    .collect(toUnmodifiableList());
        }

        private boolean ruleOutConjugatePairsInPositions(List<SudokuPosition> positions, List<SudokuPosition> conjugatePair, List<Integer> possibleValuesForPosition) {
            final long changes = positions.stream().filter(positionsInRow -> {
                if (conjugatePair.contains(positionsInRow)) {
                    return false;
                }
                return getPossibleValuesForPosition(positionsInRow).removeAll(possibleValuesForPosition);
            }).count();
            return changes > 0;
        }

        public boolean findAndRuleOutXYWings(SudokuPosition position, List<SudokuPosition> openPositions) {
            final List<Integer> possibleValuesForPosition = getPossibleValuesForPosition(position);

            boolean foundAndRuledOutXYWing = false;

            if (!matchesNumberOfPossibleValuesForPosition(2, position)) {
                return false;
            }

            final Integer leftValue = possibleValuesForPosition.get(0);
            final Integer rightValue = possibleValuesForPosition.get(1);

            final List<SudokuPosition> xyWingCandidates = position.getPositionsToBeRuledOut().stream()
                    .filter(otherPosition -> {
                        final List<Integer> possibleValuesForOtherPosition = getPossibleValuesForPosition(otherPosition);
                        return possibleValuesForOtherPosition.size() == 2 &&
                                (possibleValuesForOtherPosition.contains(leftValue) || possibleValuesForOtherPosition.contains(rightValue));
                    }).collect(toUnmodifiableList());


                final List<Integer> xWingValues = xyWingCandidates.stream()
                        .map(xyWingCandidate -> getPossibleValuesForPosition(xyWingCandidate).stream()
                                .filter(xyWingValue -> !xyWingValue.equals(rightValue) && !xyWingValue.equals(leftValue))
                                .collect(toUnmodifiableList()))
                        .filter(possibleValuesForXYWing -> possibleValuesForXYWing.size() == 1)
                        .map(possibleValuesForXYWing -> possibleValuesForXYWing.get(0))
                        .collect(toList());

                final List<Integer> duplicateValues = getDuplicateValues(xWingValues);
                if (duplicateValues.size() != 1) {
                    return false;
                }

                final Integer duplicateValue = duplicateValues.get(0);

            final Optional<SudokuPosition> xyWingLeftOptional = xyWingCandidates.stream()
                    .filter(xyWingCandidate -> {
                        final List<Integer> values = getPossibleValuesForPosition(xyWingCandidate);
                        return values.contains(leftValue) && values.contains(duplicateValue);
                    })
                    .findFirst();

            final Optional<SudokuPosition> xyWingRightOptional = xyWingCandidates.stream()
                    .filter(xyWingCandidate -> {
                        final List<Integer> values = getPossibleValuesForPosition(xyWingCandidate);
                        return values.contains(rightValue) && values.contains(duplicateValue);
                    })
                    .findFirst();

            if (xyWingLeftOptional.isEmpty() || xyWingRightOptional.isEmpty()) {
                return false;
            }

            final SudokuPosition xyWingLeft = xyWingLeftOptional.get();
            final SudokuPosition xyWingRight = xyWingRightOptional.get();

            return openPositions.stream().map(openPosition -> {
                if (xyWingLeft == openPosition || xyWingRight == openPosition) {
                    return false;
                }
                final List<Integer> possibleValuesForOpenPosition = getPossibleValuesForPosition(openPosition);
                if (!possibleValuesForOpenPosition.contains(duplicateValue)) {
                    return false;
                }
                if (!(openPosition.sharesSameHouse(xyWingLeft) && openPosition.sharesSameHouse(xyWingRight))) {
                    return false;
                }
                possibleValuesForOpenPosition.remove(duplicateValue);
                return true;
            }).anyMatch(value -> value);
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

            if (isUnsetValue(value)) {
                return false;
            }
            internalSudoku.setValueForPosition(position, value);
            position.getPositionsToBeRuledOut().forEach(positionInSameRow -> {
                getPossibleValuesForPosition(positionInSameRow).remove((Integer) value);
            });
            possibleValues[position.getRow()][position.getCol()].clear();
            return true;
        }

        public String toPrintableString() {
            final String horizontalFieldDelimiter = "|";
            final String spacer = " ";
            final String longSpacer = "      ";
            final String lineSeparator = "|| --- --- --- | --- --- --- | --- --- --- || --- --- --- | --- --- --- | --- --- --- || --- --- --- | --- --- --- | --- --- --- ||\n";
            final String lineFiller = lineSeparator.replaceAll("-", " ");
            final String boxSeparator = lineSeparator.replaceAll("-", "=");
            final String possibleValuesTemplate = "123 456 789";

            final StringBuilder sb = new StringBuilder();
            final StringBuilder possibleValuesSb = new StringBuilder();

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
