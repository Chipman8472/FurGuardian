package ca.furguardian.it.petwellness.ui.records;

public class MedicalRecord {
    private String date;
    private String type;
    private String details;

    public MedicalRecord() {
        // Default constructor required for Firebase
    }

    public MedicalRecord(String date, String type, String details) {
        this.date = date;
        this.type = type;
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getDetails() {
        return details;
    }
}

