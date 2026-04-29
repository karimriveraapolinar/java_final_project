package game.model;

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
}
