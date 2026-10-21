import java.awt.Component;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Login extends JFrame {
   private JTextField emailField = new JTextField(20);
   private JPasswordField passwordField = new JPasswordField(20);

   public Login() {
      this.setTitle("Lost and Found Portal - Login");
      this.setSize(400, 220);
      this.setDefaultCloseOperation(3);
      this.setLocationRelativeTo((Component)null);
      JPanel var1 = new JPanel(new GridLayout(3, 2, 10, 10));
      var1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      var1.add(new JLabel("Email:"));
      var1.add(this.emailField);
      var1.add(new JLabel("Password:"));
      var1.add(this.passwordField);
      JButton var2 = new JButton("Login");
      var1.add(new JLabel());
      var1.add(var2);
      this.add(var1);
      var2.addActionListener((var1x) -> this.loginUser());
   }

   private void loginUser() {
      String var1 = this.emailField.getText().trim();
      char[] var2 = this.passwordField.getPassword();
      String var3 = new String(var2);
      if (!var1.isEmpty() && !var3.isEmpty()) {
         String var4 = "SELECT id, name, role FROM users WHERE email = ? AND password_hash = ?";

         try {
            Connection var5 = DBConnection.getInstance().getConnection();

            try {
               PreparedStatement var6 = var5.prepareStatement(var4);

               try {
                  var6.setString(1, var1);
                  var6.setString(2, var3);
                  ResultSet var7 = var6.executeQuery();

                  try {
                     if (var7.next()) {
                        int userId = var7.getInt("id");
                        String userName = var7.getString("name");
                        String userRole = var7.getString("role");

                        JOptionPane.showMessageDialog(this, "Login successful!");

                        new Dashboard(userId, userName, userRole).setVisible(true);
                        dispose();
                     } else {
                        JOptionPane.showMessageDialog(this, "Invalid email or password.");
                     }
                  } catch (Throwable var22) {
                     if (var7 != null) {
                        try {
                           var7.close();
                        } catch (Throwable var21) {
                           var22.addSuppressed(var21);
                        }
                     }

                     throw var22;
                  }

                  if (var7 != null) {
                     var7.close();
                  }
               } catch (Throwable var23) {
                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var20) {
                        var23.addSuppressed(var20);
                     }
                  }

                  throw var23;
               }

               if (var6 != null) {
                  var6.close();
               }
            } catch (Throwable var24) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var19) {
                     var24.addSuppressed(var19);
                  }
               }

               throw var24;
            }

            if (var5 != null) {
               var5.close();
            }
         } catch (SQLException var25) {
            JOptionPane.showMessageDialog(this, "Database error: " + var25.getMessage());
         } finally {
            Arrays.fill(var2, '\u0000');
         }

      } else {
         JOptionPane.showMessageDialog(this, "Please fill in all fields.");
      }
   }

   public static void main(String[] var0) {
      SwingUtilities.invokeLater(() -> (new Login()).setVisible(true));
   }
}

