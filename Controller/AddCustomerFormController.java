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
 * @author Skyler McCracken
 * Controller for the form to add new customers to the database
 */
public class AddCustomerFormController implements Initializable {

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
    private ComboBox<Country> countryBox;
    @FXML
    private ComboBox<FirstLevelDivision> regionBox;
    @FXML
    private TextField postalField;
    @FXML
    private TextField phoneField;

    Stage stage;
    Parent scene;

    /**
     * Handles the action event of the Exit Button being clicked by
     * loading and setting the Scene of the View Customers Form
     * @param actionEvent
     * actionEvent caused by clicking the Exit Button
     * @throws IOException
     * IOException for potential file name issues.
     */
    public void onActionExit(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/ViewCustomersForm.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Handles the action event of the Save Customer Buttion being clicked
     * by creating a new Customer using the field information and box selected,
     * then executing the DatabaseQuery statement to save the new Customer to the Database,
     * lastly it loads the View Customers Form
     * @param actionEvent
     * actionEvent caused by clicking the save new Customer Button.
     * @throws SQLException
     * Potential SQLException if the SQL statement isn't formatted correctly
     * @throws IOException
     * Potential IOException by loading file name.
     */
    public void onActionSaveCustomer(ActionEvent actionEvent) throws SQLException, IOException {
        try{
            newCustomer.setName(nameField.getText());
            newCustomer.setAddress(addressField.getText());
            newCustomer.setPostalCode(postalField.getText());
            newCustomer.setPhone(phoneField.getText());
            FirstLevelDivision tempFLD = (FirstLevelDivision) regionBox.getSelectionModel().getSelectedItem();
            newCustomer.setDivisionID(tempFLD.getId());
            String statement = newCustomer.createCustomerStatement();
            DatabaseQuery.getStatement().execute(statement);

            stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/ViewCustomersForm.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();

        }catch (NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Please enter a valid input");
            alert.showAndWait();
        }

    }

    /**
     * Overridden initialize method to load the correct subsets of Countries in the ComboBox
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String statement = "Select * FROM WJ07NyX.countries;";
        try {
            DatabaseQuery.getStatement().execute(statement);
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

