import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class SearchItemFrame extends JFrame {

    private JTextField nameField;
    private JTextField locationField;
    private JButton searchButton;
    private JButton showAllButton;
    private DefaultTableModel tableModel;
    private JTable table;

    private ItemSearchDAO dao = new ItemSearchDAO();

    public SearchItemFrame() {
        setTitle("Search Lost / Found Items");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Vector<String> columnNames = new Vector<String>();
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

        nameField = new JTextField(12);
        locationField = new JTextField(12);
        searchButton = new JButton("Search");
        showAllButton = new JButton("Show All");

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Item Name:"));
        topPanel.add(nameField);
        topPanel.add(new JLabel("Location:"));
        topPanel.add(locationField);
        topPanel.add(searchButton);
        topPanel.add(showAllButton);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadItems();
            }
        });

        showAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nameField.setText("");
                locationField.setText("");
                loadItems();
            }
        });

        loadItems();
    }

    private void loadItems() {
        Vector<Vector<Object>> rows =
                dao.searchItems(nameField.getText().trim(), locationField.getText().trim());

        tableModel.setRowCount(0);     
        for (Vector<Object> row : rows) {
            tableModel.addRow(row);
        }
    }
}