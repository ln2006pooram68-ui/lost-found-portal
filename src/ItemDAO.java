
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemDAO {

    public boolean saveItem(Item item) {

        String sql = "INSERT INTO items "
                + "(name, description, location, item_date, "
                + "type, status, reporter_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setString(3, item.getLocation());
            ps.setString(4, item.getItemDate());
            ps.setString(5, item.getType());
            ps.setString(6, item.getStatus());
            ps.setInt(7, item.getReporterId());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
public boolean markAsReturned(int itemId, int reporterId) {
    String sql = "UPDATE items SET status = 'RETURNED' "
            + "WHERE id = ? "
            + "AND reporter_id = ? "
            + "AND UPPER(type) = 'FOUND' "
            + "AND UPPER(status) = 'APPROVED'";

    try (
        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement ps = con.prepareStatement(sql)
    ) {
        ps.setInt(1, itemId);
        ps.setInt(2, reporterId);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}
