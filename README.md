# Park It - Java Final Project

## Project Overview
A Java-based parking simulator following the MVC (Model-View-Controller) architecture and utilizing Maven for dependency management and testing. Players must steer a car into a designated parking spot while avoiding obstacles.

## MVC Architecture
The project is organized into three primary layers:
- **Model**: `game.model` package. Manages game state (`GameModel`), car physics (`Car`), player statistics (`PlayerStats`), and timing logic (`GameTimer`).
- **View**: `game.view` package. Handles rendering of the game world (`ParkingLotView`), car (`CarView`), and UI overlays (`GameOverlay`). `UserInterface` acts as the main panel.
- **Controller**: `game.controller` package. Manages user input (`InputController`) and navigation/level logic (`MenuController`).

## Design Patterns
- **Singleton**: The `GameModel` class is a Singleton, ensuring all components access the same game state and physics engine.
- **Observer**: The `ParkingObserver` interface and `GameModel`'s notification system allow the View (`UserInterface`) to react to state changes without being tightly coupled to the logic.

## Instructions for Execution
### Running the Game
1.  **Using Maven**:
    ```bash
    mvn compile
    mvn exec:java -Dexec.mainClass="game.Main"
    ```
2.  **Manual Compilation**:
    ```bash
    javac -d bin src/main/java/game/**/*.java
    java -cp bin game.Main
    ```

### Executing the Test Suite
1.  **Using Maven**:
    ```bash
    mvn test
    ```
2.  **Using IDE**: Right-click on `src/test/java/game/model/ModelTest.java` and select "Run 'ModelTest'".

## Controls
- **Arrow Keys**: Accelerate, Reverse, and Steer.
- **'R' Key**: Restart current level.
- **'Enter' Key**: Advance to next level after successful parking.

## AI Tool Usage
AI was only used to assist in architectural setup and fixing errors that were very difficult.

## Known Issues or Limitations
- Simple rectangular collision detection is used.
- Level design is currently hardcoded for two levels.