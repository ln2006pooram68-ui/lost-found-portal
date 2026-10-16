
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:sqlite:database/test.db"
            );

            Statement stmt = con.createStatement();

            String sql = "INSERT INTO items " +
                         "(item_name, description, location, date_reported, status) " +
                         "VALUES ('Blue Wallet', 'Small blue wallet', " +
                         "'College Library', '2026-10-17', 'Lost')";

            stmt.executeUpdate(sql);

            System.out.println("Item inserted successfully!");

            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM items"
            );

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Item: " + rs.getString("item_name"));
                System.out.println("Location: " + rs.getString("location"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("-------------------");
            }

            rs.close();
            stmt.close();
            con.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
