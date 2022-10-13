import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * {@code LevelsFileReader} class that reads the level game mode saved in a
 * file.
 */
public class LevelsFileReader implements Runnable {

    /**
     * Thread of the class that allows read of file.
     */
    public Thread readerThread;
    /**
     * Level saved in the file.
     */
    private Levels levelToRead;

    /**
     * Level saved in the file (int value of the enum).
     */
    private int numToRead;

    /**
     * Constructor for the class.
     */
    LevelsFileReader() {
        this.readerThread = new Thread(this);
        readerThread.start();

    }

    /**
     * Method called when the thread is started.
     */
    @Override
    public synchronized void run() {
        while (readerThread != null) {
            try {
                System.out.print("Reading 'LevelRegistred.dat' : ");
                readFile();
                levelToRead = Levels.values()[numToRead];
                System.out.println(levelToRead + " mode loaded.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            readerThread = null;
        }

    }

    /**
     * Reads the contents of the file and stores it in the class.
     * 
     * @throws IOException
     */
    public synchronized void readFile() throws IOException {
        String fileName = "LevelRegistred.dat";
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String line;
        while ((line = br.readLine()) != null) {
            // process the line
            numToRead = Integer.valueOf(line);
        }
        br.close();
    }

    /**
     * Gets the level loaded from the file.
     * 
     * @return
     */
    public Levels getLevelFromFile() {

        return levelToRead;
    }

    /**
     * Returns the thread of the current class.
     * 
     * @return {@code Thread}
     */
    public Thread geThread() {
        return readerThread;
    }

}
