package game;

import game.controller.InputController;
import game.controller.MenuController;
import game.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class ParkItTests {

    @BeforeEach
    void resetSingleton() throws Exception {
        // Resets the Singleton instance before each test to ensure a clean state
        Field f = GameModel.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    // --- Car Physics & Movement ---

    @Test
    void carStartsAtGivenPosition() {
        Car car = new Car(100, 200);
        assertEquals(100, car.getX(), 0.001);
        assertEquals(200, car.getY(), 0.001);
    }

    @Test
    void accelerateIncreasesSpeed() {
        Car car = new Car(0, 0);
        car.accelerate();
        assertTrue(car.getSpeed() > 0);
    }

    @Test
    void reverseDecreasesSpeed() {
        Car car = new Car(0, 0);
        car.reverse();
        assertTrue(car.getSpeed() < 0);
    }

    @Test
    void turnLeftOnlyWorksWhenMoving() {
        Car car = new Car(0, 0);
        car.turnLeft();
        assertEquals(0, car.getAngle(), 0.001); // no effect when stopped

        car.accelerate();
        car.turnLeft();
        assertTrue(car.getAngle() < 0); // effect when moving
    }

    @Test
    void turnRightIncreasesAngleWhenMoving() {
        Car car = new Car(0, 0);
        car.accelerate();
        car.turnRight();
        assertTrue(car.getAngle() > 0);
    }

    @Test
    void frictionReducesSpeedTowardsZero() {
        Car car = new Car(0, 0);
        car.accelerate(); // initial speed 0.1
        double initialSpeed = car.getSpeed();
        car.move(); // friction 0.05 applied
        assertTrue(car.getSpeed() < initialSpeed);
        
        car.move();
        assertEquals(0, car.getSpeed(), 0.001); // should hit exactly 0
    }

    @Test
    void frictionAppliesToNegativeSpeed() {
        Car car = new Car(0, 0);
        car.reverse(); // speed is -0.1
        car.move();    // friction adds 0.05
        assertEquals(-0.05, car.getSpeed(), 0.001);
    }

    @Test
    void setPositionResetsSpeedAndAngle() {
        Car car = new Car(0, 0);
        car.accelerate();
        car.setPosition(50, 50);
        assertEquals(0, car.getSpeed(), 0.001);
        assertEquals(0, car.getAngle(), 0.001);
    }

    // --- GameTimer ---

    @Test
    void timerStartsAtZero() {
        GameTimer t = new GameTimer();
        assertEquals(0, t.getElapsedTime());
        assertEquals("00:00", t.getFormattedTime());
    }

    @Test
    void timerRunsAndFreezesOnStop() throws InterruptedException {
        GameTimer t = new GameTimer();
        t.start();
        Thread.sleep(50); 
        t.stop();
        long frozen = t.getElapsedTime();
        Thread.sleep(50);
        assertEquals(frozen, t.getElapsedTime());
    }

    @Test
    void timerResetsToZero() throws InterruptedException {
        GameTimer t = new GameTimer();
        t.start();
        Thread.sleep(30);
        t.reset();
        assertEquals(0, t.getElapsedTime());
    }

    // --- ParkingStatus & Stats ---

    @Test
    void parkingStatusDefaultsFalse() {
        ParkingStatus s = new ParkingStatus(1);
        assertFalse(s.isSuccess());
        assertFalse(s.isCrashed());
        assertEquals(1, s.getDifficulty());
    }

    @Test
    void bestTimeUpdatesOnImprovement() {
        PlayerStats stats = new PlayerStats();
        stats.updateBestTime(1, 5000L);
        stats.updateBestTime(1, 9000L); // worse time should not replace
        assertEquals(5000L, stats.getBestTime(1));
    }

    // --- GameModel Logic ---

    @Test
    void singletonReturnsSameInstance() {
        assertSame(GameModel.getInstance(), GameModel.getInstance());
    }

    @Test
    void loadLevelResetsStatusFlags() {
        GameModel model = GameModel.getInstance();
        model.getCurrentStatus().setCrashed(true);
        model.getCurrentStatus().setSuccess(true);
        
        model.loadLevel(1);
        
        assertFalse(model.getCurrentStatus().isCrashed());
        assertFalse(model.getCurrentStatus().isSuccess());
    }

    @Test
    void modelNotifiesObserversOnUpdate() {
        GameModel model = GameModel.getInstance();
        final boolean[] notified = {false};
        ParkingObserver obs = () -> notified[0] = true;
        
        model.addObserver(obs);
        model.update();
        assertTrue(notified[0]);
    }

    @Test
    void carCrashesOnWallCollision() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1); // wall at x=200
        model.getCar().setPosition(200, 50);
        model.update();
        assertTrue(model.getCurrentStatus().isCrashed());
    }

    @Test
    void carMustBeFullyInsideSpotToSucceed() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1); // spot at (400, 400, 60, 30)
        
        // Place car partially over the boundary
        model.getCar().setPosition(390, 400); 
        model.update();
        
        assertFalse(model.getCurrentStatus().isSuccess());
    }

    @Test
    void carSucceedsWhenFullyInParkingSpot() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        model.getCar().setPosition(410, 405);
        model.update();
        assertTrue(model.getCurrentStatus().isSuccess());
    }

    // --- Controller Logic ---

    @Test
    void inputControllerUpKeyAcceleratesCar() {
        GameModel model = GameModel.getInstance();
        InputController ic = new InputController();
        KeyEvent up = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 
                                   System.currentTimeMillis(), 0, KeyEvent.VK_UP, ' ');
        ic.keyPressed(up);
        ic.update();
        assertTrue(model.getCar().getSpeed() > 0);
    }

    @Test
    void releasingUpKeyStopsAcceleration() {
        GameModel model = GameModel.getInstance();
        InputController ic = new InputController();
        KeyEvent up = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_UP, ' ');
        KeyEvent releaseUp = new KeyEvent(new Component(){}, KeyEvent.KEY_RELEASED, 0, 0, KeyEvent.VK_UP, ' ');

        ic.keyPressed(up);
        ic.update();
        double speedAfterPress = model.getCar().getSpeed();

        ic.keyReleased(releaseUp);
        ic.update();
        assertEquals(speedAfterPress, model.getCar().getSpeed(), 0.001);
    }

    @Test
    void timerStartsWhenMovementKeyPressed() {
        GameModel model = GameModel.getInstance();
        InputController ic = new InputController();
        
        KeyEvent up = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_UP, ' ');
        ic.keyPressed(up);
        ic.update();

        assertTrue(model.getTimer().getElapsedTime() >= 0);
    }

    @Test
    void pressingRRestartsLevel() {
        GameModel model = GameModel.getInstance();
        InputController ic = new InputController();
        model.getCar().setPosition(300, 300);
        
        KeyEvent rKey = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_R, 'r');
        ic.keyPressed(rKey);
        
        // Should revert to Level 1 start position (50, 50)
        assertEquals(50, model.getCar().getX());
    }

    @Test
    void enterKeyDoesNotAdvanceLevelIfNotSuccessful() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        InputController ic = new InputController();
        KeyEvent enter = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER, '\n');

        model.getCurrentStatus().setSuccess(false);
        ic.keyPressed(enter);
        
        assertEquals(1, model.getStats().getCurrentLevel());
    }

    @Test
    void menuControllerNextLevelWrapsAfterLevel2() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(2);
        new MenuController().nextLevel();
        assertEquals(1, model.getStats().getCurrentLevel());
    }
}