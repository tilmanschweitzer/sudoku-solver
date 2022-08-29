package de.tilmanschweitzer.sudoku.solver;

import de.tilmanschweitzer.sudoku.model.Sudoku;
import de.tilmanschweitzer.sudoku.model.SudokuPosition;

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

        final List<Sudoku> solutions = findSolutions(sudoku);

        if (solutions.size() == 0) {
            throw new RuntimeException("No solution found");
        }
        if (solutions.size() > 1) {
            throw new RuntimeException("Found " + solutions.size() + " solutions");
        }

        return solutions.get(0);
    }

    public List<Sudoku> findSolutions(Sudoku sudoku) {
        return findSolutions(sudoku, Optional.empty());
    }

    private List<Sudoku> findSolutions(Sudoku sudoku, Optional<SudokuPosition> latestPosition) {
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
            return findSolutions(copy, firstUnsetPositionOptional);
        }).reduce((sudokus, sudokus2) -> Stream.concat(sudokus.stream(), sudokus2.stream()).collect(Collectors.toUnmodifiableList())).orElse(emptyList());
    }
}
