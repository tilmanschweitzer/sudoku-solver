package de.tilmanschweitzer.sudoku.model;

import java.util.List;

import static java.util.stream.Collectors.toUnmodifiableList;

public final class SudokuUtils {
    public static boolean isUnsetValue(int value) {
        return value == 0;
    }

    public static boolean isValidValue(int value) {
        return value >= 1 && value <= 9;
    }

    public static boolean hasDuplicateValues(List<Integer> values) {
        final List<Integer> validValues = values.stream().filter(SudokuUtils::isValidValue).collect(toUnmodifiableList());
        return validValues.size() > validValues.stream().distinct().count();
    }

    public static List<Integer> getDuplicateValues(List<Integer> values) {
        return values.stream()
                .filter(value -> values.stream().filter(otherValue -> otherValue.equals(value)).count() > 1)
                .distinct()
                .collect(toUnmodifiableList());
    }
}
