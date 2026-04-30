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

    /**
     * Speeds up the car.
     */
    public void accelerate() {
        speed += acceleration;
    }

    /**
     * Moves the car in reverse.
     */
    public void reverse() {
        speed -= acceleration;
    }

    /**
     * Turns the car to the left.
     */
    public void turnLeft() {
        if (speed != 0) {
            angle -= turnSpeed;
        }
    }

    /**
     * Turns the car to the right.
     */
    public void turnRight() {
        if (speed != 0) {
            angle += turnSpeed;
        }
    }

    /**
     * Gets the rectangular area covered by the car.
     * @return The car's boundaries.
     */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // Getters and Setters
    /** @return current x position */
    public double getX() { return x; }
    /** @return current y position */
    public double getY() { return y; }
    /** @return current angle in degrees */
    public double getAngle() { return angle; }
    /** @return current speed */
    public double getSpeed() { return speed; }
    /** @return width of the car */
    public int getWidth() { return width; }
    /** @return height of the car */
    public int getHeight() { return height; }

    /**
     * Sets the car's position and resets its state.
     * @param x New x position.
     * @param y New y position.
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        this.speed = 0;
        this.angle = 0;
    }
}
