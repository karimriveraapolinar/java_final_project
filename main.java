import game.controller.InputController;
import game.model.GameModel;
import game.view.UserInterface;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Entry point for the "Park It" Java game.
 * Initializes MVC components and starts the game loop.
 */
public class main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Park It - Java Edition");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GameModel model = GameModel.getInstance();
        UserInterface view = new UserInterface();
        InputController controller = new InputController();
        
        frame.add(view);
        view.addKeyListener(controller);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Game Loop (approx 60 FPS)
        Timer gameLoop = new Timer(16, e -> {
            controller.update();
            model.update();
        });
        gameLoop.start();
    }
}
