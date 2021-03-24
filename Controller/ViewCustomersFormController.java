package Controller;

import Data.DatabaseQuery;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller class for the View CustomersForm
 * @author Skyler McCracken
 */
public class ViewCustomersFormController implements Initializable {

    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> idCol;
    @FXML
    private TableColumn<Customer, String> nameCol;
    @FXML
    private TableColumn<Customer, String > addressCol;
    @FXML
    private TableColumn<Customer, String> phoneCol;
    @FXML
    private TableColumn<Customer, String> regionCol;
    Stage stage;
    Parent scene;

    /**
     * Handles the action event of clicking the Add Button by loading
     * and setting the new Scene of the addCustomers Form
     * @param actionEvent
     * actionEvent caused by clicking the addCustomers Button
     * @throws IOException
     * Potential IOException by loading a new FXML file.
     */
    public void onActionAddNewCustomer(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/addCustomerForm.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Handles the action event of clicking the Modify by first determining if there is a selected
     * Customer on the table view. If so creates a ModifyCustomersForm controller loading the data using the
     * sendCustomer Method. If not, informed the user to select a Customer.
     * @param actionEvent
     * actionEvent caused by clicking the Modify Button
     * @throws IOException
     * Potential IOException by loading a new FXML file.
     */
    public void onActionModifyCustomer(ActionEvent actionEvent) throws IOException {
        if (customerTable.getSelectionModel().getSelectedItem() != null){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/modifyCustomerForm.fxml"));
            loader.load();

            ModifyCustomersFormController MCController = loader.getController();
            MCController.sendCustomer(customerTable.getSelectionModel().getSelectedItem());
            stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();

        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection Made");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer to modify.");
            alert.showAndWait();
        }
    }
    /**
     * Handles the action event of clicking the Modify by first determining if there is a selected
     * Customer on the table view. If so searches the Database for active appointments with that Customer and informed the
     * user to remove those appointments before deleting selected customer. If no appointments are found, it removes the
     * customer from the Database and reloads the UI. If no customer is selected it will inform the user to select a Customer.
     * @param actionEvent
     * actionEvent caused by clicking the Modify Button
     * @throws SQLException
     * Potential SQLException by a formatted SQL statement
     */
    public void onActionDeleteCustomer(ActionEvent actionEvent) throws SQLException {
        if (customerTable.getSelectionModel().getSelectedItem() != null) {
            Customer tempCust = customerTable.getSelectionModel().getSelectedItem();
            if (tempCust.hasCurrentAppointments()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Appointment Conflict");
                alert.setContentText("This customer has an appointment currently scheduled. Please remove it before attempting to delete the customer.");
                alert.showAndWait();
            } else {
                String statement = "DELETE FROM WJ07NyX.customers WHERE Customer_ID = " + customerTable.getSelectionModel().getSelectedItem().getId() + ';';
                DatabaseQuery.getStatement().execute(statement);
                allCustomers.remove(tempCust);
                idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
                nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
                addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
                phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Selection Made");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer to delete.");
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
     * Overridden method to load the TableView with all Customers in the Database.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String statement = "SELECT * FROM WJ07NyX.customers";
        try {
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
        String statementTwo;
        for (int i = 0; i < allCustomers.size(); i++) {
            statementTwo = "SELECT * FROM WJ07NyX.first_level_divisions WHERE Division_ID = " + allCustomers.get(i).getDivisionID() + ";";
            DatabaseQuery.getStatement().execute(statementTwo);
            ResultSet rs2 = DatabaseQuery.getStatement().getResultSet();
            while (rs2.next()){
                String tempDivison = rs2.getString("Division");
                allCustomers.get(i).setDivision(tempDivison);
            }
        }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        customerTable.setItems(allCustomers);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        regionCol.setCellValueFactory(new PropertyValueFactory<>("division"));
    }
}
