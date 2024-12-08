package ca.furguardian.it.petwellness.ui.records;

public class Record {
    private String id;
    private String date;
    private String summary;
    private String details;
    private boolean isExpanded;

    public Record(String id, String date, String summary, String details) {
        this.id = id;
        this.date = date;
        this.summary = summary;
        this.details = details;
        this.isExpanded = false;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getSummary() {
        return summary;
    }

    public String getDetails() {
        return details;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
