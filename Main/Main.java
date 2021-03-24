package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Data.DatabaseConn;
import Data.DatabaseQuery;

import java.sql.SQLException;


/**
 * Main Method to begin running the application.
 * @author Skyler McCracken
 */
public class Main extends Application {

    /**
     * Overridden method that loads the initial Login Form UI
     * @param primaryStage
     * @throws Exception
     * Exception due to potential file load issue.
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/View/loginForm.fxml"));
        primaryStage.setTitle("User Access");
        primaryStage.setScene(new Scene(root, 505, 175));
        primaryStage.show();
    }

    /**
     * Main method that calls and initites the database connection and load initial uti
     * @param args
     * @throws SQLException
     * SQLException due to potential issues connecting to the Database.
     */
    public static void main(String[] args) throws SQLException {
        DatabaseConn.startConnection();
        DatabaseQuery.setStatement(DatabaseConn.getConn());
        launch(args);
        DatabaseConn.closeConnection();
    }
}
