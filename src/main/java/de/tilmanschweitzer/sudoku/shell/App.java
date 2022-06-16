package de.tilmanschweitzer.sudoku.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
public class App {

    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.asList(args));

        if (args.length < 1) {
            System.err.println("No filename given");
            System.exit(1);
        }

        final String filename = args[0];
        try (BufferedReader bufferedReader = Files.newBufferedReader(Path.of(filename))) {
            bufferedReader.lines().skip(1).limit(1).forEach((line) -> {
                final String[] split = line.split(",");
                if (split.length < 2 || split[0].length() != 81 || split[1].length() != 81) {
                    throw new SudokuFormatException();
                }

                final Sudoku unsolvedSudoku = Sudoku.fromString(split[0]);
                final Sudoku solvedSudoku = Sudoku.fromString(split[1]);

                System.out.println(unsolvedSudoku);


            });
        }
    }


}
