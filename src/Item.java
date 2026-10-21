
public class Item {

    private int id;
    private String name;
    private String description;
    private String location;
    private String itemDate;
    private String type;
    private String status;
    private int reporterId;

    public Item(String name, String description,
                String location, String itemDate,
                String type, int reporterId) {

        this.name = name;
        this.description = description;
        this.location = location;
        this.itemDate = itemDate;
        this.type = type;
        this.reporterId = reporterId;
        this.status = "PENDING";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getItemDate() {
        return itemDate;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public int getReporterId() {
        return reporterId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
