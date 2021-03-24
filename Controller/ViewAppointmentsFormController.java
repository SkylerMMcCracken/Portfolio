package Controller;

import Model.Appointment;
import Data.DatabaseQuery;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller class for the viewAppointmentsForm FXML document.
 * @author SkylerMcCracken
 */
public class ViewAppointmentsFormController implements Initializable {

    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private LocalDateTime firstPoint;
    private LocalDateTime endPoint;
    @FXML
    private Button modifyAppointment;
    @FXML
    private ToggleGroup viewToggle;
    @FXML
    private TableView<Appointment> appointmentView;
    @FXML
    private TableColumn<Appointment, Integer> appIDCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;
    @FXML
    private TableColumn<Appointment, String> descriptionCol;
    @FXML
    private TableColumn<Appointment, String> locationCol;
    @FXML
    private TableColumn<Appointment, String> contactCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private TableColumn<Appointment, String> startCol;
    @FXML
    private TableColumn<Appointment, String> endCol;
    @FXML
    private TableColumn<Appointment, Integer> custIDCol;

    Stage stage;
    Parent scene;

    /**
     * Handles the action event caused by the user clicking the monthly radio button by
     * establishing the current month, then filtering the data in the tableView if the
     * start date has the same Month.
     *
     * Uses lambda expressions as the most efficient way to search through the list of data and return
     * true if the data is matching.
     * @param actionEvent
     * actionEvent caused by clicking the Monthly radio button.
     */
    public void onActionFilterMonthly(ActionEvent actionEvent) {
        Month currentMonth = LocalDateTime.now().getMonth();
        FilteredList<Appointment> filteredData = new FilteredList<>(allAppointments);
        filteredData.setPredicate(row -> {

            Month rowMonth = row.getStartTime().getMonth();

            return rowMonth == currentMonth;
        });
        appointmentView.setItems(filteredData);
    }

    /**
     * Handles the action event caused by the user clicking the monthly radio button by
     * establishing the current day, then filtering the data in the tableView if the
     * start date is within 7 days of current.
     *
     * Uses lambda expressions as the most efficient way to search through the list of data and return
     * true if the data is matching.
     * @param actionEvent
     * actionEvent caused by clicking the Monthly radio button.
     */
    public void onActionFilterWeekly(ActionEvent actionEvent) {
        firstPoint = LocalDateTime.now();
        endPoint = firstPoint.plusDays(7);
        FilteredList<Appointment> filteredData = new FilteredList<>(allAppointments);
        filteredData.setPredicate(row -> {

            LocalDateTime rowDate = row.getStartTime();

            return rowDate.isAfter(firstPoint) && rowDate.isBefore(endPoint);
        });
        appointmentView.setItems(filteredData);
    }

    /**
     * Handles the action event of a user clicking the add appointment by loading the addAppointmentForm UI
     * @param actionEvent
     * actionEvent caused by clicking the add appointment button.
     * @throws IOException
     * IOException to catch potential issues loading fxml File.
     */
    public void onActionAddAppointment(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/addAppointmentForm.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Handles the action event of a user clicking the add appointment by loading the modifyAppointmentForm UI by
     * first ensuring that data is selected and prompting the user to select one if not.
     * @param actionEvent
     * actionEvent caused by clicking the modify appointment button.
     * @throws IOException
     * IOException to catch potential issues loading fxml File.
     */
    public void onActionModifyAppointment(ActionEvent actionEvent) throws IOException {
        if (appointmentView.getSelectionModel().getSelectedItem() != null){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/modifyAppointmentForm.fxml"));
            loader.load();

            ModifyAppointmentFormController MAController = loader.getController();
            MAController.sendAppointment(appointmentView.getSelectionModel().getSelectedItem());
            stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection Made");
            alert.setHeaderText(null);
            alert.setContentText("Please select an appointment to modify.");
            alert.showAndWait();
        }
    }

    /**
     * Handles the action event of a user clicking the delete button by
     * first ensuring that data is selected and prompting the user to select one if not.
     *
     * If there is something selected a confirmation message will loading informing the user
     * that this will remove the appointment from the database.
     * @param actionEvent
     * actionEvent caused by clicking the delete appointment button
     * @throws SQLException
     * SQLExceptoin to catch potential issues with SQL statements when deleting the item.
     */
    public void onActionDeleteAppointment(ActionEvent actionEvent) throws SQLException {
        if (appointmentView.getSelectionModel().getSelectedItem() != null) {
            Alert warning = new Alert(Alert.AlertType.CONFIRMATION);
            warning.setTitle("Confirm Delete Appointment");
            warning.setHeaderText("Confirm this action?");
            warning.setContentText("This will delete the appointment from the database. Would you like to continue?\n" +
                    "Appointment ID: " + appointmentView.getSelectionModel().getSelectedItem().getId() + "\nType: " +
                    appointmentView.getSelectionModel().getSelectedItem().getType());
            Optional<ButtonType> result = warning.showAndWait();
            if (result.get() == ButtonType.OK) {
                String statement = "DELETE FROM WJ07NyX.appointments WHERE Appointment_ID = " + appointmentView.getSelectionModel().getSelectedItem().getId() + ';';
                DatabaseQuery.getStatement().execute(statement);
                allAppointments.remove(appointmentView.getSelectionModel().getSelectedItem());
                appIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
                descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
                locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
                contactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
                typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
                startCol.setCellValueFactory(new PropertyValueFactory<>("startTimeString"));
                endCol.setCellValueFactory(new PropertyValueFactory<>("endTimeString"));
                custIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection Made");
            alert.setHeaderText(null);
            alert.setContentText("Please select an appointment to delete.");
            alert.showAndWait();
        }
    }

    /**
     * Handles the action event of the Exit Button being clicked by
     * loading and setting the Scene of the Main Form
     * @param actionEvent
     * actionEvent caused by clicking the Exit Button
     * @throws IOException
     * IOException for potential file name issues.
     */
    public void onActionExit(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/mainForm.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Overridden Initialize method that retrieves all appointment information and loads into a observable list.
     *
     * Then filters the appointments that fall within the same month as the default radio button selects.
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //Lambda expression used as the most efficient way to look through allAppointments data and return true if within the same month as current.
        appointmentView.setItems(allAppointments);
        Month currentMonth = LocalDateTime.now().getMonth();
        FilteredList<Appointment> filteredData = new FilteredList<>(allAppointments);
        filteredData.setPredicate(row -> {

            Month rowMonth = row.getStartTime().getMonth();

            return rowMonth == currentMonth;
        });
        appointmentView.setItems(filteredData);
        appIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTimeString"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTimeString"));
        custIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }
}
