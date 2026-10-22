
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyReportedItemsFrame extends JFrame {

    private final int userId;
    private final DefaultTableModel model;
    private final JTable table;

    public MyReportedItemsFrame(int userId) {
        this.userId = userId;

        setTitle("My Reported Items - Lost & Found");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        model = new DefaultTableModel(
                new String[]{
                    "ID", "Name", "Location",
                    "Date", "Type", "Status"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton returnedButton = new JButton("Mark as Returned");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttons = new JPanel();
        buttons.add(returnedButton);
        buttons.add(refreshButton);
        add(buttons, BorderLayout.SOUTH);

        returnedButton.addActionListener(
                e -> markSelectedAsReturned());

        refreshButton.addActionListener(e -> loadItems());

        loadItems();
    }

    private void loadItems() {
        model.setRowCount(0);

        String sql = "SELECT id, name, location, item_date, type, status "
                + "FROM items WHERE reporter_id = ? "
                + "AND UPPER(status) <> 'RETURNED' "
                + "ORDER BY id DESC";

        try (
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getString("item_date"),
                        rs.getString("type"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Could not load reported items.");
        }
    }

    
    private void markSelectedAsReturned() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this, "Please select an item first.");
         return;
        }

        int itemId = (int) model.getValueAt(selectedRow, 0);
      String type = String.valueOf(model.getValueAt(selectedRow, 4));

     if (!"FOUND".equalsIgnoreCase(type)) {
            JOptionPane.showMessageDialog(
                    this, "Only FOUND items can be marked as returned.");
            return;
      }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "This will permanently delete the item. Continue?",
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION
     );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean deleted = new ItemDAO().deleteReturnedItem(itemId, userId);

    if (deleted) {
        JOptionPane.showMessageDialog(
                this, "Item deleted successfully.");
        loadItems();
    } else {
        JOptionPane.showMessageDialog(
                this, "Item could not be deleted.");
    }
}

}
