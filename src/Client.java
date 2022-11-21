import javax.swing.*;
import java.net.*;
import java.util.Random;
import java.io.*;
import java.awt.*;

/**
 * {@code Client} : Client program that makes a connection with a {@code Server}
 * via socket, referenced by ip address and port.
 * 
 * @see Server
 */
public class Client implements Runnable {

    /**
     * Input Stream for collecting data from server.
     */
    private DataInputStream in;
    private DataOutputStream out;
    private Socket sock;
    /**
     * Thread which reads the incoming data of the server and displays it on the
     * chat Case.
     */
    private Thread chatReader = new Thread(this);

    private String pseudo;
    /**
     * Label for connected clients in the subMenu
     */

    private JMenuItem connectedClient;
    private JMenu connectedClients = new JMenu();
    private GUI clientGui;

    Client(GUI gui) {

        this.clientGui = gui;

        // Realease version mode
        setClientParameters();

        // Debugging mode
        // Random r = new Random();
        // int alea = r.nextInt((100 - 0) + 1) + 0;
        // pseudo = "Client-"+alea;
        // gui.setTitleFrame(pseudo);
        // runClient("localhost", 10000,pseudo); // Dev usage

    }

    // GUI METHODS

    // NETWORK

    /**
     * Sets the client parameters for the client session.
     */
    public void setClientParameters() {
        JTextField addressField = new JTextField();
        JTextField portField = new JTextField();
        JTextField pseudoField = new JTextField();
        Object[] message = {
                "Ip Address:", addressField,
                "Port:", portField,
                "Pseudo:", pseudoField
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Set connection to the server",
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) { // Check if something is entered
            String address = addressField.getText();
            int port = Integer.valueOf(portField.getText());
            pseudo = pseudoField.getText();
            runClient(address, port, pseudo);
        } else {
            JOptionPane.showMessageDialog(null, "Nothing selected. Press OK to exit.",
                    "ERROR", JOptionPane.WARNING_MESSAGE);
            clientGui.modeOffline();
        }
    }

    /**
     * Establishes a connection with the server and allows to send messages
     * 
     * @param address
     * @param port
     * @param pseudo
     */
    private void runClient(String address, int port, String pseudo) {
        int idClient;
        System.out.print("Trying to connect to " + address + " port:" + port + "...");
        try {
            sock = new Socket(address, port);
            System.out.println("done.");

            // Initialize the streams
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());

            // Get information from the server
            out.writeUTF(pseudo);

            // Receive data : unique id of the client.
            idClient = in.readInt(); // id of client : reception
            System.out.println("Client id: " + idClient);

            // Read message from the server

            chatReader.start();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(null, address + ":" + port + " unreachable. Retry later.",
                    "ERROR", JOptionPane.WARNING_MESSAGE);
            // setClientParameters();
            clientGui.modeOffline();
        }
    }

    /**
     * Thread's method to read the incoming data and put in on the chat Case GUI.
     */
    @Override
    public void run() {

        clientGui.setChatClient(pseudo, out);

        String messageReceived = new String();
        while (!messageReceived.equals("end")) {
            try {
                int indexCaseReceived;
                messageReceived = in.readUTF();

                // (Re) Initialize the field via server
                if (messageReceived.equals("-1:initField")) {
                    System.out.println("Received field..");
                    int dimParam;
                    boolean valueBool;
                    dimParam = Integer.valueOf(in.readUTF());

                    int numMinesToPlace;
                    numMinesToPlace = Integer.valueOf(in.readUTF());
                    clientGui.setField(new Field(numMinesToPlace, dimParam));

                    for (int x = 0; x < dimParam; x++) {
                        for (int y = 0; y < dimParam; y++) {
                            messageReceived = in.readUTF();
                            if (messageReceived.equals("x")) {
                                valueBool = true;
                            } else {
                                valueBool = false;
                            }
                            clientGui.setFieldXY(x, y, valueBool);

                        }
                    }
                    messageReceived = in.readUTF(); // Level mode selected on Server
                    clientGui.setLevelMode(messageReceived);
                    clientGui.timeInit();
                    clientGui.initializationFieldPanel();
                }

                // Simulate a right click on a specific index
                else if (messageReceived.equals("-1:rightClick")) {
                    indexCaseReceived = in.readInt();
                    clientGui.updateCase(indexCaseReceived, "rightClick");

                }

                // Simulate a left click on a specific index
                else if (messageReceived.equals("-1:leftClick")) {
                    indexCaseReceived = in.readInt();
                    clientGui.updateCase(indexCaseReceived, "leftClick");
                }

                // Update the connected clients list
                else if (messageReceived.equals("-1:connectedClients")) {
                    int totalConnected = in.readInt();

                    clientGui.getConnectedClients().removeAll();
                    for (int i = 0; i < totalConnected; i++) {
                        messageReceived = in.readUTF();
                        connectedClient = new JMenuItem(messageReceived);
                       
                        System.out.println(messageReceived);
                        clientGui.getConnectedClients().add(connectedClient);
                    }
                }

                // Display a message on the chat box
                else {

                    clientGui.getChatClient().addTextToChat(messageReceived);
                }

            } catch (IOException e) { // Server off
                messageReceived = "end";
            }
        }
        chatReader = null;
    }

    /**
     * Sends a message to the server
     * 
     * @param message
     */
    public void sendMessageToServer(String message) {
        try {
            out.writeUTF(message);

        } catch (IOException e) {
            System.out.println("error sending message: " + e.getMessage());
        }
    }

    /**
     * Simulate a click on a specific case
     * 
     * @param indexCase
     * @param notification
     */
    public void clickOnCaseToServer(int indexCase, String notification) {
        try {
            System.out.println(notification + " " + indexCase);
            out.writeUTF(notification);
            out.writeInt(indexCase);

        } catch (IOException e) {
            System.out.println("error sending message: " + e.getMessage());
        }
    }

    public void endSession() {
        try {
            out.writeUTF("end");
            out.close();
            in.close();
            sock.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Stream and socket already closed");
        }
    }

    public void setConnectedClients(JMenu connectedClientsFromGUI) {
        connectedClientsFromGUI = connectedClients;
    }
}