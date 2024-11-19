package ca.furguardian.it.petwellness.ui.records;
//Justin Chipman - N01598472
//Imran Zafurallah - N01585098
//Zane Aransevia - N01351168
//Tevadi Brookes - N01582563
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
