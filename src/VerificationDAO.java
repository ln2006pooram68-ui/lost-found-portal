import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerificationDAO {

    public static final int MIN_VOTES = 3;
    public static final double MIN_SCORE = 60.0;

    public boolean saveVote(int itemId, int userId, String vote) {
        if (vote == null ||
            (!vote.equals("GENUINE") && !vote.equals("SUSPICIOUS"))) {
            System.out.println("Invalid vote. Use GENUINE or SUSPICIOUS.");
            return false;
        }

        String sql = "INSERT INTO verification_votes "
                   + "(item_id, user_id, vote) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ps.setInt(2, userId);
            ps.setString(3, vote);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Vote could not be saved: " + e.getMessage());
            return false;
        }
    }

    public int getVoteCount(int itemId) {
        String sql = "SELECT COUNT(*) FROM verification_votes WHERE item_id = ?";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getCredibilityScore(int itemId) {
        String sql = "SELECT "
                   + "COUNT(CASE WHEN vote = 'GENUINE' THEN 1 END) AS genuine_count, "
                   + "COUNT(*) AS total_count "
                   + "FROM verification_votes WHERE item_id = ?";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int genuine = rs.getInt("genuine_count");
                    int total = rs.getInt("total_count");

                    if (total == 0) {
                        return 0.0;
                    }

                    return genuine * 100.0 / total;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }
    
public int getTotalVotes(int itemId) {

    String sql = "SELECT COUNT(*) AS total_count "
            + "FROM verification_votes WHERE item_id = ?";

    try (Connection con =
                DBConnection.getInstance().getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql)) {

        pstmt.setInt(1, itemId);

        try (java.sql.ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total_count");
            }
        }

    } catch (SQLException e) {
        System.out.println("Error counting votes:");
        e.printStackTrace();
    }

    return 0;
}


    public String getVerificationStatus(int itemId) {
        int votes = getVoteCount(itemId);

        if (votes < MIN_VOTES) {
            return "PENDING";
        }

        if (getCredibilityScore(itemId) >= MIN_SCORE) {
            return "ELIGIBLE";
        }

        return "FLAGGED";
    }
}