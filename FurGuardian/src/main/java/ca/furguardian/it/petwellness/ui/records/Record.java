package ca.furguardian.it.petwellness.ui.records;

public class Record {
    private String date;
    private String summary;
    private String details;
    private boolean isExpanded;

    public Record(String Date, String summary, String details) {
        this.date = Date;
        this.summary = summary;
        this.details = details;
        this.isExpanded = false;
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
