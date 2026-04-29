package game.model;

/**
 * Interface for observers that want to be notified of game state changes.
 */
public interface ParkingObserver {
    /**
     * Called when the game model changes.
     */
    void update();
}
