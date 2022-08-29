package de.tilmanschweitzer.sudoku.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ExecutionTimer {

    final List<Long> measuredExecutionTimesInMilliseconds = new ArrayList<>();
    public <T> T execute(Supplier<T> supplier) {
        final long startTime = System.currentTimeMillis();
        T result = supplier.get();
        final long stopTime = System.currentTimeMillis();
        final long measuredTime = stopTime - startTime;
        addExecutionTime(measuredTime);
        return result;
    }

    public void addExecutionTime(long executionTime) {
        measuredExecutionTimesInMilliseconds.add(executionTime);
    }

    public Optional<Long> getLatestExecutionTime() {
        if (measuredExecutionTimesInMilliseconds.isEmpty()) {
            return Optional.empty();
        }
        final int lastIndex = measuredExecutionTimesInMilliseconds.size() - 1;
        return Optional.of(measuredExecutionTimesInMilliseconds.get(lastIndex));
    }

    public int getNumberOfExecutions() {
        return measuredExecutionTimesInMilliseconds.size();
    }

    public Optional<Long> getExecutionTimeSum() {
        if (measuredExecutionTimesInMilliseconds.isEmpty()) {
            return Optional.empty();
        }
        return measuredExecutionTimesInMilliseconds.stream().reduce(Long::sum);
    }

    public Optional<Long> getAverageExecutionTime() {
        if (measuredExecutionTimesInMilliseconds.isEmpty()) {
            return Optional.empty();
        }
        long average = getExecutionTimeSum().orElseGet(() -> 0L) / getNumberOfExecutions();
        return Optional.of(average);
    }

    public Optional<Long> getMedianExecutionTime() {
        if (measuredExecutionTimesInMilliseconds.isEmpty()) {
            return Optional.empty();
        }
        final List<Long> sortedTimes = measuredExecutionTimesInMilliseconds.stream().sorted().collect(Collectors.toUnmodifiableList());
        if (getNumberOfExecutions() % 2 == 1) {
            int indexMiddle = getNumberOfExecutions() / 2;
            return Optional.of(sortedTimes.get(indexMiddle));
        } else {
            int indexLeftMiddle = getNumberOfExecutions() / 2 - 1;
            int indexRightMiddle = getNumberOfExecutions() / 2;
            long median = (sortedTimes.get(indexLeftMiddle) + sortedTimes.get(indexRightMiddle)) / 2;
            return Optional.of(median);
        }
    }

    public Optional<Long> getMinExecutionTime() {
        return measuredExecutionTimesInMilliseconds.stream().min(Long::compareTo);
    }

    public Optional<Long> getMaxExecutionTime() {
        return measuredExecutionTimesInMilliseconds.stream().max(Long::compareTo);
    }
}
