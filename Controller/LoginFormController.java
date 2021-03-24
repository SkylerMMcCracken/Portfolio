package Controller;

import Data.DatabaseConn;
import Data.DatabaseQuery;
import Data.TimeConverter;
import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Controller for the Login Form
 * @author Skyler McCracken
 */
public class LoginFormController implements Initializable {
    Stage stage;
    Parent scene;
    private Locale defaultLocal = Locale.getDefault();
    private ResourceBundle resourceBundle;

    @FXML
    private TextField userField;
    @FXML
    private PasswordField passField;
    @FXML
    private Button exitButton;
    @FXML
    private Label locationLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Label passLabel;
    @FXML
    private Label userLabel;
    @FXML
    private Label titleLabel;

    /**
     * Handles the action event of clicking the login button by pulling the data in the userField and passField
     * and creating a SQL statement to see if user date in the database matches it and continues to the next form if successful.
     * Also writes to a txt file about the history and timing of login attempts.
     * Also searches through the appointments in the database for any within 15 minutes and informs the user of them if there are.
     * @param actionEvent
     * actionEvent caused by clicking the login button
     * @throws IOException
     * Potential IOException by loading the next form as well as the output txt file.
     */
    public void onActionLogin(ActionEvent actionEvent) throws IOException {
        String filename = "login_activity.txt";

        FileWriter fWriter = new FileWriter(filename, true);
        PrintWriter outputFile = new PrintWriter(fWriter);
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        outputFile.print("User: " + userField.getText() + " attempted to login at " + timeStamp + " and was ");
        try {
            String statement = "SELECT * from WJ07NyX.users WHERE users.User_Name = '" + userField.getText() + "' AND users.Password = '" + passField.getText() + "';";
            try {
                DatabaseQuery.getStatement().execute(statement);
                if (DatabaseQuery.getStatement().getResultSet().next()) {
                    outputFile.println("Successful");
                    outputFile.flush();
                    outputFile.close();
                    stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/mainForm.fxml"));
                    stage.setScene(new Scene(scene));
                    stage.show();
                    ObservableList<Appointment> allAppointments =  FXCollections.observableArrayList();
                    statement = "SELECT * FROM WJ07NyX.appointments;";
                    DatabaseQuery.getStatement().execute(statement);
                    ResultSet rs = DatabaseQuery.getStatement().getResultSet();
                    while(rs.next()){
                        Appointment tempAppointment = new Appointment();
                        tempAppointment.setId(rs.getInt("Appointment_ID"));
                        tempAppointment.setStartTime(rs.getTimestamp("Start").toLocalDateTime());
                        tempAppointment.setStartTimeString(rs.getTimestamp("Start").toLocalDateTime());
                        allAppointments.add(tempAppointment);
                    }
                    boolean apptFound = false;
                    for (int i = 0; i < allAppointments.size(); i++) {
                        if (TimeConverter.withinFifteen(allAppointments.get(i).getStartTime())){
                            apptFound = true;
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Upcoming Appointment;");
                            alert.setHeaderText(null);
                            alert.setContentText("Upcoming Appointment:\n" +
                                    "Appointment " + allAppointments.get(i).getId() + " at " + allAppointments.get(i).getStartTimeString());
                            alert.showAndWait();
                        }
                    }
                    if (!apptFound){
                        Alert alert = new Alert((Alert.AlertType.INFORMATION));
                        alert.setTitle("No Upcoming Appointments");
                        alert.setHeaderText(null);
                        alert.setContentText("No Appointments within 15 minutes.");
                        alert.showAndWait();
                    }
                }else {
                    outputFile.println("Not Successful");
                    outputFile.flush();
                    outputFile.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    if (defaultLocal.getLanguage().equals("fr")) {
                        alert.setTitle(resourceBundle.getString("loginerror"));
                        alert.setContentText(resourceBundle.getString("unknowncredentials"));
                    }
                    else {
                        alert.setTitle("Login Error");
                        alert.setContentText("Username or Password Unknown");
                    }
                    alert.showAndWait();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (NumberFormatException e){
            outputFile.println("Not Successful");
            outputFile.flush();
            outputFile.close();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            if (defaultLocal.getLanguage().equals("fr")) {
                alert.setTitle(resourceBundle.getString("error"));
                alert.setContentText(resourceBundle.getString("validInput"));
            }else {
                alert.setTitle("Error");
                alert.setContentText("Please enter a valid input");
            }
            alert.showAndWait();
        }
    }

    /**
     * Handles the action event of clicking on the exit button by closing the program.
     * @param actionEvent
     * actionEvent caused by clicking the Exit Button
     */
    public void onActionExit(ActionEvent actionEvent) {
        stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Overridden initialize method to load the login form information in the language determined by the PC's defaultLocal.
     * @param url
     * @param resourceBundle
     * resourceBundle to fetch potential language change.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            resourceBundle = ResourceBundle.getBundle("PropertyFiles/Nat", defaultLocal);
            this.resourceBundle = resourceBundle;
            if (defaultLocal.getLanguage().equals("fr")) {
                //stage.setTitle(resourceBundle.getString("useraccess"));
                titleLabel.setText(resourceBundle.getString("userlogin"));
                userLabel.setText(resourceBundle.getString("username"));
                userField.setPromptText(resourceBundle.getString("username"));
                passLabel.setText(resourceBundle.getString("password"));
                passField.setPromptText(resourceBundle.getString("password"));
                exitButton.setText(resourceBundle.getString("exit"));
                loginButton.setText(resourceBundle.getString("login"));
            }
        }catch (MissingResourceException e){
        }
        locationLabel.setText(defaultLocal.getCountry());

    }
}
