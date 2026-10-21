import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Register extends JFrame {

JTextField nameField = new JTextField(15);
JTextField emailField = new JTextField(15);
JPasswordField passwordField = new JPasswordField(15);

public Register() {
setTitle("User Registration");
setSize(450, 220);
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setLocationRelativeTo(null);

JPanel panel = new JPanel();
panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

panel.add(makeRow("Name:", nameField));
panel.add(makeRow("Email:", emailField));
panel.add(makeRow("Password:", passwordField));

JButton button = new JButton("Register");
JButton loginButton = new JButton("Back to Login");

JPanel buttonPanel = new JPanel();
buttonPanel.add(button);
buttonPanel.add(loginButton);

panel.add(buttonPanel);

add(panel);

button.addActionListener(e -> registerUser());

loginButton.addActionListener(e -> {
new Login().setVisible(true);
dispose();
});

}

void registerUser() {
String name = nameField.getText();
String email = emailField.getText();
String password = new String(passwordField.getPassword());

if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
JOptionPane.showMessageDialog(this, "Fill all fields");
return;
}

try {
Connection con = DBConnection.getInstance().getConnection();

String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";

PreparedStatement ps = con.prepareStatement(sql);
ps.setString(1, name);
ps.setString(2, email);
ps.setString(3, password);
ps.setString(4, "user");

ps.executeUpdate();

JOptionPane.showMessageDialog(this, "Registration successful!");

ps.close();
con.close();

} catch (SQLException e) {
JOptionPane.showMessageDialog(this, e.getMessage());
}
}


JPanel makeRow(String text, JComponent field) {
JPanel row = new JPanel(new java.awt.BorderLayout(10, 5));
row.setMaximumSize(new java.awt.Dimension(400, 35));

JLabel label = new JLabel(text);
label.setPreferredSize(new java.awt.Dimension(80, 25));

field.setPreferredSize(new java.awt.Dimension(250, 25));

row.add(label, java.awt.BorderLayout.WEST);
row.add(field, java.awt.BorderLayout.CENTER);

return row;
}
public static void main(String[] args) {
new Register().setVisible(true);
}
}
