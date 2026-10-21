import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class MessageDAO {

    // Save a message in the database
    public void sendMessage(int itemId, int senderId,
                            int receiverId, String messageText)
                            throws SQLException {

        String sql = "INSERT INTO messages " +
                     "(item_id, sender_id, receiver_id, message_text) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ps.setInt(2, senderId);
            ps.setInt(3, receiverId);
            ps.setString(4, messageText);

            ps.executeUpdate();
        }
    }

    // Retrieve messages exchanged by two users about an item
    public Vector<Vector<Object>> getMessages(
            int itemId, int user1Id, int user2Id)
            throws SQLException {

        Vector<Vector<Object>> messages = new Vector<>();

        String sql = "SELECT m.sender_id, u.name, m.message_text, m.sent_at " +
                     "FROM messages m " +
                     "JOIN users u ON m.sender_id = u.id " +
                     "WHERE m.item_id = ? " +
                     "AND ((m.sender_id = ? AND m.receiver_id = ?) " +
                     "OR (m.sender_id = ? AND m.receiver_id = ?)) " +
                     "ORDER BY m.id ASC";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ps.setInt(2, user1Id);
            ps.setInt(3, user2Id);
            ps.setInt(4, user2Id);
            ps.setInt(5, user1Id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> message = new Vector<>();

                    message.add(rs.getInt("sender_id"));
                    message.add(rs.getString("name"));
                    message.add(rs.getString("message_text"));
                    message.add(rs.getString("sent_at"));

                    messages.add(message);
                }
            }
        }

        return messages;
    }
}