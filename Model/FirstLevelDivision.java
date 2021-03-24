package Model;

public class FirstLevelDivision {
    private int id;
    private String division;
    private int countryID;

    public FirstLevelDivision(int id, String division, int countryID) {
        this.id = id;
        this.division = division;
        this.countryID = countryID;
    }

    public FirstLevelDivision() {
        this.id = 0;
        this.division = null;
        this.countryID = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
    public String toString(){
        return getDivision();
    }
}
