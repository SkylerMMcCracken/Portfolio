package Controller;

import Data.DatabaseQuery;
import Model.Appointment;
import Model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * @author Skyler McCracken
 * Controller class for the Contact Schedules FXML Document.
 */
public class ContactSchedulesController implements Initializable {


    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private ObservableList<Contact> allContacts = FXCollections.observableArrayList();


    @FXML
    private ComboBox<Contact> contactBox;
    @FXML
    private TableView<Appointment> appointmentView;
    @FXML
    private TableColumn<Appointment, Integer> appIDCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;
    @FXML
    private TableColumn<Appointment, String> descriptionCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private TableColumn<Appointment, String> startCol;
    @FXML
    private TableColumn<Appointment, String> endCol;
    @FXML
    private TableColumn<Appointment, Integer> custCol;

    Stage stage;
    Parent scene;

    /**
     * Overridden Initialize method to ensure the UI loads all appointments in the Database to have ready
     * for the filtered view and loads all contacts in the Database for the ComboBox to select the contact.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String statement = "SELECT * FROM WJ07NyX.appointments";
        try {
            DatabaseQuery.getStatement().execute(statement);
            ResultSet rs = DatabaseQuery.getStatement().getResultSet();
            while (rs.next()){
                Appointment tempAppointment = new Appointment();
                tempAppointment.setId(rs.getInt("Appointment_ID"));
                tempAppointment.setTitle(rs.getString("Title"));
                tempAppointment.setDescription(rs.getString("Description"));
                tempAppointment.setLocation(rs.getString("Location"));
                tempAppointment.setType(rs.getString("Type"));
                tempAppointment.setStartTime(rs.getTimestamp("Start").toLocalDateTime());
                tempAppointment.setStartTimeString(rs.getTimestamp("Start").toLocalDateTime());
                tempAppointment.setEndTime(rs.getTimestamp("End").toLocalDateTime());
                tempAppointment.setEndTimeString(rs.getTimestamp("End").toLocalDateTime());
                tempAppointment.setCustomerID(rs.getInt("Customer_ID"));
                tempAppointment.setUserID(rs.getInt("User_ID"));
                tempAppointment.setContactID(rs.getInt("Contact_ID"));

                allAppointments.add(tempAppointment);
            }
            for (int i = 0; i < allAppointments.size(); i++) {
                statement = "SELECT Contact_Name FROM WJ07NyX.contacts WHERE Contact_ID = " + allAppointments.get(i).getContactID() +";";
                DatabaseQuery.getStatement().execute(statement);
                DatabaseQuery.getStatement().getResultSet().next();
                allAppointments.get(i).setContactName(DatabaseQuery.getStatement().getResultSet().getString("Contact_Name"));
            }
            statement = "SELECT * FROM WJ07NyX.contacts";
            DatabaseQuery.getStatement().execute(statement);
            ResultSet rs2 = DatabaseQuery.getStatement().getResultSet();
            while (rs2.next()){
                Contact tempContact = new Contact();
                tempContact.setId(rs2.getInt("Contact_ID"));
                tempContact.setName(rs2.getString("Contact_Name"));
                tempContact.setEmail(rs2.getString("Email"));
                allContacts.add(tempContact);
            }
            contactBox.setItems(allContacts);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Handles the action event of a user selecting a Contact in the Combo box
     * then filtering the table view with the appointments that have a matching Contact ID.
     *
     * Uses a lambda expression to efficiently search through each row in the appointments and return true
     * if matching Contact IDs.
     * @param actionEvent
     * actionEvent caused by selecting a contact in the ComboBox
     */
    public void onActionFilter(ActionEvent actionEvent) {
        FilteredList<Appointment> filteredData = new FilteredList<>(allAppointments);
        filteredData.setPredicate(row -> {
            int contactID = row.getContactID();

            return contactID == contactBox.getSelectionModel().getSelectedItem().getId();
        });
        appointmentView.setItems(filteredData);
        appIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTimeString"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTimeString"));
        custCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

    /**
     * Handles the action event of the user clicking the exit button by loading the View Reports Form
     * @param actionEvent
     * actionEvent caused by clicking the exit button.
     * @throws IOException
     * IOException to catch potential issues loading fxml File.
     */
    public void onActionExit(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/viewReportsForm.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }
}
