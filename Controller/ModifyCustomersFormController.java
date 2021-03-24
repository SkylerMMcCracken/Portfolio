package Controller;

import Data.DatabaseQuery;
import Model.Country;
import Model.Customer;
import Model.FirstLevelDivision;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller class for the ModifyCustomers Form
 * @author Skyler McCracken
 */
public class ModifyCustomersFormController {

    private Customer newCustomer = new Customer();
    private ObservableList<FirstLevelDivision> regionList = FXCollections.observableArrayList();
    private ObservableList<Country> countryList = FXCollections.observableArrayList();
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private ComboBox countryBox;
    @FXML
    private ComboBox regionBox;
    @FXML
    private TextField postalField;
    @FXML
    private TextField phoneField;

    Stage stage;
    Parent scene;

    /**
     * Handles the Action Event of clicking the Save Customer Button by first pulling the data from the available
     * fields and creating a new Customer class. Executes a SQL statement from the DatabaseQuery class to alter
     * the selected Customer Information in the Database. Then loads the ViewCustomersForm
     * @param actionEvent
     * actionEvent caused by clicking the Save Customer Button
     * @throws SQLException
     * Potential SQLException for incorrectly formatted SQL statement.
     * @throws IOException
     * Potential IOException caused by loading new FXML file.
     */
    public void onActionSaveCustomer(ActionEvent actionEvent) throws SQLException, IOException {
        try{
            newCustomer.setName(nameField.getText());
            newCustomer.setAddress(addressField.getText());
            newCustomer.setPostalCode(postalField.getText());
            newCustomer.setPhone(phoneField.getText());
            FirstLevelDivision tempFLD = (FirstLevelDivision) regionBox.getSelectionModel().getSelectedItem();
            newCustomer.setDivisionID(tempFLD.getId());
            String statement = newCustomer.modifyCustomerStatement();
            DatabaseQuery.getStatement().execute(statement);

            stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/ViewCustomersForm.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();

        }catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Please enter a valid input");
            alert.showAndWait();
        }

    }

    /**
     * Handles the action event caused by clicking the exit button by loading the ViewCustomersForm
     * @param actionEvent
     * actionEvent caused by clicking the Exit Button
     * @throws IOException
     * Potential IOException caused by loading new FXML file.
     */
    public void onActionExit(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/ViewCustomersForm.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Helper class to load the selected customer data from the Table View in the View Customers Form
     * @param customer
     * Customer class customer is the selected line from the previous Table View
     */
    public void sendCustomer(Customer customer){
        int countryID = 0;
        this.newCustomer = customer;
        String statementOne = "Select * FROM WJ07NyX.countries;";
        try {
            DatabaseQuery.getStatement().execute(statementOne);
            ResultSet rs = DatabaseQuery.getStatement().getResultSet();
            while (rs.next()){
                Country tempCountry = new Country();
                tempCountry.setId(rs.getInt("Country_ID"));
                tempCountry.setName(rs.getString("Country"));
                countryList.add(tempCountry);
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        countryBox.setItems(countryList);
        String statementTwo = "SELECT * FROM WJ07NyX.first_level_divisions WHERE Division_ID = " + newCustomer.getDivisionID() + ";";
        try{
            DatabaseQuery.getStatement().execute(statementTwo);
            ResultSet rs = DatabaseQuery.getStatement().getResultSet();
            while (rs.next()){
                countryID = rs.getInt("COUNTRY_ID");
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        String statementThree = "SELECT * FROM WJ07NyX.first_level_divisions where COUNTRY_ID = " + countryID + " ORDER BY Division;";
        try {
            DatabaseQuery.getStatement().execute(statementThree);
            ResultSet rs = DatabaseQuery.getStatement().getResultSet();
            while (rs.next()){
                FirstLevelDivision tempDivision = new FirstLevelDivision();
                tempDivision.setId(rs.getInt("Division_ID"));
                tempDivision.setDivision(rs.getString("Division"));
                tempDivision.setCountryID(rs.getInt("COUNTRY_ID"));
                regionList.add(tempDivision);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        regionBox.setItems(regionList);

        idField.setPromptText(String.valueOf(customer.getId()));
        nameField.setText(customer.getName());
        addressField.setText(customer.getAddress());
        countryBox.setValue(searchCountryByID(countryID));
        regionBox.setValue(searchFirstLevelDivisionByID(customer.getDivisionID()));
        postalField.setText(customer.getPostalCode());
        phoneField.setText(customer.getPhone());
    }

    /**
     * Helper class that returns the FirstLevelDivision with the matching ID
     * @param id
     * int id is the item searched for.
     * @return
     * returns the FirstLevelDivision with the matching ID or null if no match is found.
     */
    public FirstLevelDivision searchFirstLevelDivisionByID (int id){
        for (int i = 0; i < regionList.size(); i++){
            if (regionList.get(i).getId() == id) return regionList.get(i);
        }
        return null;
    }
    /**
     * Helper class that returns the Country with the matching ID
     * @param id
     * int id is the item searched for.
     * @return
     * returns the Country with the matching ID or null if no match is found.
     */
    public Country searchCountryByID (int id){
        for (int i = 0; i < countryList.size(); i++) {
            if (countryList.get(i).getId() == id) return countryList.get(i);
        }
        return null;
    }

    /**
     * Handles the action event caused by selecting a different Country by searching the database
     * for all FirstLevelDivisions with the matching Country_ID and loading the Region ComboBox with that subset.
     * @param actionEvent
     * actionEvent caused by selecting a country in the Country ComboBox
     */
    public void onActionCountrySelected(ActionEvent actionEvent) {
        regionBox.getItems().clear();
        Country tempCountry = (Country) countryBox.getSelectionModel().getSelectedItem();
        String statement = "SELECT * FROM WJ07NyX.first_level_divisions where Country_ID = " + tempCountry.getId() + " ORDER BY Division;";
        try {
            DatabaseQuery.getStatement().execute(statement);
            ResultSet rs = DatabaseQuery.getStatement().getResultSet();
            while (rs.next()){
                FirstLevelDivision tempDivision = new FirstLevelDivision();
                tempDivision.setId(rs.getInt("Division_ID"));
                tempDivision.setDivision(rs.getString("Division"));
                tempDivision.setCountryID(rs.getInt("COUNTRY_ID"));
                regionList.add(tempDivision);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        regionBox.setItems(regionList);
    }
}
