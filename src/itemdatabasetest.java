
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class itemdatabasetest {

    public static void main(String[] args) {

        try (Connection con =
                DBConnection.getInstance().getConnection()) {

            // Create a test user if it does not exist
            String userSQL =
                    "INSERT OR IGNORE INTO users " +
                    "(name, email, password_hash) " +
                    "VALUES (?, ?, ?)";

            try (PreparedStatement ps =
                    con.prepareStatement(userSQL)) {

                ps.setString(1, "Test User");
                ps.setString(2, "itemtest@example.com");
                ps.setString(3, "test-only");

                ps.executeUpdate();
            }

            // Find the test user's ID
            int userId;

            String findUser =
                    "SELECT id FROM users WHERE email = ?";

            try (PreparedStatement ps =
                    con.prepareStatement(findUser)) {

                ps.setString(1, "itemtest@example.com");

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new Exception("Test user not found");
                    }

                    userId = rs.getInt("id");
                }
            }

            // Remove the previous test item, if any
            String deleteSQL =
                    "DELETE FROM items WHERE name = ? " +
                    "AND reporter_id = ? AND type = ?";

            try (PreparedStatement ps =
                    con.prepareStatement(deleteSQL)) {

                ps.setString(1, "Test Lost Bag");
                ps.setInt(2, userId);
                ps.setString(3, "LOST");

                ps.executeUpdate();
            }

            // Save one test item
            String insertSQL =
                    "INSERT INTO items " +
                    "(name, description, location, item_date, " +
                    "type, reporter_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps =
                    con.prepareStatement(insertSQL)) {

                ps.setString(1, "Test Lost Bag");
                ps.setString(2, "Black college bag");
                ps.setString(3, "College library");
                ps.setString(4, "2026-10-17");
                ps.setString(5, "LOST");
                ps.setInt(6, userId);

                ps.executeUpdate();
            }

            System.out.println("Item saved successfully!");

            // Read the saved item
            String selectSQL =
                    "SELECT id, name, description, location, " +
                    "type, status FROM items " +
                    "WHERE name = ? AND reporter_id = ? " +
                    "AND type = ?";

            try (PreparedStatement ps =
                    con.prepareStatement(selectSQL)) {

                ps.setString(1, "Test Lost Bag");
                ps.setInt(2, userId);
                ps.setString(3, "LOST");

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        System.out.println(
                                "ID: " + rs.getInt("id"));
                        System.out.println(
                                "Name: " + rs.getString("name"));
                        System.out.println(
                                "Description: " +
                                rs.getString("description"));
                        System.out.println(
                                "Location: " +
                                rs.getString("location"));
                        System.out.println(
                                "Type: " + rs.getString("type"));
                        System.out.println(
                                "Status: " + rs.getString("status"));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
