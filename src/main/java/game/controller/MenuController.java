package game.controller;

import game.model.GameModel;

/**
 * Manages level selection and game browsing navigation.
 */
public class MenuController {
    /**
     * Resets the current level.
     */
    public void restartLevel() {
        GameModel model = GameModel.getInstance();
        model.loadLevel(model.getStats().getCurrentLevel());
    }

    /**
     * Advances to the next level.
     */
    public void nextLevel() {
        GameModel model = GameModel.getInstance();
        int next = model.getStats().getCurrentLevel() + 1;
        if (next > 2) next = 1; // Loop back or end game
        model.loadLevel(next);
    }
}
