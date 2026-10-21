
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
    "jdbc:sqlite:" + new java.io.File("database/lostfound.db").getAbsolutePath();

    private static final DBConnection instance =
            new DBConnection();

    private DBConnection() {
    }

    public static DBConnection getInstance() {
        return instance;
    }

    public Connection getConnection() throws SQLException {
    System.out.println("Database location: " +
        new java.io.File("database/lostfound.db").getAbsolutePath());

    return DriverManager.getConnection(URL);
}
}
