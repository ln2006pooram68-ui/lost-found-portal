import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class SearchItemFrame extends JFrame {
 
    // Column positions in the table. Columns 0-6 come from ItemSearchDAO,
    // column 7 (Credibility) is added by this frame.
    private static final int COL_ID = 0;
    private static final int COL_NAME = 1;
    private static final int COL_TYPE = 5;
    private static final int COL_STATUS = 6;
    private static final int COL_CREDIBILITY = 7;
 
    private int userId;
 
    private JTextField nameField;
    private JTextField locationField;
    private JButton searchButton;
    private JButton showAllButton;
    private JButton claimButton;
    private JButton messageButton;
    private JButton genuineButton;
    private JButton suspiciousButton;
 
    private DefaultTableModel tableModel;
    private JTable table;
 
    private ItemSearchDAO dao = new ItemSearchDAO();
    private VerificationDAO verificationDAO = new VerificationDAO();
 
    public SearchItemFrame(int userId) {
        this.userId = userId;
 
        setTitle("Search Lost / Found Items");
        setSize(1000, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Name");
        columnNames.add("Description");
        columnNames.add("Location");
        columnNames.add("Date");
        columnNames.add("Type");
        columnNames.add("Status");
        columnNames.add("Credibility");
 
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
 
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 
        nameField = new JTextField(12);
        locationField = new JTextField(12);
 
        searchButton = new JButton("Search");
        showAllButton = new JButton("Show All");
        claimButton = new JButton("Claim Item");
        messageButton = new JButton("Message Reporter");
        genuineButton = new JButton("Genuine");
        suspiciousButton = new JButton("Suspicious");
 
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Item Name:"));
        topPanel.add(nameField);
        topPanel.add(new JLabel("Location:"));
        topPanel.add(locationField);
        topPanel.add(searchButton);
        topPanel.add(showAllButton);
 
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(claimButton);
        bottomPanel.add(messageButton);
        bottomPanel.add(new JLabel("   Rate this post:"));
        bottomPanel.add(genuineButton);
        bottomPanel.add(suspiciousButton);
 
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
 
        searchButton.addActionListener(e -> loadItems());
 
        showAllButton.addActionListener(e -> {
            nameField.setText("");
            locationField.setText("");
            loadItems();
        });
 
        claimButton.addActionListener(e -> claimSelectedItem());
        messageButton.addActionListener(e -> messageSelectedReporter());
        genuineButton.addActionListener(e -> voteOnSelectedItem("GENUINE"));
        suspiciousButton.addActionListener(e -> voteOnSelectedItem("SUSPICIOUS"));
 
        loadItems();
    }
 
    private void loadItems() {
        Vector<Vector<Object>> rows =
                dao.searchItems(
                        nameField.getText().trim(),
                        locationField.getText().trim()
                );
 
        tableModel.setRowCount(0);
 
        for (Vector<Object> row : rows) {
            int itemId = (Integer) row.get(COL_ID);
 
            // Add the credibility score as the last column.
            // (If the DAO already returns 8 columns, replace instead of add.)
            if (row.size() > COL_CREDIBILITY) {
                row.set(COL_CREDIBILITY, formatScore(itemId));
            } else {
                row.add(formatScore(itemId));
            }
 
            tableModel.addRow(row);
        }
    }
 
    // Turns an item's votes into text like "75% (4 votes)" or "No votes yet".
    private String formatScore(int itemId) {
        int votes = verificationDAO.getVoteCount(itemId);
 
        if (votes == 0) {
            return "No votes yet";
        }
 
        double score = verificationDAO.getCredibilityScore(itemId);
        return String.format("%.0f%% (%d %s)",
                score, votes, votes == 1 ? "vote" : "votes");
    }
 
    // Finds who reported an item. Returns -1 if it cannot be found.
    private int getReporterId(int itemId) {
        String sql = "SELECT reporter_id FROM items WHERE id = ?";
 
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setInt(1, itemId);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("reporter_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
        return -1;
    }
 
    private void voteOnSelectedItem(String vote) {
        int selectedRow = table.getSelectedRow();
 
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select an item first."
            );
            return;
        }
 
        int itemId = Integer.parseInt(
                tableModel.getValueAt(selectedRow, COL_ID).toString()
        );
 
        String itemName =
                tableModel.getValueAt(selectedRow, COL_NAME).toString();
 
        // Rule 1: you cannot rate your own post.
        int reporterId = getReporterId(itemId);
 
        if (reporterId == -1) {
            JOptionPane.showMessageDialog(
                    this, "Could not find the item."
            );
            return;
        }
 
        if (reporterId == userId) {
            JOptionPane.showMessageDialog(
                    this, "You cannot rate your own post."
            );
            return;
        }
 
        // Rule 2: one vote per user per item.
        if (verificationDAO.hasUserVoted(itemId, userId)) {
            JOptionPane.showMessageDialog(
                    this, "You have already rated this item."
            );
            return;
        }
 
        String label = vote.equals("GENUINE") ? "Genuine" : "Suspicious";
 
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Mark \"" + itemName + "\" as " + label + "?",
                "Confirm Vote",
                JOptionPane.YES_NO_OPTION
        );
 
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }
 
        boolean saved = verificationDAO.saveVote(itemId, userId, vote);
 
        if (saved) {
            // Show the new score in the same row without reloading the table.
            tableModel.setValueAt(
                    formatScore(itemId), selectedRow, COL_CREDIBILITY
            );
 
            JOptionPane.showMessageDialog(
                    this,
                    "Thank you! Your vote (" + label + ") has been recorded."
            );
        } else {
            JOptionPane.showMessageDialog(
                    this, "Could not save your vote."
            );
        }
    }
 
    private void claimSelectedItem() {
        int selectedRow = table.getSelectedRow();
 
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select an item first."
            );
            return;
        }
 
        int itemId = Integer.parseInt(
                tableModel.getValueAt(selectedRow, COL_ID).toString()
        );
 
        String itemName =
                tableModel.getValueAt(selectedRow, COL_NAME).toString();
 
        String type =
                tableModel.getValueAt(selectedRow, COL_TYPE).toString();
 
        String status =
                tableModel.getValueAt(selectedRow, COL_STATUS).toString();
 
        if (!"FOUND".equalsIgnoreCase(type)) {
            JOptionPane.showMessageDialog(
                    this, "You can only claim items reported as FOUND."
            );
            return;
        }
 
        if ("CLAIMED".equalsIgnoreCase(status)
                || "RETURNED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(
                    this, "This item is no longer available to claim."
            );
            return;
        }
 
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Submit a claim for \"" + itemName + "\"?",
                "Confirm Claim",
                JOptionPane.YES_NO_OPTION
        );
 
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }
 
        String sql =
                "INSERT INTO claims (item_id, claimant_id, status) " +
                "VALUES (?, ?, ?)";
 
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setInt(1, itemId);
            ps.setInt(2, userId);
            ps.setString(3, "PENDING");
 
            ps.executeUpdate();
 
            JOptionPane.showMessageDialog(
                    this,
                    "Claim submitted successfully! Status: PENDING"
            );
 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, "Could not submit claim: " + e.getMessage()
            );
        }
    }
 
    private void messageSelectedReporter() {
        int selectedRow = table.getSelectedRow();
 
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select an item first."
            );
            return;
        }
 
        int itemId = Integer.parseInt(
                tableModel.getValueAt(selectedRow, COL_ID).toString()
        );
 
        int reporterId;
 
        // Find who reported this item.
        String sql = "SELECT reporter_id FROM items WHERE id = ?";
 
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setInt(1, itemId);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(
                            this, "Could not find the item."
                    );
                    return;
                }
 
                reporterId = rs.getInt("reporter_id");
            }
 
            int otherUserId;
 
            if (reporterId != userId) {
                // The current user is not the reporter.
                // Open a conversation with the reporter.
                otherUserId = reporterId;
 
            } else {
                // The current user is the finder/reporter.
                // Find a person who has claimed this item.
                String claimSql =
                        "SELECT claimant_id FROM claims " +
                        "WHERE item_id = ? AND claimant_id != ? " +
                        "ORDER BY id DESC LIMIT 1";
 
                try (PreparedStatement claimPs =
                             con.prepareStatement(claimSql)) {
 
                    claimPs.setInt(1, itemId);
                    claimPs.setInt(2, userId);
 
                    try (ResultSet claimRs = claimPs.executeQuery()) {
                        if (!claimRs.next()) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "No other user has claimed this item yet."
                            );
                            return;
                        }
 
                        otherUserId = claimRs.getInt("claimant_id");
                    }
                }
            }
 
            new ChatFrame(itemId, userId, otherUserId)
                    .setVisible(true);
 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not open conversation: " + e.getMessage()
            );
        }
    }
}