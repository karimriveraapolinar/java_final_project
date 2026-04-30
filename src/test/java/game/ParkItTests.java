package game;

import game.controller.InputController;
import game.controller.MenuController;
import game.model.*;
import org.junit.jupiter.api.BeforeEach;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParkItTests {

    @BeforeEach
    void resetSingleton() throws Exception {
        Field f = GameModel.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    // --- Car ---

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

    // --- ParkingStatus ---

    @Test
    void parkingStatusDefaultsFalse() {
        ParkingStatus s = new ParkingStatus(1);
        assertFalse(s.isSuccess());
        assertFalse(s.isCrashed());
        assertEquals(1, s.getDifficulty());
    }

    @Test
    void parkingStatusSetters() {
        ParkingStatus s = new ParkingStatus(2);
        s.setSuccess(true);
        s.setCrashed(true);
        assertTrue(s.isSuccess());
        assertTrue(s.isCrashed());
    }

    // --- PlayerStats ---

    @Test
    void bestTimeUpdatesOnImprovement() {
        PlayerStats stats = new PlayerStats();
        stats.updateBestTime(1, 5000L);
        stats.updateBestTime(1, 9000L); // worse — should not replace
        assertEquals(5000L, stats.getBestTime(1));
    }

    @Test
    void bestTimeIgnoresInvalidLevel() {
        PlayerStats stats = new PlayerStats();
        assertEquals(0, stats.getBestTime(99));
        stats.updateBestTime(0, 1000L); // should not throw
    }

    // --- GameModel ---

    @Test
    void singletonReturnsSameInstance() {
        assertSame(GameModel.getInstance(), GameModel.getInstance());
    }

    @Test
    void loadLevel1HasOneObstacleAndCorrectSpot() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        assertEquals(1, model.getObstacles().size());
        assertEquals(400, model.getParkingSpot().x);
    }

    @Test
    void loadLevel2HasTwoObstacles() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(2);
        assertEquals(2, model.getObstacles().size());
    }

    @Test
    void updateDoesNothingWhenCrashed() {
        GameModel model = GameModel.getInstance();
        model.getCar().setPosition(100, 100);
        model.getCurrentStatus().setCrashed(true);
        model.update();
        assertEquals(100, model.getCar().getX(), 0.001);
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
    void carSucceedsWhenFullyInParkingSpot() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1); // spot at (400,400,60,30)
        model.getCar().setPosition(410, 405);
        model.update();
        assertTrue(model.getCurrentStatus().isSuccess());
    }

    @Test
    void carCrashesOnBoundaryExit() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        model.getCar().setPosition(810, 100);
        model.update();
        assertTrue(model.getCurrentStatus().isCrashed());
    }

    // --- Controllers ---

    @Test
    void menuControllerNextLevelWrapsAfterLevel2() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(2);
        new MenuController().nextLevel();
        assertEquals(1, model.getStats().getCurrentLevel());
    }

    @Test
    void inputControllerUpKeyAcceleratesCar() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(1);
        InputController ic = new InputController();
        KeyEvent up = new KeyEvent(new java.awt.Component(){},
                KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
                KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        ic.keyPressed(up);
        ic.update();
        assertTrue(model.getCar().getSpeed() > 0);
    }
}