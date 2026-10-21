
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Vector;

public class ChatFrame extends JFrame {

    private int itemId;
    private int userId;
    private int otherUserId;

    private MessageDAO messageDAO = new MessageDAO();
    private JTextArea chatArea;
    private JTextField messageField;

    public ChatFrame(int itemId, int userId, int otherUserId) {
        this.itemId = itemId;
        this.userId = userId;
        this.otherUserId = otherUserId;

        setTitle("Item Conversation");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        messageField = new JTextField();

        JButton sendButton = new JButton("Send");

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );

        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());

        messageField.addActionListener(e -> sendMessage());

        loadMessages();
    }

    private void sendMessage() {
        String text = messageField.getText().trim();

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message.");
            return;
        }

        try {
            messageDAO.sendMessage(
                itemId, userId, otherUserId, text
            );

            messageField.setText("");
            loadMessages();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Could not send message: " + e.getMessage()
            );
        }
    }

    private void loadMessages() {
        try {
            Vector<Vector<Object>> messages =
                messageDAO.getMessages(itemId, userId, otherUserId);

            chatArea.setText("");

            for (Vector<Object> message : messages) {
                int senderId = (Integer) message.get(0);
                String senderName = (String) message.get(1);
                String text = (String) message.get(2);
                String time = (String) message.get(3);

                String sender = (senderId == userId)
                    ? "You" : senderName;

                chatArea.append(
                    sender + " [" + time + "]:\n"
                    + text + "\n\n"
                );
            }

            chatArea.setCaretPosition(chatArea.getDocument().getLength());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Could not load messages: " + e.getMessage()
            );
        }
    }
}

