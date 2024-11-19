package ca.furguardian.it.petwellness.ui.records;
//Justin Chipman - N01598472
//Imran Zafurallah - N01585098
//Zane Aransevia - N01351168
//Tevadi Brookes - N01582563
public class MedicalRecord {
    private String date;
    private String type;
    private String details;

    public MedicalRecord() {
        // Required empty constructor for Firebase
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