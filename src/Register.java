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
setSize(400, 250);
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setLocationRelativeTo(null);

JPanel panel = new JPanel();
panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

panel.add(makeRow("Name:", nameField));
panel.add(makeRow("Email:", emailField));
panel.add(makeRow("Password:", passwordField));

JButton button = new JButton("Register");
panel.add(button);

add(panel);

button.addActionListener(e -> registerUser());

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
row.add(new JLabel(text), java.awt.BorderLayout.WEST);
row.add(field, java.awt.BorderLayout.CENTER);
return row;
}
public static void main(String[] args) {
new Register().setVisible(true);
}
}
