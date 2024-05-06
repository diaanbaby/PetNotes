package classes;

public class Vaccination {
    private String name;
    private String dateTime;

    public Vaccination(String name, String dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public static Vaccination newInstance(String name, String dateTime) {
        return new Vaccination(name, dateTime);
    }


}
