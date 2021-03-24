package Data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Query class to handle sql statements ran to the server.
 * @author Skyler McCracken
 */
public class DatabaseQuery {

    private static Statement statement;

    public static void setStatement(Connection conn) throws SQLException {
        statement = conn.createStatement();
    }

    public static Statement getStatement() {
        return statement;
    }
}
