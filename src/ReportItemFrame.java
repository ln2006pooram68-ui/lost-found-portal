import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReportItemFrame extends JFrame {

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField locationField;
    private JTextField dateField;
    private JComboBox<String> typeBox;
    private JButton submitButton;

    private int reporterId;                  
    private ItemDAO dao = new ItemDAO();     

    public ReportItemFrame(int reporterId) {
        this.reporterId = reporterId;

        setTitle("Report Lost / Found Item");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        nameField = new JTextField();
        descriptionArea = new JTextArea(3, 20);
        locationField = new JTextField();
        dateField = new JTextField(LocalDate.now().toString()); 
        typeBox = new JComboBox<String>(new String[] {"LOST", "FOUND"});
        submitButton = new JButton("Submit Report");

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        panel.add(new JLabel("Date (yyyy-MM-dd):"));
        panel.add(dateField);
        panel.add(new JLabel("Type:"));
        panel.add(typeBox);
        panel.add(new JLabel(""));
        panel.add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveItem();
            }
        });

        add(panel);
    }

    private void saveItem() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String location = locationField.getText().trim();
        String date = dateField.getText().trim();
        String type = (String) typeBox.getSelectedItem();

        if (name.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Item name and location are required.",
                    "Missing Information", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Date must look like 2025-01-31.",
                    "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Item item = new Item(name, description, location, date, type, reporterId);
        boolean saved = dao.saveItem(item);

        if (saved) {
            JOptionPane.showMessageDialog(this,
                    "Report submitted! Status: PENDING ");
            nameField.setText("");
            descriptionArea.setText("");
            locationField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Could not save the report.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}