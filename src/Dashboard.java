import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

private int userId;
private String userName;
private String userRole;

public Dashboard(int userId, String userName, String userRole) {
this.userId = userId;
this.userName = userName;
this.userRole = userRole;

setTitle("Lost and Found Portal");
setSize(450, 300);
setLocationRelativeTo(null);
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

JLabel welcomeLabel = new JLabel(
"Welcome, " + userName + "!",
SwingConstants.CENTER
);

JLabel roleLabel = new JLabel(
"Role: " + userRole,
SwingConstants.CENTER
);

JButton logoutButton = new JButton("Logout");

logoutButton.addActionListener(e -> {
dispose();
new Login().setVisible(true);
});

JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
panel.setBorder(
BorderFactory.createEmptyBorder(30, 30, 30, 30)
);

panel.add(welcomeLabel);
panel.add(roleLabel);
panel.add(logoutButton);

JButton reportButton = new JButton("Report Lost/Found Item");

reportButton.addActionListener(e -> {
JTextField itemNameField = new JTextField();
String[] itemTypes = {"Lost", "Found"};

JComboBox<String> typeBox =
new JComboBox<>(itemTypes);

JTextField locationField = new JTextField();
JTextField descriptionField = new JTextField();

JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));

form.add(new JLabel("Report type:"));
form.add(typeBox);

form.add(new JLabel("Item name:"));
form.add(itemNameField);

form.add(new JLabel("Location:"));
form.add(locationField);

form.add(new JLabel("Description:"));
form.add(descriptionField);

int result = JOptionPane.showConfirmDialog(
this,
form,
"Report Lost/Found Item",
JOptionPane.OK_CANCEL_OPTION,
JOptionPane.PLAIN_MESSAGE
);

if (result == JOptionPane.OK_OPTION) {
if (itemNameField.getText().trim().isEmpty()) {
JOptionPane.showMessageDialog(
this,
"Please enter an item name."
);
} else {
JOptionPane.showMessageDialog(
this,
"Form completed! Database saving is the next step."
);
}
}
});


panel.add(reportButton);

if ("admin".equalsIgnoreCase(userRole)) {
JButton adminButton = new JButton("Admin Panel");

adminButton.addActionListener(e -> {
JOptionPane.showMessageDialog(
this,
"Admin access granted."
);
});

panel.add(adminButton);
}

add(panel);
}
}
