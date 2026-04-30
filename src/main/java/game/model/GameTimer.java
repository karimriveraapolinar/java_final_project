package game.model;

/**
 * Handles game timing.
 */
public class GameTimer {
    private long startTime;
    private long elapsedTime;
    private boolean running;

    public GameTimer() {
        reset();
    }

    /**
     * Starts or resumes the timer.
     */
    public void start() {
        startTime = System.currentTimeMillis() - elapsedTime;
        running = true;
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        if (running) {
            elapsedTime = System.currentTimeMillis() - startTime;
            running = false;
        }
    }

    /**
     * Resets the timer to zero.
     */
    public void reset() {
        startTime = 0;
        elapsedTime = 0;
        running = false;
    }

    /**
     * @return the total time elapsed in milliseconds.
     */
    public long getElapsedTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        }
        return elapsedTime;
    }

    /**
     * @return the elapsed time as a MM:SS string.
     */
    public String getFormattedTime() {
        long seconds = getElapsedTime() / 1000;
        long millis = (getElapsedTime() % 1000) / 10;
        return String.format("%02d:%02d", seconds, millis);
    }
}
