package game.model;

import java.awt.Rectangle;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit tests for the Game Model components.
 */
public class ModelTest {

    @Test
    public void testSingleton() {
        GameModel instance1 = GameModel.getInstance();
        GameModel instance2 = GameModel.getInstance();
        assertSame("GameModel should be a singleton", instance1, instance2);
    }

    @Test
    public void testCarMovement() {
        Car car = new Car(100, 100);
        car.accelerate();
        car.move();
        assertTrue("Car should have moved", car.getX() > 100 || car.getY() > 100);
    }

    @Test
    public void testPlayerStats() {
        PlayerStats stats = new PlayerStats();
        assertEquals(1, stats.getCurrentLevel());
        stats.setCurrentLevel(2);
        assertEquals(2, stats.getCurrentLevel());
    }

    @Test
    public void testGameTimer() {
        GameTimer timer = new GameTimer();
        timer.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        timer.stop();
        assertTrue("Timer should have recorded time", timer.getElapsedTime() >= 100);
    }

    @Test
    public void testCollisionDetection() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        Car car = model.getCar();
        // Move car into an obstacle
        car.setPosition(200, 100); 
        model.update();
        assertTrue("Car should be crashed", model.getCurrentStatus().isCrashed());
    }

    @Test
    public void testCarFriction() {
        Car car = new Car(100, 100);
        car.accelerate(); // Give it some speed
        double initialSpeed = car.getSpeed();
        car.move(); // Friction should apply during move
        assertTrue("Speed should decrease due to friction", car.getSpeed() < initialSpeed);
    }

    @Test
    public void testCarTurning() {
        Car car = new Car(100, 100);
        car.accelerate(); // Need speed to turn
        double initialAngle = car.getAngle();
        car.turnRight();
        assertTrue("Angle should have increased", car.getAngle() > initialAngle);
        
        car.turnLeft();
        assertEquals("Angle should be back to initial", initialAngle, car.getAngle(), 0.001);
    }

    @Test
    public void testLevelReset() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        model.getCar().accelerate();
        model.getCar().move();
        
        // Reset level
        model.loadLevel(1);
        assertEquals("Car X should reset to 50", 50, model.getCar().getX(), 0.001);
        assertEquals("Car Y should reset to 50", 50, model.getCar().getY(), 0.001);
        assertEquals("Speed should reset to 0", 0, model.getCar().getSpeed(), 0.001);
        assertFalse("Status should not be crashed", model.getCurrentStatus().isCrashed());
    }

    @Test
    public void testTimerFormatting() {
        GameTimer timer = new GameTimer();
        // Manually check formatting logic
        assertEquals("Initial time should be 00:00", "00:00", timer.getFormattedTime());
    }

    @Test
    public void testParkingSuccess() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        Car car = model.getCar();
        Rectangle spot = model.getParkingSpot();
        
        // Position car inside the parking spot
        car.setPosition(spot.x + 5, spot.y + 5);
        model.update();
        
        assertTrue("Car should be successfully parked", model.getCurrentStatus().isSuccess());
    }
}
