package ca.furguardian.it.petwellness.ui.records;

public class Record {
    private String title;
    private String summary;
    private String details;
    private boolean isExpanded;

    public Record(String title, String summary, String details) {
        this.title = title;
        this.summary = summary;
        this.details = details;
        this.isExpanded = false;
    }

    public String getTitle() {
        return title;
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
