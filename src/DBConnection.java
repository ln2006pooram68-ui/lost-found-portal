
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlite:database/lostfound.db";

    private static final DBConnection instance =
            new DBConnection();

    private DBConnection() {
    }

    public static DBConnection getInstance() {
        return instance;
    }

    public Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(URL);
    }
}
