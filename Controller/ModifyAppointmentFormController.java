package Controller;

import Data.DatabaseQuery;
import Data.TimeConverter;
import Model.Appointment;
import Model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.Chronology;

/**
 * Controller class for the modifyAppointmentForm FXML document.
 * @author Skyler McCracken
 */
public class ModifyAppointmentFormController {


    Appointment newAppointment = new Appointment();
    private ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    ObservableList<LocalTime> availableTimes = FXCollections.observableArrayList();
    private boolean startChange = false;
    private boolean endChange = false;

    @FXML
    private TextField appIDField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField locationField;
    @FXML
    private ComboBox<Contact> contactBox;
    @FXML
    private TextField typeField;
    @FXML
    private TextField custField;
    @FXML
    private DatePicker startDate;
    @FXML
    private ComboBox<LocalTime> startTime;
    @FXML
    private DatePicker endDate;
    @FXML
    private ComboBox<LocalTime> endTime;
    @FXML
    private TextField userField;


    Stage stage;
    Parent scene;

    /**
     * Handles the action event caused by the user clicking the save new appointment button by
     * first checking if any times of the appointment were changed. And keeping the newAppointment times the same
     * if not.
     *
     * Then goes through the logic validation first, ensuring that the
     * customer ID is within the database for foreign key dependencies. Secondly, validates
     * the logic of an appointment start being before an appointment end. Third, searches the database
     * to see if there is any overlap in customer appointment times. If all data is valid, creates
     * a Appointment Object then executes the statement to create a new appointment in the database.
     * @param actionEvent
     * actionEvent caused by clicking the save button.
     * @throws IOException
     * IOException to catch potential issues loading fxml File.
     */
    public void onActionSaveAppointment(ActionEvent actionEvent) throws IOException {
        try {
            String statement = "SELECT * FROM WJ07NyX.customers WHERE Customer_ID = " + custField.getText() + ";";
            DatabaseQuery.getStatement().execute(statement);
            try {
                if (!DatabaseQuery.getStatement().getResultSet().next()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Customer ID Issue");
                    alert.setHeaderText(null);
                    alert.setContentText("No Matching Customer with ID listed in Database");
                    alert.showAndWait();
                } else {
                    statement = "SELECT * FROM WJ07NyX.users WHERE User_ID = " + userField.getText() + ";";
                    DatabaseQuery.getStatement().execute(statement);
                    if (!DatabaseQuery.getStatement().getResultSet().next()) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("User ID Issue");
                        alert.setHeaderText(null);
                        alert.setContentText("No Matching User with ID listed in Database");
                        alert.showAndWait();
                    } else {
                        LocalDateTime appointmentStart;
                        LocalDateTime appointmentEnd;
                        if (startChange)
                            appointmentStart = LocalDateTime.of(startDate.getValue(), startTime.getValue());
                        else
                            appointmentStart = LocalDateTime.of(newAppointment.getStartTime().toLocalDate(), startTime.getValue());
                        if (endChange)
                            appointmentEnd = LocalDateTime.of(endDate.getValue(), endTime.getValue());
                        else
                            appointmentEnd = LocalDateTime.of(newAppointment.getEndTime().toLocalDate(), endTime.getValue());

                        if (appointmentStart.isAfter(appointmentEnd)) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Time Issue");
                            alert.setHeaderText(null);
                            alert.setContentText("Please Ensure the start time is before the end time");
                            alert.showAndWait();
                        } else {
                            statement = "SELECT * FROM WJ07NyX.appointments WHERE Customer_ID = " + custField.getText() + ";";
                            DatabaseQuery.getStatement().execute(statement);
                            ResultSet rs = DatabaseQuery.getStatement().getResultSet();
                            boolean overlap = false;
                            int overlapID = 0;
                            while (rs.next()) {
                                overlapID = rs.getInt("Appointment_ID");
                                LocalDateTime tempAppointmentStart = TimeConverter.utcTOlocal(rs.getTimestamp("Start").toLocalDateTime());
                                LocalDateTime tempAppointmentEnd = TimeConverter.utcTOlocal(rs.getTimestamp("End").toLocalDateTime());
                                if ((appointmentStart.isAfter(tempAppointmentStart) && appointmentStart.isBefore(tempAppointmentEnd)) || (appointmentEnd.isAfter(tempAppointmentStart)
                                        && appointmentEnd.isBefore(tempAppointmentEnd) || appointmentStart.isEqual(tempAppointmentStart) || appointmentEnd.isEqual(tempAppointmentEnd))) {
                                    if (newAppointment.getId() != overlapID) {
                                        overlap = true;
                                        overlapID = rs.getInt("Appointment_ID");
                                    }
                                }
                            }
                            if (appointmentEnd.isAfter(appointmentStart.plusHours(14))) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Date Issue");
                                alert.setHeaderText(null);
                                alert.setContentText("Please ensure that the appointment falls within a single business day.");
                                alert.showAndWait();
                            } else {
                                if (!overlap) {
                                    try {
                                        newAppointment.setTitle(titleField.getText());
                                        newAppointment.setContactID(contactBox.getSelectionModel().getSelectedItem().getId());
                                        newAppointment.setLocation(locationField.getText());
                                        newAppointment.setType(typeField.getText());
                                        newAppointment.setDescription(descriptionField.getText());
                                        newAppointment.setCustomerID(Integer.parseInt(custField.getText()));
                                        newAppointment.setStartTime(TimeConverter.localToUTC(appointmentStart));
                                        newAppointment.setEndTime(TimeConverter.localToUTC(appointmentEnd));
                                        newAppointment.setUserID(Integer.parseInt(userField.getText()));
                                        DatabaseQuery.getStatement().execute(newAppointment.modifyAppointmentStatement());

                                        stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                                        scene = FXMLLoader.load(getClass().getResource("/view/viewAppointmentsForm.fxml"));
                                        stage.setScene(new Scene(scene));
                                        stage.show();

                                    } catch (NumberFormatException e) {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("ID Format");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Please enter only integer values for the Customer ID or User ID");
                                        alert.showAndWait();
                                    }
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Customer Overlap");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Customer ID: " + custField.getText() + " already has an appointment scheduled at that time\n" +
                                            "with appointment ID: " + overlapID);
                                    alert.showAndWait();
                                }
                            }
                        }
                    }
                }
            }catch (NullPointerException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Date/Time Issue");
                alert.setHeaderText(null);
                alert.setContentText("Please ensure all dates and times are selected");
                alert.showAndWait();
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Form Issue");
            alert.setHeaderText(null);
            alert.setContentText("Please ensure all forms are filled.");
            alert.showAndWait();
        }
    }

    /**
     * Handles the action event of the user clicking the exit button by loading the View Appointments Form
     * @param actionEvent
     * actionEvent caused by clicking the exit button.
     * @throws IOException
     * IOException to catch potential issues loading fxml File.
     */
    public void onActionExit(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/viewAppointmentsForm.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Helper class to load the UI with prepopulated data from the param appointment
     * @param appointment
     * Appointment object from the selected data in the ViewAppointments Form
     */
    public void sendAppointment(Appointment appointment){
        this.newAppointment = appointment;
        String statement = "Select * FROM WJ07NyX.contacts;";
        try{
            DatabaseQuery.getStatement().execute(statement);
            ResultSet rs = DatabaseQuery.getStatement().getResultSet();
            while (rs.next()){
                Contact tempContact = new Contact();
                tempContact.setEmail(rs.getString("Email"));
                tempContact.setName(rs.getString("Contact_Name"));
                tempContact.setId(rs.getInt("Contact_ID"));
                allContacts.add(tempContact);
            }
            contactBox.setItems(allContacts);

            LocalDateTime start = TimeConverter.estTOlocal(LocalDateTime.of(LocalDate.now(), LocalTime.of(8,0)));
            LocalDateTime end = start.plusHours(14);

            while (start.isBefore(end.minusMinutes(14))){
                availableTimes.add(start.toLocalTime());
                start = start.plusMinutes(15);
            }
            startTime.setItems(availableTimes);
            availableTimes.add(end.toLocalTime());
            endTime.setItems(availableTimes);

            appIDField.setPromptText(String.valueOf(newAppointment.getId()));
            titleField.setText(newAppointment.getTitle());
            descriptionField.setText(newAppointment.getDescription());
            locationField.setText(newAppointment.getLocation());
            contactBox.getSelectionModel().select(searchContactByID(newAppointment.getContactID()));
            typeField.setText(newAppointment.getType());
            startDate.setPromptText(TimeConverter.promptString(newAppointment.getStartTime()));
            startTime.getSelectionModel().select(newAppointment.getStartTime().toLocalTime());
            endDate.setPromptText(TimeConverter.promptString(newAppointment.getEndTime()));
            endTime.getSelectionModel().select(newAppointment.getEndTime().toLocalTime());
            custField.setText(String.valueOf(newAppointment.getCustomerID()));
            userField.setText(String.valueOf(newAppointment.getUserID()));
        }catch (SQLException throwable){
            throwable.printStackTrace();
        }
    }

    /**
     * Help class to search through the list of contacts and return the object
     * that has the matching id.
     * @param id
     * Int id to search for matching id within the list of contacts.
     * @return
     * Returns a Contact object with the matching ID.
     */
    public Contact searchContactByID(int id){
        for (int i = 0; i < allContacts.size(); i++) {
            if (allContacts.get(i).getId() == id) return allContacts.get(i);
        }
        return null;
    }

    /**
     * Handles the actionEvent of the user selecting a new start date by
     * changing the class boolean of startChange to true.
     * @param actionEvent
     * actionEvent caused by the user selecting a start date.
     */
    public void onActionStartChange(ActionEvent actionEvent) {
        startChange = true;
    }
    /**
     * Handles the actionEvent of the user selecting a new end date by
     * changing the class boolean of endChange to true.
     * @param actionEvent
     * actionEvent caused by the user selecting a end date.
     */
    public void onActionEndChange(ActionEvent actionEvent) {
        endChange = true;
    }
}
