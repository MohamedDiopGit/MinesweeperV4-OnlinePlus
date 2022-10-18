import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.List;

/**
 * {@code GUI} : Graphic User Interface class, extends {@code JPanel}.
 * The main component that runs the Graphic interface,
 * and manages the front and back-end processes to call entities and specific
 * functions
 * in order to display the information.
 * It allows to display the grid, the menubar on the main frame and the pop-ups
 * to give and collect data correctly.
 */
public class GUI extends JPanel implements Runnable {

    /**
     * Field to be process for the grid and display in the GUI.
     */
    private Field field;

    /**
     * imported Main from the "main.java".
     */
    private Main main;

    /**
     * Timer for the session which update every second the {@code timeSession}.
     */
    private Timer timer;

    /**
     * Seconds elapsed since the beginning.
     */
    private int seconds = 0;
    /**
     * scoreLabel of the current game session.
     */
    private JLabel scoreLabel = new JLabel();

    private int scoreFlag;
    private int openedCases = 0;
    /**
     * Time session (elapsed) information to display
     */
    private JLabel timeSession = new JLabel();

    /**
     * restart button's text
     */
    private ImageIcon restartImg = new ImageIcon("restart.png");
    // private ImageIcon restartPressedImg = new ImageIcon("restartPressed.png");
    private JButton restart = new JButton(
            new ImageIcon(restartImg.getImage().getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH)));
    // private JButton restart = new JButton(new ImageIcon("restart.png"));
    private JMenuItem saveGame = new JMenuItem("Save");
    /**
     * Pane in the center of the screen that displays the grid
     */
    private JPanel panelCenter = new JPanel();
    /**
     * Current game level of the session
     * 
     * @see Levels
     */
    private Levels levelGame;
    private JMenuBar menuBar;

    /**
     * Game mode's text
     */
    private JLabel levelMode = new JLabel();
    private JPanel panelNorth = new JPanel(new FlowLayout());
    private JPanel panelNorthCenter = new JPanel(new FlowLayout());
    private List<Case> cases = new ArrayList<Case>();
    private Client client;
    private boolean modeOnline = false;
    private Thread clientSession;
    private ChatClient chatClient;
    private Server server;

    /**
     * Label for connected clients in the menu
     */
    private JMenu connectedClients = new JMenu("Connected clients");

    /**
     * Constructor for the GUI, which starts the game.
     * 
     * @param {@code Main} : the main component that contains a {@code Field}.
     * @see #startNewGame()
     */
    GUI(Main main) {
        this.main = main;
        this.field = main.getField();

        setLayout(new BorderLayout());
        panelNorth.setBackground(Color.lightGray);
        setBorder(BorderFactory.createRaisedBevelBorder());
        panelNorth.setBorder(BorderFactory.createLoweredBevelBorder());

        startNewGame();

    }

    /**
     * Global starter method which starts and initializes the game.
     * {@code Field.initField()} methods and catch the game level in the GUI.
     */
    public void startNewGame() {
        saveGame.setText("Not saved");
        saveGame.setForeground(Color.RED);
        this.levelGame = field.getLevel();
        levelMode.setText(String.valueOf(levelGame));
        this.displayMenu();
        this.restartButton();
        this.reinitialize();
    }

    /**
     * Displays the menu bar for choosing between multiple difficulties
     * and display their informations.
     * It adds the {@code ActionListener} on the difficulty options,
     * in order to {@code startNewGame()} with parameters depending on level game
     * mode.
     * 
     * @see #startNewGame()
     */
    public void displayMenu() {
        remove(panelNorth);
        add(panelNorth, BorderLayout.NORTH);
        panelNorth.removeAll();
        panelNorth.setLayout(new BorderLayout());

        menuBar = new JMenuBar();
        JMenuItem modeMenu = new JMenu("Mode");
        JMenuItem easyMode = new JMenuItem("EASY");
        JMenuItem mediumMode = new JMenuItem("MEDIUM");
        JMenuItem hardMode = new JMenuItem("HARD");
        JMenuItem customMode = new JMenuItem("CUSTOM");
        JMenu option = new JMenu("Options");

        option.add(saveGame);

        // SERVER OPTIONS MENU
        JMenu infoServer = new JMenu("Server");
        JMenuItem connectionToServer = new JMenuItem("Connection to server");
        JMenuItem disconnectionFromServer = new JMenuItem("Disconnect from server");

        infoServer.add(connectionToServer);
        infoServer.add(disconnectionFromServer);

        levelMode.setText(String.valueOf(levelGame));

        // MODE MENU
        modeMenu.add(easyMode);
        modeMenu.add(mediumMode);
        modeMenu.add(hardMode);
        modeMenu.add(customMode);

        // MENU BAR
        menuBar.add(option);
        menuBar.add(modeMenu);
        menuBar.add(levelMode);
        menuBar.add(infoServer);
        menuBar.add(connectedClients);
        menuBar.setBorder(BorderFactory.createRaisedBevelBorder());

        // CUSTOM APPEARANCE FOR SIDE BLOCKS
        timeSession.setForeground(Color.RED);
        scoreLabel.setForeground(Color.RED);

        scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 30));
        timeSession.setFont(new Font("Monospaced", Font.BOLD, 30));

        // PANEL AND RESTART BUTTON CUSTOMIZATION
        JPanel panelNorthWest = new JPanel(new FlowLayout());
        JPanel panelNorthEast = new JPanel(new FlowLayout());
        panelNorthCenter = new JPanel(new FlowLayout());

        panelNorthWest.add(scoreLabel);
        panelNorthEast.add(timeSession);

        panelNorthWest.setBackground(Color.black);
        panelNorthEast.setBackground(Color.black);

        restart.setBackground(Color.lightGray);
        restart.setBorder(BorderFactory.createEmptyBorder());
        panelNorthCenter.add(restart);
        panelNorthCenter.setBackground(Color.lightGray);

        panelNorth.add(panelNorthEast, BorderLayout.EAST);
        panelNorth.add(panelNorthCenter, BorderLayout.CENTER);
        panelNorth.add(panelNorthWest, BorderLayout.WEST);

        // Add menu options
        saveGame.addActionListener(evt -> saveGameLevel());

        // Add connection to server
        connectionToServer.addActionListener(evt -> modeOnline());
        disconnectionFromServer.addActionListener(evt -> modeOffline());

        // Add different mode actions in the menu
        easyMode.addActionListener(evt -> selectorLevelGame(Levels.EASY));
        mediumMode.addActionListener(evt -> selectorLevelGame(Levels.MEDIUM));
        hardMode.addActionListener(evt -> selectorLevelGame(Levels.HARD));
        customMode.addActionListener(evt -> selectorLevelGame(Levels.CUSTOM));

        // Addd the menu bar to the main frame.
        main.setJMenuBar(menuBar);

    }

    /**
     * Activates the mode online by starting a client session
     */
    public void modeOnline() {
        if (!modeOnline) {
            clientSession = new Thread(this);
            clientSession.start();
        }
    }

    /**
     * Desactivate the mode online by closing the connection and removing the chat
     */
    public void modeOffline() {
        if (modeOnline) {

            modeOnline = false;
            if (chatClient != null) {
                main.remove(chatClient);
            }
            setTitleFrame("Minesweeper");
            main.loadGameLevel();
            main.pack();
            menuBar.remove(connectedClients);
            startNewGame();

        }
    }

    /**
     * Set the title frame for this GUI on main
     * 
     * @param titleFrame
     */
    public void setTitleFrame(String titleFrame) {
        main.setTitle(titleFrame);
    }

    /**
     * Allow to create a specific thread for running the client
     */
    @Override
    public void run() {
        modeOnline = true;
        client = new Client(this);
    }

    /**
     * Selects and sets a mode level for the game
     * 
     * @param level
     */
    public void selectorLevelGame(Levels level) {
        modeOnline = false;
        field = new Field(level);
        levelMode.setText(String.valueOf(level));
        saveGame.setForeground(Color.RED);
        startNewGame();
    }

    /**
     * Initialization method for the grid cases (main grid).
     */
    public void initializationFieldPanel() { // Initialization of boxes with different values for a
        // certain area / allow to place flags on mines

        remove(panelCenter); // initialization of the panel
        panelCenter = new JPanel();
        add(panelCenter, BorderLayout.CENTER);
        panelCenter.setBorder(BorderFactory.createLoweredBevelBorder());
        int dimParam = this.field.getDim(); // Get the dimensions of the field
        panelCenter.setLayout(new GridLayout(dimParam, dimParam));

        cases.clear();
        int indexCase = 0;
        // Loop on the entire field elements
        for (int x = 0; x < dimParam; x++) {
            for (int y = 0; y < dimParam; y++) { // loop on the matrix to display all objects

                Case caseToAdd = new Case(indexCase, x, y, this, modeOnline);
                cases.add(caseToAdd);
                panelCenter.add(cases.get(cases.size() - 1));

                indexCase++;
            }
        }
        main.pack();
    }

    /**
     * Activates the restart button by adding an {@code ActionListener} event
     * on the restart button. It will call the {@code reinitialize()} method.
     * 
     * @see #reinitialize()
     */
    public void restartButton() { // Restart a game

        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNewGame();
            }
        });

    }

    /**
     * Generates a new field, and restarts the timer, and the scoreLabel of the
     * current
     * game.
     * It also calls the {@code initializationFieldPanel()} method to clear the
     * field/grid.
     * 
     * @see #initializationFieldPanel()
     */
    public void reinitialize() {
        scoreFlag = field.getNumberOfMines();
        scoreLabel.setText(String.valueOf(scoreFlag));
        timeInit();

        field.initField();
        this.initializationFieldPanel();

        if (server != null) { // Restart all field on client via server if he is on
            server.sendToAllField();
            main.pack();
        }
    }

    /**
     * Processes the time elapsed since the beginning of the start of a game
     * session.
     * It also checks if the time session has outdated the time limit, if so,
     * it will reinitialize the game after showing a popup (Game over) to the user.
     * 
     * @see #reinitialize()
     */
    public void timeInit() { //
        seconds = 0;
        if (timer != null) {
            timer.stop();
        }
        timeSession.setText(String.valueOf(seconds));
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                timeSession.setText(String.valueOf(seconds));
            }
        });
        timer.start();
    }

    /**
     * Saves the game level in a local file "LevelRegistred.dat".
     */
    public void saveGameLevel() {
        new LevelsFileWriter(this.levelGame);
        saveGame.setText("Saved");
        saveGame.setForeground(new Color(57, 163, 18));
    }

    public Field getFieldFromGUI() {
        return field;
    }

    /**
     * Increments the flag counter (triggered by a flag removal).
     */
    public void upScoreFlag() {
        scoreFlag++;
        scoreLabel.setText(String.valueOf(scoreFlag));
    }

    /**
     * Decrements the flag counter (triggered by a flag deposal).
     */
    public void downScoreFlag() {
        scoreFlag--;
        scoreLabel.setText(String.valueOf(scoreFlag));
    }

    /**
     * Triggers the game over and restarts the game
     */
    public void gameOver() {
        JOptionPane.showMessageDialog(this, "Mine clicked on.",
                "Game over", JOptionPane.INFORMATION_MESSAGE);
        if (modeOnline && server == null) { // only client send this
            client.sendMessageToServer("-1:resetField");
        } else {
            startNewGame();
        }
    }

    /**
     * Checks if the game is winned
     */
    public void checkIfWin() {
        int totalCases = field.getDim() * field.getDim();
        if (((totalCases - openedCases) == field.getNumberOfMines()) && scoreFlag == 0) {
            JOptionPane.showMessageDialog(this, "You won.",
                    "Game win", JOptionPane.INFORMATION_MESSAGE);
            openedCases = 0;
            if (modeOnline && server == null) {
                client.sendMessageToServer("-1:resetField");
            } else {
                startNewGame();
            }
        }
    }

    /**
     * Increments the number of cases opened.
     */
    public void incrementCasesOpened() {
        openedCases++;
    }

    /**
     * Notifies the server that a click has occurred on a case which further
     * triggers {@code updateCase}.
     * 
     * @param indexCase
     * @param notification
     * @see #updateCase(int, String)
     */
    public void notifyClickOnCase(int indexCase, String notification) {
        client.clickOnCaseToServer(indexCase, notification);
    }

    /**
     * Trigger a click (referenced by index) on a case via left or right Click from
     * the server.
     * 
     * @param indexCaseReceived
     * @param typeClicked
     */
    public void updateCase(int indexCaseReceived, String typeClicked) {
        System.out.println("Received: " + indexCaseReceived + " " + typeClicked);
        if (typeClicked.equals("rightClick")) {
            cases.get(indexCaseReceived).rightClick();
        } else if (typeClicked.equals("leftClick")) {
            cases.get(indexCaseReceived).leftClick();
        }
    }

    // GETTERS AND SETTERS SECTION

    public Field getField() {
        return this.field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setFieldXY(int x, int y, boolean valueBool) {
        field.setFieldGrid(x, y, valueBool);
    }

    public void setLevelMode(String levelModeSelected) {
        levelMode.setText(levelModeSelected);
    }

    public String getLevelMode() {
        return levelMode.getText();
    }

    public void setChatClient(String pseudo, DataOutputStream out) {
        chatClient = new ChatClient(pseudo);
        chatClient.setOutputStream(out);
        main.add(chatClient);
        main.pack();
    }

    public ChatClient getChatClient() {
        return chatClient;
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * Check if the server is running this GUI
     */
    public void setServer(Server server) {
        this.server = server;
    }

    /**
     * Add the connected clients on the menuBar
     * 
     * @param connectedClientsUpdate
     */
    public void setConnectedClientsOnMenu(JMenu connectedClientsUpdate) {
        this.connectedClients = connectedClientsUpdate;
    }

    public JMenu getConnectedClients() {
        return connectedClients;
    }

    public Client getClient() {
        return client;
    }

    public Server getServer() {
        return server;
    }
}
