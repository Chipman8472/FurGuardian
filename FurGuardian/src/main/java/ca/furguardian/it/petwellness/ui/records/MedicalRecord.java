package ca.furguardian.it.petwellness.ui.records;

public class MedicalRecord {
    private String uid;
    private String date;
    private String type;
    private String details;

    public MedicalRecord() {
        // Required empty constructor for Firebase
    }

    public MedicalRecord(String uid, String date, String type, String details) {
        this.uid = uid;
        this.date = date;
        this.type = type;
        this.details = details;
    }

    public String getUid() {
        return uid;
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
