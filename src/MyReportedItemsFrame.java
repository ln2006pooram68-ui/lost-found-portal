
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
                new String[]{"ID", "Name", "Location", "Date",
                        "Type", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");

        JPanel buttons = new JPanel();
        buttons.add(refreshButton);
        add(buttons, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadItems());

        loadItems();
    }

    private void loadItems() {
        model.setRowCount(0);

        String sql = "SELECT id, name, location, item_date, type, status "
        + "FROM items WHERE reporter_id = ? "
        + "AND UPPER(status) <> 'RETURNED' ORDER BY id DESC";

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
            JOptionPane.showMessageDialog(this,
                    "Could not load reported items.");
        }
    }

    
        
}
