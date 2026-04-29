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

    public void start() {
        startTime = System.currentTimeMillis() - elapsedTime;
        running = true;
    }

    public void stop() {
        if (running) {
            elapsedTime = System.currentTimeMillis() - startTime;
            running = false;
        }
    }

    public void reset() {
        startTime = 0;
        elapsedTime = 0;
        running = false;
    }

    public long getElapsedTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        }
        return elapsedTime;
    }

    public String getFormattedTime() {
        long seconds = getElapsedTime() / 1000;
        long millis = (getElapsedTime() % 1000) / 10;
        return String.format("%02d:%02d", seconds, millis);
    }
}
