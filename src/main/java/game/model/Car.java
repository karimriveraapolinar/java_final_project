package game.model;

import java.awt.Rectangle;

/**
 * Represents the car's physical properties and state.
 * Stats: speed, handling, size.
 */
public class Car {
    private double x, y;
    private double angle;
    private double speed;
    private final double acceleration = 0.1;
    private final double friction = 0.05;
    private final double turnSpeed = 3.0;
    
    private final int width = 40;
    private final int height = 20;

    /**
     * Constructs a car at a specific position.
     * @param x Initial x coordinate
     * @param y Initial y coordinate
     */
    public Car(double x, double y) {
        this.x = x;
        this.y = y;
        this.speed = 0;
        this.angle = 0;
    }

    /**
     * Updates the car's position based on speed and angle.
     */
    public void move() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
        
        // Apply friction
        if (speed > 0) speed -= friction;
        else if (speed < 0) speed += friction;
        
        if (Math.abs(speed) < friction) speed = 0;
    }

    public void accelerate() {
        speed += acceleration;
    }

    public void reverse() {
        speed -= acceleration;
    }

    public void turnLeft() {
        if (speed != 0) {
            angle -= turnSpeed;
        }
    }

    public void turnRight() {
        if (speed != 0) {
            angle += turnSpeed;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // Getters and Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public double getSpeed() { return speed; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        this.speed = 0;
        this.angle = 0;
    }
}
