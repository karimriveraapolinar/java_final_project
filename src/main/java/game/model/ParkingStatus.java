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

    /**
     * @return true if the car is successfully parked.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success sets the success state.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return true if the car has crashed.
     */
    public boolean isCrashed() {
        return crashed;
    }

    /**
     * @param crashed sets the crashed state.
     */
    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }

    /**
     * @return the current difficulty level.
     */
    public int getDifficulty() {
        return difficulty;
    }
}
