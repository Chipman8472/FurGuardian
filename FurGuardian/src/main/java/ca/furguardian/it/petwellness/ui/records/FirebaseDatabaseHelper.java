package ca.furguardian.it.petwellness.ui.records;
//Justin Chipman - N01598472
//Imran Zafurallah - N01585098
//Zane Aransevia - N01351168
//Tevadi Brookes - N01582563
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseHelper {

    private DatabaseReference databaseReference;

    public FirebaseDatabaseHelper() {
        // Initialize Firebase reference to the pet records node
        this.databaseReference = FirebaseDatabase.getInstance().getReference("pets").child("12334").child("records");
    }

    // Method to add a record
    public void addRecord(MedicalRecord record, String date, OnRecordOperationListener listener) {
        databaseReference.child(date).setValue(record)
                .addOnSuccessListener(aVoid -> listener.onSuccess("Record added successfully"))
                .addOnFailureListener(e -> listener.onFailure("Failed to add record: " + e.getMessage()));
    }

    // Method to delete a record
    public void deleteRecord(String date, OnRecordOperationListener listener) {
        databaseReference.child(date).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess("Record deleted successfully"))
                .addOnFailureListener(e -> listener.onFailure("Failed to delete record: " + e.getMessage()));
    }

    // Interface for success and failure callbacks
    public interface OnRecordOperationListener {
        void onSuccess(String message);

        void onFailure(String errorMessage);
    }
}

