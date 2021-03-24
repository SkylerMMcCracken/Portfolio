package Controller;

import Data.DatabaseQuery;
import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

/**
 * Controller for the Main Form
 * @author SkylerMcCracken
 */
public class MainFormController {
    Stage stage;
    Parent scene;
    @FXML
    private Button exitButton;

    /**
     * Handles the action event of the user clicking the View Reports button by loading
     * the ViewReports UI
     * @param actionEvent
     * actionEvent caused by clicking on the View Reports Button
     * @throws IOException
     * IOException to catch potential issues loading fxml File.
     */
    public void onActionViewReports(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/viewReportsForm.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Handles the actionEvent of the user clicking the View Appointments button by
     * loading the View Appointments UI
     * @param actionEvent
     * actionEvent caused by clicking the View Appointments Button
     * @throws IOException
     * IOException to catch potential issues loading fxml File.
     */
    public void onActionViewAppointment(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/viewAppointmentsForm.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Handles the action event of clicking the View Customers Button by loading
     * and setting the new Scene of the View Customers Form
     * @param actionEvent
     * actionEvent caused by clicking the View Customers Button
     * @throws IOException
     * Potential IOException by loading a new FXML file.
     */
    public void onActionViewCustomers(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/viewCustomersForm.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
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

}
