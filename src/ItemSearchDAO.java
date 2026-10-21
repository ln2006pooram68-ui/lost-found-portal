import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class ItemSearchDAO {
    public Vector<Vector<Object>> searchItems(String name, String location) {
        Vector<Vector<Object>> rows = new Vector<Vector<Object>>();

        String sql = "SELECT id, name, description, location, item_date, type, status "
                + "FROM items WHERE name LIKE ? AND location LIKE ? ORDER BY id DESC";

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            ps.setString(2, "%" + location + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<Object>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("description"));
                row.add(rs.getString("location"));
                row.add(rs.getString("item_date"));
                row.add(rs.getString("type"));
                row.add(rs.getString("status"));
                rows.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
