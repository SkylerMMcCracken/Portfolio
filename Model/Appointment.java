package Model;

import Controller.LoginFormController;
import Data.DatabaseConn;
import Data.TimeConverter;

import java.sql.Date;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * Appointment class to handle data on the ui side before altering the datbase.
 * @author Skyler McCracken
 */
public class Appointment {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private int id;
    private String title;
    private String description;
    private String location;
    private int contactID;
    private String contactName;
    private String type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startTimeString;
    private String endTimeString;
    private int customerID;
    private String customer;
    private int userID;

    public Appointment(int id,
                       String title,
                       String description,
                       String location,
                       String contact,
                       String type,
                       LocalDateTime startTime,
                       LocalDateTime endTime,
                       String customer,
                       int userID,
                       int contactID,
                       int customerID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactName = contact;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customer = customer;
        this.userID = userID;
        this.contactID = contactID;
        this.customerID = customerID;
        this.startTimeString = startTime.format(formatter);
        this.endTimeString = endTime.format(formatter);

    }

    public Appointment() {
        this.id = 0;
        this.title = null;
        this.description = null;
        this.location = null;
        this.contactName = null;
        this.type = null;
        this.startTime = null;
        this.endTime = null;
        this.customer = null;
        this.userID = 0;
        this.customerID = 0;
        this.contactID = 0;
        this.startTimeString = "";
        this.endTimeString = "";

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contact) {
        this.contactName = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public void setStartTimeString(LocalDateTime startTime) {
        LocalDateTime localStart = TimeConverter.utcTOlocal(startTime);
        this.startTimeString = localStart.format(formatter);
    }

    public String getEndTimeString() {
        return endTimeString;
    }

    public void setEndTimeString(LocalDateTime endTime) {
        LocalDateTime localEnd = TimeConverter.utcTOlocal(endTime);
        this.endTimeString = localEnd.format(formatter);
    }

    /**
     * Breaks down a sql statement of creating a new appointment using class data.
     * @return
     * returns the SQL statement string
     */
    public String createAppointmentStatement() {
        return "INSERT INTO WJ07NyX.appointments(Title, Description, Location, Type, Start, End, Customer_ID, Contact_ID, User_ID)VALUES('" +
                this.getTitle() + "', '" + this.getDescription() + "', '" + this.getLocation() + "', '" + this.getType() + "', '" + this.getStartTime().format(formatter) +
                "', '" + this.getEndTime().format(formatter) + "', '" + this.getCustomerID() + "', '" + this.getContactID() + "', '" + this.getUserID() + "');";
    }

    /**
     * Breaks down a sql statement of updating an appointment using class data.
     * @return
     * returns the SQL statement string
     */
    public String modifyAppointmentStatement() {
        return "UPDATE WJ07NyX.appointments SET Title = '" + this.getTitle() + "', Description = '" + this.getDescription() + "', Location = '" + this.getLocation() +
                "', Type = '" + this.getType() + "', Start = '" + this.getStartTime().format(formatter) + "', End = '" + this.getEndTime().format(formatter) +
                "', Customer_ID = '" + this.getCustomerID() + "', Contact_ID = '" + this.getCustomerID() + "', User_ID = '"+ this.getUserID() + "' WHERE Appointment_ID = '" + this.getId() + "';";
    }
}
