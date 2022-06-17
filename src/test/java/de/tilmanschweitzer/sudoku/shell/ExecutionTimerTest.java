package de.tilmanschweitzer.sudoku.shell;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

class ExecutionTimerTest {

    @Test
    public void getNumberOfExecutions_returnsTheCorrectNumberOfExecutions() {
        final ExecutionTimer executionTimer = new ExecutionTimer();

        assertThat(executionTimer.getNumberOfExecutions(), is(0));

        executionTimer.addExecutionTime(113);
        assertThat(executionTimer.getNumberOfExecutions(), is(1));

        executionTimer.addExecutionTime(17);
        executionTimer.addExecutionTime(23);
        assertThat(executionTimer.getNumberOfExecutions(), is(3));
    }

    @Test
    public void getExecutionTimeSum_returnsTheSumOfAllValues() {
        final ExecutionTimer executionTimer = new ExecutionTimer();

        assertThat(executionTimer.getExecutionTimeSum(), is(Optional.empty()));

        executionTimer.addExecutionTime(113);
        assertThat(executionTimer.getExecutionTimeSum().get(), is(113L));

        executionTimer.addExecutionTime(17);
        executionTimer.addExecutionTime(23);
        assertThat(executionTimer.getExecutionTimeSum().get(), is(153L));
    }

    @Test
    public void getAverageExecutionTime_returnsTheArithmeticAverageOfAllValues() {
        final ExecutionTimer executionTimer = new ExecutionTimer();

        assertThat(executionTimer.getAverageExecutionTime(), is(Optional.empty()));

        executionTimer.addExecutionTime(113);
        assertThat(executionTimer.getAverageExecutionTime().get(), is(113L));

        executionTimer.addExecutionTime(17);
        executionTimer.addExecutionTime(23);
        assertThat(executionTimer.getAverageExecutionTime().get(), is(51L));
    }

    @Test
    public void getMinExecutionTime_returnsTheMinimumOfAllValues() {
        final ExecutionTimer executionTimer = new ExecutionTimer();

        assertThat(executionTimer.getMinExecutionTime(), is(Optional.empty()));

        executionTimer.addExecutionTime(113);
        assertThat(executionTimer.getMinExecutionTime().get(), is(113L));

        executionTimer.addExecutionTime(17);
        executionTimer.addExecutionTime(23);
        assertThat(executionTimer.getMinExecutionTime().get(), is(17L));
    }

    @Test
    public void getMaxExecutionTime_returnsTheMaximumOfAllValues() {
        final ExecutionTimer executionTimer = new ExecutionTimer();

        assertThat(executionTimer.getMaxExecutionTime(), is(Optional.empty()));

        executionTimer.addExecutionTime(113);
        assertThat(executionTimer.getMaxExecutionTime().get(), is(113L));

        executionTimer.addExecutionTime(17);
        executionTimer.addExecutionTime(23);
        assertThat(executionTimer.getMaxExecutionTime().get(), is(113L));
    }

    @Test
    public void getMedianExecutionTime_returnsTheMedianForOddNumberOfValues() {
        final ExecutionTimer executionTimer = new ExecutionTimer();

        assertThat(executionTimer.getMedianExecutionTime(), is(Optional.empty()));

        executionTimer.addExecutionTime(113);
        assertThat(executionTimer.getMedianExecutionTime().get(), is(113L));

        executionTimer.addExecutionTime(17);
        executionTimer.addExecutionTime(23);
        assertThat(executionTimer.getMedianExecutionTime().get(), is(23L));
    }

    @Test
    public void getMedianExecutionTime_returnsTheMedianForEvenNumberOfValues() {
        final ExecutionTimer executionTimer = new ExecutionTimer();

        assertThat(executionTimer.getMedianExecutionTime(), is(Optional.empty()));

        executionTimer.addExecutionTime(113);
        executionTimer.addExecutionTime(17);
        executionTimer.addExecutionTime(23);
        executionTimer.addExecutionTime(25);
        assertThat(executionTimer.getMedianExecutionTime().get(), is(24L));
    }

    @Test
    public void getLatestExecutionTime_returnsTheLastAddedExecutionTime() {
        final ExecutionTimer executionTimer = new ExecutionTimer();

        assertThat(executionTimer.getLatestExecutionTime(), is(Optional.empty()));

        executionTimer.addExecutionTime(113);
        assertThat(executionTimer.getLatestExecutionTime().get(), is(113L));

        executionTimer.addExecutionTime(17);
        assertThat(executionTimer.getLatestExecutionTime().get(), is(17L));

        executionTimer.addExecutionTime(23);
        assertThat(executionTimer.getLatestExecutionTime().get(), is(23L));
    }

    @Test
    public void execute_returnsTheResultOfTheSupplier() {
        final ExecutionTimer executionTimer = new ExecutionTimer();

        final Integer result = executionTimer.execute(() -> 23);

        assertThat(result, is(23));
    }

    @Test
    public void execute_measuresTimeOfExecution() {
        final ExecutionTimer executionTimer = new ExecutionTimer();

        executionTimer.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 17;
        });

        assertThat(executionTimer.getNumberOfExecutions(), is(1));
        assertThat(executionTimer.getLatestExecutionTime().get(), greaterThan(1000L));
    }


}
