package game.view;

import game.model.GameModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Displays overlay elements like timer, leaderboard, and menus.
 */
public class GameOverlay {
    public void draw(Graphics g) {
        GameModel model = GameModel.getInstance();
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Draw Timer
        g.drawString("Time: " + model.getTimer().getFormattedTime(), 20, 30);
        
        // Draw Level
        g.drawString("Level: " + model.getStats().getCurrentLevel(), 20, 60);
        
        // Draw Status Messages
        if (model.getCurrentStatus().isCrashed()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("CRASHED!", 300, 300);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Press 'R' to Restart", 300, 350);
        } else if (model.getCurrentStatus().isSuccess()) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("PARKED!", 300, 300);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Press 'Enter' for Next Level", 280, 350);
        }
    }
}
