package de.tilmanschweitzer.sudoku.shell;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class BacktrackingSudokuSolver implements SudokuSolver {

    @Override
    public Sudoku solve(Sudoku sudoku) {

        final List<Sudoku> solutions = solutions(sudoku, Optional.empty());

        if (solutions.size() == 0) {
            throw new RuntimeException("No solution found");
        }
        if (solutions.size() > 1) {
            throw new RuntimeException("Multiple solutions found");
        }

        return solutions.get(0);
    }

    private List<Sudoku> solutions(Sudoku sudoku, Optional<SudokuPosition> latestPosition) {
        if (!sudoku.isValid()) {
            return emptyList();
        } else if (sudoku.isCompleted()) {
            return singletonList(sudoku);
        }

        final Optional<SudokuPosition> firstUnsetPositionOptional = sudoku.getFirstUnsetPosition(latestPosition);
        if (firstUnsetPositionOptional.isEmpty()) {
            throw new RuntimeException("Excepted to have at least one unset position when sudoku is not completed");
        }

        final SudokuPosition position = firstUnsetPositionOptional.get();
        return IntStream.range(1, 10).boxed().parallel().map((value) -> {
            final Sudoku copy = Sudoku.fromSudoku(sudoku);
            copy.setValueForPosition(position, value);
            return solutions(copy, firstUnsetPositionOptional);
        }).reduce((sudokus, sudokus2) -> Stream.concat(sudokus.stream(), sudokus2.stream()).collect(Collectors.toUnmodifiableList())).orElse(emptyList());
    }
}
