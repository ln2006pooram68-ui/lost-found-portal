
import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseTest {

    public static void main(String[] args) {

        //String url = "jdbc:sqlite:database/lostfound.db";

       // try (Connection con = DriverManager.getConnection(url);
       //      Statement stmt = con.createStatement()) {
        try (Connection con = DBConnection.getInstance().getConnection();
            Statement stmt = con.createStatement()) {
                
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "role TEXT NOT NULL DEFAULT 'USER')"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "item_id INTEGER NOT NULL, " +
                "sender_id INTEGER NOT NULL, " +
                "receiver_id INTEGER NOT NULL, " +
                "message_text TEXT NOT NULL, " +
                "sent_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (item_id) REFERENCES items(id), " +
                "FOREIGN KEY (sender_id) REFERENCES users(id), " +
                "FOREIGN KEY (receiver_id) REFERENCES users(id))"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "location TEXT, " +
                "item_date TEXT, " +
                "type TEXT NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'PENDING', " +
                "reporter_id INTEGER NOT NULL, " +
                "FOREIGN KEY (reporter_id) REFERENCES users(id))"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS claims (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "item_id INTEGER NOT NULL, " +
                "claimant_id INTEGER NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'PENDING', " +
                "FOREIGN KEY (item_id) REFERENCES items(id), " +
                "FOREIGN KEY (claimant_id) REFERENCES users(id))"
            );

            //System.out.println("Database and tables created successfully!");
           var rs = con.createStatement().executeQuery(
               "SELECT name FROM sqlite_master WHERE type='table'"
           );

            while (rs.next()) {
                System.out.println("Table: " + rs.getString("name"));
            }

            rs.close();
            System.out.println("Connected to database successfully!");
            System.out.println("Users, items, claims and messages tables are ready!");        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
