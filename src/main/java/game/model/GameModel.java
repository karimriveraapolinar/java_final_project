package game.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * The central model for the parking game.
 * Implements the Singleton pattern.
 */
public class GameModel {
    private static GameModel instance;

    private Car car;
    private PlayerStats stats;
    private ParkingStatus currentStatus;
    private GameTimer timer;
    private List<Rectangle> obstacles;
    private Rectangle parkingSpot;

    private List<ParkingObserver> observers;

    private GameModel() {
        car = new Car(100, 100);
        stats = new PlayerStats();
        currentStatus = new ParkingStatus(1);
        timer = new GameTimer();
        obstacles = new ArrayList<>();
        observers = new ArrayList<>();
        loadLevel(1);
    }

    /**
     * @return the single instance of the GameModel.
     */
    public static synchronized GameModel getInstance() {
        if (instance == null) {
            instance = new GameModel();
        }
        return instance;
    }

    /**
     * Adds an observer to be notified of updates.
     * @param observer the observer to add.
     */
    public void addObserver(ParkingObserver observer) {
        observers.add(observer);
    }

    /**
     * Notifies all observers of changes in the model.
     */
    public void notifyObservers() {
        for (ParkingObserver observer : observers) {
            observer.update();
        }
    }

    /**
     * Loads a specific level and sets up its obstacles.
     * @param level the level number to load.
     */
    public void loadLevel(int level) {
        stats.setCurrentLevel(level);
        currentStatus = new ParkingStatus(level);
        car.setPosition(50, 50);
        timer.reset();
        
        obstacles.clear();
        // Simple level design
        if (level == 1) {
            obstacles.add(new Rectangle(200, 0, 20, 300));
            parkingSpot = new Rectangle(400, 400, 60, 30);
        } else if (level == 2) {
            obstacles.add(new Rectangle(150, 100, 300, 20));
            obstacles.add(new Rectangle(150, 300, 300, 20));
            parkingSpot = new Rectangle(500, 200, 60, 30);
        }
        
        notifyObservers();
    }

    /**
     * Updates the game state for the current frame.
     */
    public void update() {
        if (!currentStatus.isCrashed() && !currentStatus.isSuccess()) {
            car.move();
            checkCollisions();
            notifyObservers();
        }
    }

    /**
     * Checks for collisions with obstacles or boundaries.
     */
    private void checkCollisions() {
        Rectangle carBounds = car.getBounds();
        
        // Wall collisions
        for (Rectangle obs : obstacles) {
            if (carBounds.intersects(obs)) {
                currentStatus.setCrashed(true);
                timer.stop();
            }
        }
        
        // Parking spot detection
        if (parkingSpot.contains(carBounds)) {
            currentStatus.setSuccess(true);
            timer.stop();
            stats.updateBestTime(stats.getCurrentLevel(), timer.getElapsedTime());
        }
        
        // Boundary check
        if (car.getX() < 0 || car.getX() > 800 || car.getY() < 0 || car.getY() > 600) {
            currentStatus.setCrashed(true);
            timer.stop();
        }
    }

    // Getters
    /** @return the car object */
    public Car getCar() { return car; }
    /** @return player statistics */
    public PlayerStats getStats() { return stats; }
    /** @return current parking status */
    public ParkingStatus getCurrentStatus() { return currentStatus; }
    /** @return the game timer */
    public GameTimer getTimer() { return timer; }
    /** @return list of obstacles in the level */
    public List<Rectangle> getObstacles() { return obstacles; }
    /** @return the target parking spot */
    public Rectangle getParkingSpot() { return parkingSpot; }
}
