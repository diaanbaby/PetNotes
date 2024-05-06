package classes;

public class VetVisit {
    private String name;
    private String dateTime;

    public VetVisit(String name, String dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public String getDateTime() {
        return dateTime;
    }
    public static VetVisit newInstance(String name, String dateTime) {
        return new VetVisit(name, dateTime);
    }
}
