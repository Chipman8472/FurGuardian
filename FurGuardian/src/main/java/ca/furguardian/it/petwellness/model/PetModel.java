package ca.furguardian.it.petwellness.model;
//Justin Chipman - N01598472
//Imran Zafurallah - N01585098
//Zane Aransevia - N01351168
//Tevadi Brookes - N01582563
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.ui.records.MedicalRecord;

public class PetModel {

    private final DatabaseReference databaseReference;

    public PetModel() {
        // Initialize Firebase reference to the pet records node
        this.databaseReference = FirebaseDatabase.getInstance().getReference("pets").child("12334").child("records");
    }

    // Method to add a record
    public void addRecord(MedicalRecord record, String date, OnRecordOperationListener listener) {
        databaseReference.child(date).setValue(record)
                .addOnSuccessListener(aVoid -> listener.onSuccess(String.valueOf(R.string.record_added_successfully)))
                .addOnFailureListener(e -> listener.onFailure((R.string.failed_to_add_record) + e.getMessage()));
    }

    // Method to delete a record
    public void deleteRecord(String date, OnRecordOperationListener listener) {
        databaseReference.child(date).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess(String.valueOf(R.string.record_deleted_successfully)))
                .addOnFailureListener(e -> listener.onFailure((R.string.failed_to_delete_record) + e.getMessage()));
    }

    // Interface for success and failure callbacks
    public interface OnRecordOperationListener {
        void onSuccess(String message);

        void onFailure(String errorMessage);
    }
}

