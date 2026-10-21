
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchItemFrame extends JFrame {

    private int userId;

    private JTextField nameField;
    private JTextField locationField;
    private JButton searchButton;
    private JButton showAllButton;
    private JButton claimButton;
    private JButton messageButton;

    private DefaultTableModel tableModel;
    private JTable table;

    private ItemSearchDAO dao = new ItemSearchDAO();

    public SearchItemFrame(int userId) {
        this.userId = userId;

        setTitle("Search Lost / Found Items");
        setSize(850, 450);
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

        tableModel = new DefaultTableModel(columnNames, 0) {
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
            tableModel.addRow(row);
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
                tableModel.getValueAt(selectedRow, 0).toString()
        );

        String itemName =
                tableModel.getValueAt(selectedRow, 1).toString();

        String type =
                tableModel.getValueAt(selectedRow, 5).toString();

        String status =
                tableModel.getValueAt(selectedRow, 6).toString();

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
                tableModel.getValueAt(selectedRow, 0).toString()
        );

        String itemName =
                tableModel.getValueAt(selectedRow, 1).toString();

        int reporterId;

        String sql = "SELECT reporter_id FROM items WHERE id = ?";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(
                            this, "Could not find the item's reporter."
                    );
                    return;
                }

                reporterId = rs.getInt("reporter_id");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, "Could not find reporter: " + e.getMessage()
            );
            return;
        }

        if (reporterId == userId) {
            JOptionPane.showMessageDialog(
                    this, "You cannot message yourself about your own item."
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Opening conversation about: " + itemName
        );

        new ChatFrame(itemId, userId, reporterId).setVisible(true);
    }
}
