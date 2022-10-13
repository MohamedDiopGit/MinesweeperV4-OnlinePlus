import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * {@code LevelsFileWriter} class that writes and saves the level game mode in a
 * file.
 */
public class LevelsFileWriter implements Runnable {
    /**
     * Thread of the class that allows write in file.
     */
    private Thread writeThread;
    /**
     * Level to save in the file.
     */
    private Levels levelToWrite;

    /**
     * Constructor for the class.
     */
    LevelsFileWriter(Levels level) {
        this.writeThread = new Thread(this);
        this.levelToWrite = level;
        writeThread.start();
    }

    /**
     * Method called when the thread is started.
     */
    @Override
    public synchronized void run() {
        while (writeThread != null) {
            System.out.print("Saving " + levelToWrite + " mode in 'LevelRegistered.dat'... ");
            writeString(String.valueOf(this.levelToWrite.ordinal()));
            System.out.println("done");
            writeThread = null;
        }
    }

    /**
     * Writes the given level (int value) in the file.
     * 
     * @param {@code String} : stringToWrite
     */
    public static synchronized void writeString(String stringToWrite) {
        try (RandomAccessFile file = new RandomAccessFile("LevelRegistred.dat", "rw")) {
            file.write(stringToWrite.getBytes());
        } catch (IOException ex) {
            ex.getMessage();
        }
    }

}