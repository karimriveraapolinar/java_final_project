package game.model;

/**
 * Represents the status of a parking attempt.
 * Success/fail, level difficulty.
 */
public class ParkingStatus {
    private boolean success;
    private int difficulty;
    private boolean crashed;

    public ParkingStatus(int difficulty) {
        this.difficulty = difficulty;
        this.success = false;
        this.crashed = false;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isCrashed() {
        return crashed;
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
