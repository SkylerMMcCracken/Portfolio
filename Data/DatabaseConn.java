package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class to establish and maintain the connection to the database
 * @author SkylerMcCracken
 */
public class DatabaseConn {
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com/WJ07NyX";

    private static final String jdbcURL = protocol + vendorName + ipAddress;

    private static Connection conn = null;

    private static final String mySQLJDBCDriver= "com.mysql.cj.jdbc.Driver";
    private static final String username = "U07NyX";
    private static final String password = "53689077496";

    /**
     * Establishes a connection with the database using class login credentials.
     * then returns the connection object.
     * @return
     * Connection object if established.
     */
    public static Connection startConnection(){
        try{
            Class.forName(mySQLJDBCDriver);
            conn = DriverManager.getConnection(jdbcURL,username,password);
            System.out.println("Connection Successful");
        }catch (ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Closes the connection to the database.
     * @throws SQLException
     * SQLException for potential issue closing a connection.
     */
    public static void closeConnection() throws SQLException {
        conn.close();
        System.out.println("Connection Closed");
    }

    public static Connection getConn() {
        return conn;
    }

}
