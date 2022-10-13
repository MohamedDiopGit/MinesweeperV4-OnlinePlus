import java.util.Random;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * {@code Field} of the minesweeper.
 */
public class Field {

    /* Field parameters */
    // private final static int NBMINES = 3;

    /**
     * Number of mines to place on the minefield.
     */
    private final int nbMinesToPlace;
    private final static int nbMines[] = { 3, 7, 10, 15 }; // Possible mines of the minefield depending on level
                                                           // difficulty
    private final static int dimParam[] = { 5, 7, 9, 12 }; // Possible dimensions of the minefield depending on level
                                                           // difficulty

    /**
     * Edge dimension of the square minefield.
     */
    private final int dimParameter;

    /**
     * Minefield that contains every mines and their position.
     */
    private boolean[][] fieldGrid;

    /**
     * Number of mines placed during the minefield calculation in initField() method
     * 
     * @see #initField()
     */
    private int nbMinesPlaced = 0;

    /**
     * Game level mode selected.
     */
    private Levels levelGame;

    /**
     * Default constructor for the field.
     */
    Field() {
        this.dimParameter = 4;
        this.nbMinesToPlace = 3;
        this.fieldGrid = new boolean[dimParameter][dimParameter];
    }

    /**
     * Constructor for the field with parameter values
     * 
     * @param nbMinesPlaced
     * @param dimParameter
     */
    Field(int nbMinesPlaced, int dimParameter) {
        this.dimParameter = dimParameter;
        this.nbMinesToPlace = nbMinesPlaced;
        this.fieldGrid = new boolean[dimParameter][dimParameter];
    }

    /**
     * Constructor for field with level in entry.
     * 
     * @param {@code Levels} : Level mode selected.
     */
    Field(Levels level) {

        this.levelGame = level;
        if (level.ordinal() == 3) { // CUSTOM level = 3

            /*
             * TERMINAL ENTRIES MODE
             * The user select the number of mines to place:
             * System.out.print("Select the number of mines: ");
             * int nbMinesToSelect = setParameter();
             * 
             * // The user select the dimension of the minefield:
             * System.out.print("Select the X dimension of the square field: ");
             * int dimParamToSelect = setParameter();
             */

            /* POPUP ENTRIES MODE */
            JTextField dimParamToSelect = new JTextField();
            JTextField nbMinesToSelect = new JTextField();
            Object[] message = {
                    "Dimensions:", dimParamToSelect,
                    "Number of mines:", nbMinesToSelect
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Set Game Parameters",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) { // Check if OK_OPTION is ok
                nbMinesToPlace = Integer.valueOf(nbMinesToSelect.getText());
                dimParameter = Integer.valueOf(dimParamToSelect.getText());
            } else { // Catch the error case
                level = Levels.EASY;
                JOptionPane.showMessageDialog(null,
                        "Error in setting parameters, please try again. EASY_MODE selected.",
                        "ERROR", JOptionPane.WARNING_MESSAGE);
                nbMinesToPlace = nbMines[level.ordinal()]; // EASY = 0 / MEDIUM = 1 / HARD = 2
                dimParameter = dimParam[level.ordinal()];
            }

        } else {
            nbMinesToPlace = nbMines[level.ordinal()]; // EASY = 0 / MEDIUM = 1 / HARD = 2
            dimParameter = dimParam[level.ordinal()];
        }

    }

    /**
     * Returns the current level of the minefield.
     * 
     * @return {@code Levels} : enum type
     */
    public Levels getLevel() {
        return this.levelGame;
    }

    /**
     * Sets the current level of the minefield.
     * 
     * @param level
     */
    public void setLevel(Levels level) {
        this.levelGame = level;
    }

    /**
     * Returns a parameter typed in the terminal.
     * 
     * @return {@code int}
     */
    public int setParameter() {
        try (Scanner sc = new Scanner(System.in)) {
            int parameter = sc.nextInt();
            return parameter;
        }
    }

    /**
     * Initialization method which fills the field matrix or grid and place the
     * mines on it.
     */
    public void initField() { // Place the mine in the field
        this.fieldGrid = new boolean[dimParameter][dimParameter];
        Random alea = new Random();
        while (nbMinesPlaced < nbMinesToPlace) { // Check if there is enough available places to place a mine
            int x = alea.nextInt(dimParameter); // Random generation of place (x,y) on the field
            int y = alea.nextInt(dimParameter);
            if (!this.fieldGrid[x][y]) { // Check if the position is not currently occupied
                this.fieldGrid[x][y] = true;
                nbMinesPlaced++; // increments the number of placed mines
            }
        }
        nbMinesPlaced = 0; // IMPORTANT : Re-initialization for further use in the GUI when restart for
                           // example
    }

    /**
     * This method displays the field on the terminal.
     */
    public void display() { // Display the entire field of mine
        for (int x = 0; x < fieldGrid.length; x++) {
            for (int y = 0; y < fieldGrid[0].length; y++) { // For loop on the matrix to display all objects
                if (fieldGrid[x][y])
                    System.out.print('x'); // Display a mine if true
                else
                    System.out.print(computeNbMines(x, y)); // Display an empty case
            }
            System.out.println(); // Skips a line
        }
    }

    /**
     * Calculates the number of mine around a case of the field
     * 
     * @param x : x coordinate
     * @param y : y coordinate
     * @return {@code int} : number of mine around the case
     */
    int computeNbMines(int x, int y) {
        int nb = 0;

        // Inferior limit for edge cases
        int borneInfX = x == 0 ? 0 : x - 1;
        int borneInfY = y == 0 ? 0 : y - 1;

        // Superior limit for edge cases
        int borneSupX = x == fieldGrid.length - 1 ? fieldGrid.length - 1 : x + 1;
        int borneSupY = y == fieldGrid[0].length - 1 ? fieldGrid[0].length - 1 : y + 1;

        for (int i = borneInfX; i <= borneSupX; i++) {
            for (int j = borneInfY; j <= borneSupY; j++) {
                if (fieldGrid[i][j]) {
                    nb++;
                }
            }
        }
        return nb;
    }

    /**
     * Returns the dimension parameter of the grid
     * 
     * @return {@code int} : dimension of the grid / field
     */
    public int getDim() {
        return this.dimParameter;
    }

    /**
     * Returns the number of mines parameter from the grid
     * 
     * @return {@code int} : total of mines on the grid / field
     */
    public int getNumberOfMines() {
        return this.nbMinesToPlace;
    }

    /**
     * Returns the {@code String} value of
     * the case whether the {@code computeNbMines} one or "x" / "0" for a mine or
     * not.
     * 
     * @param x            : x coordinate of the element
     * @param y            : y coordinate of the same element
     * @param computeOrNot : {@code boolean} indicating whether the
     *                     {@code computeNbMines} (1) one or "x" / "0" for a mine or
     *                     not (0)
     * @return {@code String}
     * @see #computeNbMines(int, int)
     */
    public String getElementFromXY(int x, int y, boolean computeOrNot) { // Return the current state of a case on the
                                                                         // grid
        if (this.fieldGrid[x][y]) {
            return "x";
        } else {
            if (computeOrNot) {
                return String.valueOf(computeNbMines(x, y));
            }
            return "0";
        }

    }

    public void setFieldGrid(int x, int y, boolean value){
        this.fieldGrid[x][y] = value;
    }

}