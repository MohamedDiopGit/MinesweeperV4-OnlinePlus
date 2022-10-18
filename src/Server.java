import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import java.io.*; // Streams
import java.net.*;

import static java.lang.Thread.currentThread;

/**
 * {@code Server} : minesweeper class that creates a server for communication between
 * multiple clients with dynamic multithreading.
 */
public class Server implements Runnable {
    /**
     * Server socket center.
     */
    private ServerSocket gestSock;
    /**
     * Array of the threads.
     */
    private static List<Thread> clients = new ArrayList<Thread>();
    private static List<String> pseudoClients = new ArrayList<String>();
    /* 
     * Array of the out streams for broadcasting messages.
     */
    private static List<DataOutputStream> outs = new ArrayList<DataOutputStream>();


    /**
     * Data formatter to send the data with messages
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss z");

    /**
     * Label for connected clients in the subMenu
     */
    private JMenu connectedClients = new JMenu("Connected clients");
    
    /**
     * minesweeper Server program.
     */

     

    private GUI serverGui;
    private ChatServer chatServer;

    public static void main(String args[]) {
        System.out.println("Running server...");
        new Server();
    }
    /**
     * Constructor for the server.
     */
    Server() {


        // Chat GUI display

        JMenuItem totalConnectedClient = new JMenuItem("Total connected clients");
        JMenu infoMenu = new JMenu("Server infos");

        infoMenu.add(totalConnectedClient);    
        infoMenu.add(connectedClients);

        
        
        // GUI : Minesweeper interface server-side
        Main main = new Main();
        main.setTitle("Server");
        serverGui = main.getGui();
        serverGui.setServer(this);
        // serverGui.setConnectedClientsOnMenu(connectedClients);
        // add(serverGui);
        
        // setJMenuBar(serverGui.getMenuBar());

        
        chatServer = new ChatServer();
        main.add(chatServer);
        main.pack();
        // // Frame settings
        // // setContentPane(serverGui);
        // pack();
        // // setResizable(false);
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Shuts down the server when exit
        // setVisible(true);

        

        // Threads creation
        try {// Socket manager : port 10000
            gestSock = new ServerSocket(10000);
            Thread client = new Thread(this);
            client.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 

    // NETWORK

    @Override
    public void run() {
        int idClient = (int) currentThread().getId();
        try {
            Socket socket = gestSock.accept(); // Waiting for connection
            clients.add(currentThread());

            // Add a thread to wait for another connection
            Thread client = new Thread(this);
            client.start();
            
            

            // Establish a stream connection with client
            DataInputStream entree = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            outs.add(out);

            // Data reading
            String pseudoClient = entree.readUTF();
            JMenuItem pseudoClientItem = new JMenuItem(pseudoClient);
            pseudoClients.add(pseudoClient);
            // connectedClients.add(pseudoClientItem);
            serverGui.getConnectedClients().add(pseudoClientItem);
            
            // Send data : unique id of the client.
            out.writeInt(idClient);

            // Connection notification to all clients connected
            chatServer.addTextToChat(getUtcDateTime() + " [" + pseudoClient + "]: " + " is connected");
            notifyConnectionToAll(idClient, pseudoClient, true, out);
            
            updateConnectedClientToAll();
            sendToAllField();
            
            // Read data from client
            String message = "";
            while (!message.equals("end")) {
                try {
                    message = entree.readUTF();

                    if(message.equals("-1:rightClick")){
                        int indexCaseReceived = entree.readInt();
                        serverGui.updateCase(indexCaseReceived, "rightClick");
                        updateCaseToAll(indexCaseReceived, message, out);
                    }
                    else if(message.equals("-1:leftClick")){
                        int indexCaseReceived = entree.readInt();;
                        serverGui.updateCase(indexCaseReceived, "leftClick");
                        updateCaseToAll(indexCaseReceived, message, out);
                    }
                    else if(message.equals("-1:resetField")){
                        resetAllMineSweeper();
                    }
                    else{
                        chatServer.addTextToChat(getUtcDateTime() + " :[" + pseudoClient + "]: " + message);
                        sendToAll(pseudoClient, message); // Broadcast to the others connected clients
                    }

                } catch (EOFException | SocketException e) {
                    message = "end";
                }
            }


            
            // Clean close of the session
            chatServer.addTextToChat(getUtcDateTime() + " [" + pseudoClient + "]: " + " has disconnected.");
            notifyConnectionToAll(idClient, pseudoClient, false, out);


            out.close();
            outs.remove(out);

            entree.close();
            socket.close();

            pseudoClients.remove(pseudoClient);
            // connectedClients.remove(pseudoClientItem);
            serverGui.getConnectedClients().remove(pseudoClientItem);
            updateConnectedClientToAll();

        } catch (IOException e) {// Quick cleaning
            // throw new RuntimeException();
            System.out.println("Failed to connect on thread: " + idClient + ",please retry.");
        }
        clients.remove(currentThread());
    }

    private void updateConnectedClientToAll() {
        outs.forEach(o -> {
            try {
                o.writeUTF("-1:connectedClients");
                o.writeInt(pseudoClients.size());
                pseudoClients.forEach(pseudo -> {
                    try {
                        o.writeUTF(pseudo);
                    } catch (IOException e) {
                        System.out.println("error writing message : updateConnectedClientToAll");
                    }
                });
            } catch (IOException e) {
                System.out.println("error writing message : updateConnectedClientToAll");
            }
        });
    }

    private void resetServerField(){
        serverGui.reinitialize();
    }
    private void resetAllMineSweeper() {
        resetServerField();
        sendToAllField();
    }

    private void updateCaseToAll(int indexCaseReceived, String typeClicked, DataOutputStream outClient) {
        outs.forEach(o -> {
            try {
                if(!o.equals(outClient)) {
                    o.writeUTF(typeClicked);
                    o.writeInt(indexCaseReceived);
                }
            } catch (IOException e) {
                System.out.println("error writing message : notifyConnectionToAll");
            }
        });
    }

public void sendToAllField() {
        outs.forEach(o -> {
            try {
                o.writeUTF("-1:initField");
                int dimParam = serverGui.getField().getDim();
                o.writeUTF(String.valueOf(dimParam));
                int numMinesToPlace = serverGui.getField().getNumberOfMines();
                o.writeUTF(String.valueOf(numMinesToPlace));

                for(int x=0; x<dimParam; x++) {
                    for(int y=0; y<dimParam; y++) {
                        o.writeUTF(serverGui.getField().getElementFromXY(x,y, false));
                    }
                }
                o.writeUTF(serverGui.getLevelMode());
                
            } catch (IOException e) {
                System.out.println("error writing message : sendToAll");
            }
        });
        serverGui.timeInit();
    }

    public static String getUtcDateTime() {
        return ZonedDateTime.now(ZoneId.of("Etc/UTC")).format(FORMATTER);
    }

    /**
     * Notifies the connected client a specific client is connected
     * @param idClientConnected
     * @param pseudoClient
     * @param message
     */
    public void notifyConnectionToAll(int idClientConnected, String pseudoClient, boolean isConnected, DataOutputStream outClient) {
        String messageComplete;
        if(isConnected){
            messageComplete = getUtcDateTime() + " [" + pseudoClient + "]: " + " is connected";
        }
        else{
            messageComplete = getUtcDateTime() + " [" + pseudoClient + "]: " + " has disconnected";
        }
        outs.forEach(o -> {
            try {
                if(!o.equals(outClient)) {
                    o.writeUTF(messageComplete);
                }
            } catch (IOException e) {
                System.out.println("error writing message : notifyConnectionToAll");
            }
        });
    }

    /**
     *  Sends a message to all the connected clients
     * @param pseudoClient
     * @param message
     */
    public void sendToAll(String pseudoClient, String message) {
        String messageComplete = getUtcDateTime() + " [" + pseudoClient + "]: " + message;
        outs.forEach(o -> {
            try {
                o.writeUTF(messageComplete);
            } catch (IOException e) {
                System.out.println("error writing message : sendToAll");
            }
        });
        
    }
}