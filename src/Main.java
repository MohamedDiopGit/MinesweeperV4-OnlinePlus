import javax.swing.JFrame;
import java.awt.*;

/**
 * {@code Main} application : Minesweeper program.
 */
public class Main extends JFrame {
    /**
     * Main GUI for the game : Minesweeper.
     */
    private final GUI gui;
    /**
     * Field to start with in the game.
     */
    private Field field;

    Main() {

        setLayout(new FlowLayout());
        setTitle("Minesweeper");

        // Load the game level
        loadGameLevel();

        // initialisation of the field
        this.field.initField();

        gui = new GUI(this);
        add(gui);
        // setContentPane(gui); // Set the center Panel for the frame

        pack();
        // setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Close correctly the frame
    }

    /**
     * Runs the minesweeper program.
     * 
     * @param args : optional arguments to pass to the program.
     */
    public static void main(String[] args) {
        new Main();
    }

    /**
     * Loads the saved level's configuration from "LevelRegistred.dat"
     * 
     * @see LevelsFileReader
     */
    public void loadGameLevel() {

        try {
            LevelsFileReader fileReader = new LevelsFileReader();

            // Waiting for the reader thread to finish loading the level mmode
            fileReader.geThread().join();

            // Configure the field with the level mode.
            field = new Field(fileReader.getLevelFromFile());

        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failed to load last save... EASY_Mode selected.");
        }
    }

    // GETTERS SECTION

    /**
     * Returns the current field that {@code Main} is running.
     * 
     * @return {@code Field}
     */
    public Field getField() { // Getter of the field
        return this.field;
    }

    public GUI getGui() {
        return this.gui;
    }
}