package game.view;

import game.model.GameModel;
import game.model.ParkingObserver;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Main game panel that coordinates all view components and implements Observer.
 */
public class UserInterface extends JPanel implements ParkingObserver {
    private CarView carView;
    private ParkingLotView lotView;
    private GameOverlay overlay;

    public UserInterface() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setFocusable(true);
        this.carView = new CarView();
        this.lotView = new ParkingLotView();
        this.overlay = new GameOverlay();
        
        GameModel.getInstance().addObserver(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        lotView.draw(g);
        carView.draw(g);
        overlay.draw(g);
    }

    /**
     * Updates the screen by requesting a repaint.
     */
    @Override
    public void update() {
        repaint();
    }
}
