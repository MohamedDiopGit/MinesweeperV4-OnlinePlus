
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
public class ChatServer extends JPanel implements ActionListener {
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
     * Constructor by default for the chat GUI server.
     */
    ChatServer() {
        

        JLabel chatTitle = new JLabel("Server : Chat box", SwingConstants.CENTER);
        JLabel chatSubTitle = new JLabel("Message logs recording", SwingConstants.CENTER);
        textOutput = new JTextArea(10, 30);
        textOutput.setEditable(false);

        JScrollPane scrollOutput = new JScrollPane(textOutput);
        DefaultCaret caret = (DefaultCaret) textOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        setLayout(new BorderLayout());

        add(chatTitle, BorderLayout.NORTH);
        add(scrollOutput, BorderLayout.CENTER);
        add(chatSubTitle, BorderLayout.SOUTH);
        

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
