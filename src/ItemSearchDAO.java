
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class ItemSearchDAO {

    public static final int MIN_VOTES = 3;
    public static final double MIN_SCORE = 60.0;

    private static final String SCORE_SQL =
            "100.0 * SUM(CASE WHEN v.vote = 'GENUINE' " +
            "THEN 1 ELSE 0 END) / COUNT(v.id)";

    private static final String BASE_COLUMNS =
            "i.id, i.name, i.description, i.location, " +
            "i.item_date, i.type, i.status";

    private static final String GROUP_COLUMNS =
            "i.id, i.name, i.description, i.location, " +
            "i.item_date, i.type, i.status";

    public Vector<Vector<Object>> searchItems(
            String name, String location) {

        Vector<Vector<Object>> rows = new Vector<Vector<Object>>();

        String sql =
                "SELECT " + BASE_COLUMNS + " " +
                "FROM items i " +
                "LEFT JOIN verification_votes v ON i.id = v.item_id " +
                "WHERE i.name LIKE ? " +
                "AND i.location LIKE ? " +
                "AND UPPER(i.status) <> 'REJECTED' " +
                "GROUP BY " + GROUP_COLUMNS + " " +
                "HAVING UPPER(i.status) = 'APPROVED' " +
                "OR COUNT(v.id) < " + MIN_VOTES + " " +
                "OR " + SCORE_SQL + " >= " + MIN_SCORE + " " +
                "ORDER BY i.id DESC";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            ps.setString(2, "%" + location + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(readItemRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    public Vector<Vector<Object>> getFlaggedItems() {

        Vector<Vector<Object>> rows = new Vector<Vector<Object>>();

        String sql =
                "SELECT " + BASE_COLUMNS + ", " +
                "COUNT(v.id) AS vote_count, " +
                SCORE_SQL + " AS score " +
                "FROM items i " +
                "JOIN verification_votes v ON i.id = v.item_id " +
                "WHERE UPPER(i.status) NOT IN ('APPROVED', 'REJECTED') " +
                "GROUP BY " + GROUP_COLUMNS + " " +
                "HAVING COUNT(v.id) >= " + MIN_VOTES + " " +
                "AND " + SCORE_SQL + " < " + MIN_SCORE + " " +
                "ORDER BY score ASC, i.id DESC";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = readItemRow(rs);
                row.add(rs.getInt("vote_count"));
                row.add(rs.getDouble("score"));
                rows.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    private Vector<Object> readItemRow(ResultSet rs)
            throws SQLException {

        Vector<Object> row = new Vector<Object>();

        row.add(rs.getInt("id"));
        row.add(rs.getString("name"));
        row.add(rs.getString("description"));
        row.add(rs.getString("location"));
        row.add(rs.getString("item_date"));
        row.add(rs.getString("type"));
        row.add(rs.getString("status"));

        return row;
    }
}
