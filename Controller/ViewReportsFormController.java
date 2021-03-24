package Controller;

import Data.DatabaseQuery;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller class for the viewReportsForm FXML document.
 * @author Skyler McCracken
 */
public class ViewReportsFormController {
    Stage stage;
    Parent scene;


    private final String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    ObservableList<String> customerCountries = FXCollections.observableArrayList();

    /**
     * Handles the action event of the user selecting the Load Appointments Report by first loading all appointments types from the database.
     * Secondly, searches through the database through a count where matching types. Third does a count of each month of start dates. If the count is zero it does not
     * get reported. This data is then displayed to the user in a alert ui.
     * @param actionEvent
     * actionEvent caused by clicking the Load Appointment Reports button.
     */
    public void onActionLoadAppointmentReport(ActionEvent actionEvent) {
        ObservableList<String> appointmentTypes = FXCollections.observableArrayList();
        String statement;
        String reportString = "Appointments by Type:\n";
        try{
            statement = "SELECT DISTINCT Type FROM WJ07NyX.appointments;";
            DatabaseQuery.getStatement().execute(statement);
            ResultSet rs = DatabaseQuery.getStatement().getResultSet();
            while (rs.next()){
                appointmentTypes.add(rs.getString("Type"));
            }
            for (int i = 0; i < appointmentTypes.size(); i++){
                statement = "SELECT COUNT(Type) FROM  WJ07NyX.appointments WHERE Type = '" + appointmentTypes.get(i) + "';";
                DatabaseQuery.getStatement().execute(statement);
                ResultSet rs2 = DatabaseQuery.getStatement().getResultSet();
                while (rs2.next()) {
                    reportString = reportString + rs2.getInt("COUNT(TYPE)") + " " + appointmentTypes.get(i) + " appointments\n";
                }
            }
            reportString = reportString + "Appointments by Month:\n";
            for (int i = 1; i <= 12; i++){
                statement = "SELECT COUNT(Start) from WJ07NyX.appointments WHERE MONTH(Start) = " + i + ";";
                DatabaseQuery.getStatement().execute(statement);
                ResultSet rs3 = DatabaseQuery.getStatement().getResultSet();
                while (rs3.next()){
                    if (rs3.getInt("Count(Start)") > 0)
                    reportString = reportString + rs3.getInt("Count(Start)") + " " + months[i-1] +" Appointments\n";
                }
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Information");
        alert.setHeaderText(null);
        alert.setContentText(reportString);
        alert.showAndWait();
    }

    /**
     * Handles the action event of clicking the load contact schedules button by loading the ui
     * for the contact schedules fxml document.
     * @param actionEvent
     * actionEvent caused by clicking Contact Schedules button.
     * @throws IOException
     * IOException for potential file name issues.
     */
    public void onActionLoadContactSchedules(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/contactSchedules.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Handles the action event of the user clicking the Customer Report button by first,
     * pulling all customers from the database into an Observable list, then for each customer,
     * checks the database for active appointments. Method then keeps track and reports of any
     * customers that do not have active appointments, or reports that all customers have active appointments.
     * @param actionEvent
     */
    public void onActionLoadCustomerReport(ActionEvent actionEvent){
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        String reportStatement = "Customers without scheduled appointments: \n";
        String statement = "SELECT * FROM WJ07NyX.customers";
        int count = 0;
        try{
            DatabaseQuery.getStatement().execute(statement);
            ResultSet rs = DatabaseQuery.getStatement().getResultSet();
            while (rs.next()){
                Customer newCust = new Customer();
                newCust.setAddress(rs.getString("Address"));
                newCust.setId(rs.getInt("Customer_ID"));
                newCust.setName(rs.getString("Customer_Name"));
                newCust.setPhone(rs.getString("Phone"));
                newCust.setPostalCode(rs.getString("Postal_Code"));
                newCust.setDivisionID(rs.getInt("Division_ID"));

                allCustomers.add(newCust);
            }
            for (int i = 0; i < allCustomers.size(); i++){
                statement = "SELECT * FROM WJ07NyX.appointments WHERE Customer_ID = " + allCustomers.get(i).getId() + ";";
                DatabaseQuery.getStatement().execute(statement);
                if (!DatabaseQuery.getStatement().getResultSet().next()){
                    reportStatement = reportStatement + "Customer ID: " +allCustomers.get(i).getId() + "\n";
                    count++;
                }
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        if (count > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Customer Information");
            alert.setHeaderText(null);
            alert.setContentText(reportStatement);
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Information");
            alert.setHeaderText(null);
            alert.setContentText("All Customers currently have scheduled appointments");
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
}
