
import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
/**
 * {@code ChatGUI} : Class that displays the chat box for the server. Acts like
 * a log terminal.
 */
public class ChatClient extends JPanel implements ActionListener {
    /**
     * Text area for the chat
     */
    private JTextArea textOutput;

    /**
     * Type text area for the chat box.
     */
    private JTextField textInput;
    
    /**
     * Output stream to server.
     */
    private DataOutputStream out;

    /**
     * Constructor for the client session
     * 
     * @param title
     */
    ChatClient(String title) {
    
        // Parameters text areas.
        int width = 30;

        JLabel chatTitle = new JLabel(title + " : Chat box", SwingConstants.CENTER);
        JLabel chatSubTitle = new JLabel("Send a message here  ", SwingConstants.CENTER);
        textOutput = new JTextArea(10, width);
        textInput = new JTextField(width);
        textInput.addActionListener((ActionListener) this);
        textOutput.setEditable(false);

        JPanel panelSouth = new JPanel(new BorderLayout());

        JScrollPane scrollOutput = new JScrollPane(textOutput);
        DefaultCaret caret = (DefaultCaret) textOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollInput = new JScrollPane(textInput);


        setLayout(new BorderLayout());
        
        panelSouth.add(scrollInput, BorderLayout.WEST);
        panelSouth.add(chatSubTitle, BorderLayout.CENTER);

        add(chatTitle, BorderLayout.NORTH);
        add(scrollOutput, BorderLayout.CENTER);
        add(panelSouth, BorderLayout.SOUTH);

    }

    /**
     * Client : Setter which take the outstream of {@code Client} to {@code Server}.
     * 
     * @param outStream
     */
    public void setOutputStream(DataOutputStream outStream) {
        this.out = outStream;
    }

    
    /**
     * It displays the message from a client to the server chat box, in the text
     * area.
     * 
     * @param message : {@code String} message to display.
     */
    public synchronized void addTextToChat(String message) {
        textOutput.append(message + "\n");
    }
    
    /**
     * Client : Sends the message typed to the server
     * 
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String message = textInput.getText();
        try {
            out.writeUTF(message);
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null, "Failed to send message : '" + message + "''.",
                    "ERROR", JOptionPane.WARNING_MESSAGE);
            // e1.printStackTrace();
        }
        textInput.setText("");
    }

}
