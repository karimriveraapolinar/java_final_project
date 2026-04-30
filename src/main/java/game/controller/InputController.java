package game.controller;

import game.model.GameModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles keyboard input for moving the car.
 */
public class InputController extends KeyAdapter {
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final MenuController menuController;

    public InputController() {
        this.menuController = new MenuController();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        handleActionKeys(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    private void handleActionKeys(int keyCode) {
        if (keyCode == KeyEvent.VK_R) {
            menuController.restartLevel();
        } else if (keyCode == KeyEvent.VK_ENTER) {
            if (GameModel.getInstance().getCurrentStatus().isSuccess()) {
                menuController.nextLevel();
            }
        }
    }

    /**
     * Updates the car's state based on the keys currently being pressed.
     */
    public void update() {
        GameModel model = GameModel.getInstance();
        if (model.getCurrentStatus().isCrashed() || model.getCurrentStatus().isSuccess()) {
            return;
        }

        if (pressedKeys.contains(KeyEvent.VK_UP)) {
            model.getCar().accelerate();
            model.getTimer().start();
        }
        if (pressedKeys.contains(KeyEvent.VK_DOWN)) {
            model.getCar().reverse();
            model.getTimer().start();
        }
        if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
            model.getCar().turnLeft();
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
            model.getCar().turnRight();
        }
    }
}
