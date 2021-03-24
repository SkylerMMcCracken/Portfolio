package Model;

import Data.DatabaseQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Customer Object Class to modify data on the UI side before altering the Database.
 * @author Skyler McCracken
 */
public class Customer {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionID;
    private String division;

    public Customer(int id, String name, String address, String postalCode, String phoneNumber, int divisionID, String division) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phoneNumber;
        this.divisionID = divisionID;
        this.division = division;
    }

    public Customer() {
        this.id = 0;
        this.name = null;
        this.address = null;
        this.postalCode = null;
        this.phone = null;
    }


    public int getDivisionID() { return divisionID; }

    public void setDivisionID(int divisionID) { this.divisionID = divisionID; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDivision() { return division;}

    public void setDivision(String division) {this.division = division;}

    /**
     * Breaks down a sql statement of creating a new customer using class data.
     * @return
     * returns the SQL statement string
     */
    public String createCustomerStatement(){
        String dateStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        String statement = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Create_Date, Division_ID)VALUES(" +
                "'" + this.name + "', '" + this.address + "', '" + this.postalCode + "', '" + this.phone + "', '" + dateStamp + "', '" + this.divisionID + "');";

        return statement;
    }
    /**
     * Breaks down a sql statement of modifying a customer using class data.
     * @return
     * returns the SQL statement string
     */
    public String modifyCustomerStatement(){
        String statement = "UPDATE customers SET Customer_Name = '" + this.name + "', Address = '" + this.address + "', Postal_Code = '" + this.postalCode +
                "', Phone = '" + this.phone +"', Division_ID = '" + this.divisionID +
                "' WHERE Customer_ID = '" + this.id + "';";
        return statement;
    }

    /**
     * Executes a sql statement for appointments with class ID, if there is data, return true, if not return false.
     * @return
     * Boolean based on info in database.
     * @throws SQLException
     * SQLException due to potential issue loading sql statements.
     */
    public Boolean hasCurrentAppointments() throws SQLException {
        String statement = "Select * FROM WJ07NyX.appointments WHERE Customer_ID = " + this.getId() + ";";
        DatabaseQuery.getStatement().execute(statement);
        ResultSet rs = DatabaseQuery.getStatement().getResultSet();
        return rs.next();
    }
}
