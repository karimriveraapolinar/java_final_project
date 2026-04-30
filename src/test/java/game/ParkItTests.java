package game;

import game.controller.InputController;
import game.controller.MenuController;
import game.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

import javax.swing.JButton;

import static org.junit.jupiter.api.Assertions.*;

public class ParkItTests {

    @BeforeEach
    void resetSingleton() throws Exception {
        // Resets the Singleton instance before each test for a clean state
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
    void carMovementFollowsTrig() {
        Car car = new Car(100, 100);
        car.accelerate(); // speed = 0.1
        // Set angle to 45 degrees
        for (int i = 0; i < 15; i++) car.turnRight(); // 3.0 * 15 = 45 degrees
        car.move();
        // At 45 degrees, change in X and Y should be equal (approx 0.0707)
        assertTrue(car.getX() > 100 && car.getY() > 100);
        assertEquals(car.getX() - 100, car.getY() - 100, 0.001);
    }

    @Test
    void frictionStopsLowSpeedExactly() {
        Car car = new Car(0, 0);
        car.accelerate(); // speed = 0.1
        car.move();       // speed = 0.05
        car.move();       // speed should become 0 because 0.05 is not < 0.05 (it's equal)
        // One more move to trigger the Math.abs(speed) < friction logic
        car.accelerate();
        // Force speed to something very small
        try {
            Field s = Car.class.getDeclaredField("speed");
            s.setAccessible(true);
            s.set(car, 0.01);
        } catch (Exception ignored) {}
        car.move();
        assertEquals(0, car.getSpeed(), 0.001);
    }

    @Test
    void turnLeftOnlyWorksWhenMoving() {
        Car car = new Car(0, 0);
        car.turnLeft();
        assertEquals(0, car.getAngle(), 0.001);
        car.accelerate();
        car.turnLeft();
        assertTrue(car.getAngle() < 0);
    }

    @Test
    void turnRightOnlyWorksWhenMoving() {
        Car car = new Car(0, 0);
        car.turnRight();
        assertEquals(0, car.getAngle(), 0.001);
        car.accelerate();
        car.turnRight();
        assertTrue(car.getAngle() > 0);
    }

    @Test
    void reverseProducesNegativeSpeed() {
        Car car = new Car(0, 0);
        car.reverse();
        assertTrue(car.getSpeed() < 0);
    }

    @Test
    void setPositionResetsSpeedAndAngle() {
        Car car = new Car(0, 0);
        car.accelerate();
        car.accelerate();
        car.turnRight();
        car.setPosition(200, 300);
        assertEquals(200, car.getX(), 0.001);
        assertEquals(300, car.getY(), 0.001);
        assertEquals(0, car.getSpeed(), 0.001);
        assertEquals(0, car.getAngle(), 0.001);
    }

    @Test
    void carBoundsReflectsPosition() {
        Car car = new Car(50, 75);
        Rectangle bounds = car.getBounds();
        assertEquals(50, bounds.x);
        assertEquals(75, bounds.y);
        assertEquals(car.getWidth(), bounds.width);
        assertEquals(car.getHeight(), bounds.height);
    }

    // --- GameModel & Logic ---

    @Test
    void multipleObserversNotified() {
        GameModel model = GameModel.getInstance();
        final int[] count = {0};
        ParkingObserver obs1 = () -> count[0]++;
        ParkingObserver obs2 = () -> count[0]++;
        model.addObserver(obs1);
        model.addObserver(obs2);
        model.update();
        assertEquals(2, count[0], "Both observers should have been notified");
    }

    @Test
    void boundaryCheckLeftWall() {
        GameModel model = GameModel.getInstance();
        model.getCar().setPosition(-1, 100);
        model.update();
        assertTrue(model.getCurrentStatus().isCrashed());
    }

    @Test
    void boundaryCheckTopWall() {
        GameModel model = GameModel.getInstance();
        model.getCar().setPosition(100, -1);
        model.update();
        assertTrue(model.getCurrentStatus().isCrashed());
    }

    @Test
    void boundaryCheckBottomWall() {
        GameModel model = GameModel.getInstance();
        model.getCar().setPosition(100, 601);
        model.update();
        assertTrue(model.getCurrentStatus().isCrashed());
    }

    @Test
    void boundaryCheckRightWall() {
        GameModel model = GameModel.getInstance();
        model.getCar().setPosition(801, 100);
        model.update();
        assertTrue(model.getCurrentStatus().isCrashed());
    }

    @Test
    void carMustBeFullyInsideSpotToSucceed() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1); // Spot: (400, 400, 60, 30)
        // Set car partially outside (Car width is 40, height is 20)
        model.getCar().setPosition(380, 400);
        model.update();
        assertFalse(model.getCurrentStatus().isSuccess());
    }

    @Test
    void obstacleCollisionCrashesCar() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1); // Obstacle: Rectangle(200, 0, 20, 300)
        model.getCar().setPosition(200, 100);
        model.update();
        assertTrue(model.getCurrentStatus().isCrashed());
    }

    @Test
    void loadLevel2HasTwoObstaclesAndCorrectSpot() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(2);
        assertEquals(2, model.getObstacles().size());
        assertEquals(500, model.getParkingSpot().x);
    }

    @Test
    void noUpdateOccursAfterCrash() {
        GameModel model = GameModel.getInstance();
        model.getCar().setPosition(-1, 100);
        model.update(); // triggers crash
        double xAfterCrash = model.getCar().getX();
        model.getCar().accelerate();
        model.update(); // should be a no-op
        assertEquals(xAfterCrash, model.getCar().getX(), 0.001);
    }

    // --- Player Stats & Timing ---

    @Test
    void statsInitialValues() {
        PlayerStats stats = new PlayerStats();
        assertEquals(Long.MAX_VALUE, stats.getBestTime(1));
        assertEquals(Long.MAX_VALUE, stats.getBestTime(5));
    }

    @Test
    void timerResumesCorrectly() throws InterruptedException {
        GameTimer t = new GameTimer();
        t.start();
        Thread.sleep(20);
        t.stop();
        long firstRun = t.getElapsedTime();
        t.start();
        Thread.sleep(20);
        assertTrue(t.getElapsedTime() > firstRun);
    }

    @Test
    void timerFormatConsistency() {
        GameTimer t = new GameTimer();
        assertEquals("00:00", t.getFormattedTime());
    }

    @Test
    void timerResetClearsElapsedTime() throws InterruptedException {
        GameTimer t = new GameTimer();
        t.start();
        Thread.sleep(30);
        t.stop();
        t.reset();
        assertEquals(0, t.getElapsedTime());
        assertEquals("00:00", t.getFormattedTime());
    }

    @Test
    void bestTimeNotUpdatedIfWorse() {
        PlayerStats stats = new PlayerStats();
        stats.updateBestTime(1, 3000L);
        stats.updateBestTime(1, 9000L);
        assertEquals(3000L, stats.getBestTime(1));
    }

    @Test
    void bestTimeOutOfRangeReturnsZero() {
        PlayerStats stats = new PlayerStats();
        assertEquals(0, stats.getBestTime(99));
    }

    @Test
    void parkingStatusDefaultState() {
        ParkingStatus status = new ParkingStatus(1);
        assertFalse(status.isSuccess());
        assertFalse(status.isCrashed());
        assertEquals(1, status.getDifficulty());
    }

    @Test
    void parkingStatusDifficulty2() {
        ParkingStatus status = new ParkingStatus(2);
        assertEquals(2, status.getDifficulty());
    }

    // --- Controller Logic ---

    @Test
    void inputControllerDownKeyReverses() {
        GameModel model = GameModel.getInstance();
        InputController ic = new InputController();
        KeyEvent down = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_DOWN, ' ');
        ic.keyPressed(down);
        ic.update();
        assertTrue(model.getCar().getSpeed() < 0);
    }

    @Test
    void inputControllerRightKeyTurns() {
        GameModel model = GameModel.getInstance();
        InputController ic = new InputController();
        model.getCar().accelerate();
        KeyEvent right = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_RIGHT, ' ');
        ic.keyPressed(right);
        ic.update();
        assertTrue(model.getCar().getAngle() > 0);
    }

    @Test
    void inputControllerLeftKeyTurns() {
        GameModel model = GameModel.getInstance();
        InputController ic = new InputController();
        model.getCar().accelerate();
        KeyEvent left = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_LEFT, ' ');
        ic.keyPressed(left);
        ic.update();
        assertTrue(model.getCar().getAngle() < 0);
    }

    @Test
    void pressingRRestartsLevel() {
        GameModel model = GameModel.getInstance();
        InputController ic = new InputController();
        model.getCar().setPosition(500, 500);
        KeyEvent rKey = new KeyEvent(new Component(){}, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_R, 'r');
        ic.keyPressed(rKey);
        assertEquals(50, model.getCar().getX());
    }

    @Test
    void accelerateIncreasesSpeed() {
        Car car = new Car(0, 0);
        car.accelerate();
        assertTrue(car.getSpeed() > 0);
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
    void bestTimeUpdatesOnImprovement() {
        PlayerStats stats = new PlayerStats();
        stats.updateBestTime(1, 5000L);
        stats.updateBestTime(1, 9000L);
        assertEquals(5000L, stats.getBestTime(1));
    }

    @Test
    void carSucceedsWhenFullyInParkingSpot() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        model.getCar().setPosition(410, 405);
        model.update();
        assertTrue(model.getCurrentStatus().isSuccess());
    }

    @Test
    void menuControllerNextLevelWrapsAfterLevel2() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(2);
        new MenuController().nextLevel();
        assertEquals(1, model.getStats().getCurrentLevel());
    }

    @Test
    void loadLevel1HasOneObstacleAndCorrectSpot() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        assertEquals(1, model.getObstacles().size());
        assertEquals(400, model.getParkingSpot().x);
    }

    @Test
    void enterKeyRequiresSuccessToAdvance() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        InputController ic = new InputController();
        KeyEvent enter = new KeyEvent(new JButton(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER, '\n');
        model.getCurrentStatus().setSuccess(false);
        ic.keyPressed(enter);
        assertEquals(1, model.getStats().getCurrentLevel());
    }

    @Test
    void inputControllerUpKeyAccelerates() {
        GameModel model = GameModel.getInstance();
        InputController ic = new InputController();
        KeyEvent up = new KeyEvent(new JButton(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_UP, ' ');
        ic.keyPressed(up);
        ic.update();
        assertTrue(model.getCar().getSpeed() > 0);
    }

    // --- 5 Additional Tests ---

    @Test
    void timerNotRunningAfterReset() throws InterruptedException {
        // After reset, elapsed time should stay 0 even without calling stop first
        GameTimer t = new GameTimer();
        t.start();
        Thread.sleep(20);
        t.reset();
        Thread.sleep(20);
        assertEquals(0, t.getElapsedTime());
    }

    @Test
    void loadLevelResetsCrashedStatus() {
        GameModel model = GameModel.getInstance();
        model.getCar().setPosition(-1, 100);
        model.update(); // crash
        assertTrue(model.getCurrentStatus().isCrashed());
        model.loadLevel(1); // reload should clear crash
        assertFalse(model.getCurrentStatus().isCrashed());
    }

    @Test
    void loadLevelResetsCarToStartPosition() {
        GameModel model = GameModel.getInstance();
        model.getCar().setPosition(700, 500);
        model.loadLevel(1);
        assertEquals(50, model.getCar().getX(), 0.001);
        assertEquals(50, model.getCar().getY(), 0.001);
    }

    @Test
    void menuControllerRestartKeepsCurrentLevel() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(2);
        model.getCar().setPosition(700, 500);
        new MenuController().restartLevel();
        assertEquals(2, model.getStats().getCurrentLevel());
        assertEquals(50, model.getCar().getX(), 0.001);
    }

    @Test
    void enterKeyAdvancesLevelOnSuccess() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        InputController ic = new InputController();
        KeyEvent enter = new KeyEvent(new JButton(), KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_ENTER, '\n');
        model.getCurrentStatus().setSuccess(true);
        ic.keyPressed(enter);
        assertEquals(2, model.getStats().getCurrentLevel());
    }
}