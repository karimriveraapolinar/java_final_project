package game.view;

import game.model.Car;
import game.model.GameModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * Handles the visual representation and movement animations of the car.
 */
public class CarView {
    public void draw(Graphics g) {
        Car car = GameModel.getInstance().getCar();
        Graphics2D g2d = (Graphics2D) g;
        
        // Save current transform
        AffineTransform old = g2d.getTransform();
        
        // Rotate and draw car
        g2d.translate(car.getX() + car.getWidth()/2, car.getY() + car.getHeight()/2);
        g2d.rotate(Math.toRadians(car.getAngle()));
        
        g2d.setColor(Color.BLUE);
        g2d.fillRect(-car.getWidth()/2, -car.getHeight()/2, car.getWidth(), car.getHeight());
        
        // Draw headlights to show direction
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(car.getWidth()/2 - 5, -car.getHeight()/2, 5, 5);
        g2d.fillRect(car.getWidth()/2 - 5, car.getHeight()/2 - 5, 5, 5);
        
        // Restore transform
        g2d.setTransform(old);
    }
}
