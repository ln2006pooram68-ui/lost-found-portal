import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateVerificationVotesTable {

    public static void main(String[] args) {
        String sql = "CREATE TABLE IF NOT EXISTS verification_votes ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "item_id INTEGER NOT NULL, "
                   + "user_id INTEGER NOT NULL, "
                   + "vote TEXT NOT NULL "
                   + "CHECK (vote IN ('GENUINE', 'SUSPICIOUS')), "
                   + "UNIQUE (item_id, user_id))";

        try (Connection con = DBConnection.getInstance().getConnection();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Verification votes table created successfully!");

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}
