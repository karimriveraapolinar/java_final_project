package game.view;

import game.model.GameModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Renders the parking lots, obstacles, and roads.
 */
public class ParkingLotView {
    public void draw(Graphics g) {
        GameModel model = GameModel.getInstance();
        
        // Draw background (road/ground)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 800, 600);
        
        // Draw parking spot
        Rectangle spot = model.getParkingSpot();
        g.setColor(Color.GREEN);
        g.drawRect(spot.x, spot.y, spot.width, spot.height);
        g.setColor(new Color(0, 255, 0, 50)); // Transparent green
        g.fillRect(spot.x, spot.y, spot.width, spot.height);
        
        // Draw obstacles (walls)
        g.setColor(Color.RED);
        for (Rectangle obs : model.getObstacles()) {
            g.fillRect(obs.x, obs.y, obs.width, obs.height);
        }
    }
}
