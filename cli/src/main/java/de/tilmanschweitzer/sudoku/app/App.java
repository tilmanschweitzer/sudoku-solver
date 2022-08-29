package de.tilmanschweitzer.sudoku.app;

import de.tilmanschweitzer.sudoku.model.Sudoku;
import de.tilmanschweitzer.sudoku.model.SudokuFormatException;
import de.tilmanschweitzer.sudoku.solver.DeductiveSudokuSolver;
import de.tilmanschweitzer.sudoku.solver.SudokuSolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.asList(args));

        if (args.length < 1) {
            System.err.println("No filename given");
            System.exit(1);
        }

        final String filename = args[0];
        final int offset = 1;
        final int limit = args.length > 1 ? Integer.parseInt(args[1]) : 100;

        final SudokuSolver sudokuSolver = DeductiveSudokuSolver.withFailWhenUnsolved(false);
        final ExecutionTimer executionTimer = new ExecutionTimer();

        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(filename))) {
            final List<Boolean> solvedSudokus = bufferedReader.lines().skip(offset).limit(limit).map((line) -> {
                final String[] split = line.split(",");
                if (split.length < 2 || split[0].length() != 81 || split[1].length() != 81) {
                    throw new SudokuFormatException();
                }

                final Sudoku unsolvedSudoku = Sudoku.fromString(split[0]);
                final Sudoku expectedSolution = Sudoku.fromString(split[1]);

                final Sudoku solvedSudoku = executionTimer.execute(() -> sudokuSolver.solve(unsolvedSudoku));

                System.out.println(unsolvedSudoku);
                System.out.println(solvedSudoku);
                System.out.println("Execution took: " + executionTimer.getLatestExecutionTime().orElse(0L) + "ms");
                System.out.println("==============================\n");

                return solvedSudoku.equals(expectedSolution);
            }).collect(Collectors.toUnmodifiableList());

            long numberOfSolvedSudokus = solvedSudokus.stream().filter(Boolean::booleanValue).count();
            System.out.println("Solved " + numberOfSolvedSudokus + " of " + solvedSudokus.size() + " sudokus");
            System.out.println("==============================\n");
            System.out.println("Execution time statistics:");
            System.out.println("Sum of execution times: " + executionTimer.getExecutionTimeSum().orElse(0L) + "ms");
            System.out.println("Average execution time: " + executionTimer.getAverageExecutionTime().orElse(0L) + "ms");
            System.out.println("Median execution time: " + executionTimer.getMedianExecutionTime().orElse(0L) + "ms");
            System.out.println("Min execution time: " + executionTimer.getMinExecutionTime().orElse(0L) + "ms");
            System.out.println("Max execution time: " + executionTimer.getMaxExecutionTime().orElse(0L) + "ms");
        }
    }
}
