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
        this.databaseReference = FirebaseDatabase.getInstance().getReference("users").child("pets").child("records");
    }

    public void addRecord(MedicalRecord record, String recordId, OnRecordOperationListener listener) {


        databaseReference.setValue(record)
                .addOnSuccessListener(aVoid -> listener.onSuccess("Record added successfully!"))
                .addOnFailureListener(e -> listener.onFailure("Failed to add record: " + e.getMessage()));
    }

    public void deleteRecord(String recordId, OnRecordOperationListener listener) {

        databaseReference.removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess("Record deleted successfully!"))
                .addOnFailureListener(e -> listener.onFailure("Failed to delete record: " + e.getMessage()));
    }


    // Interface for success and failure callbacks
    public interface OnRecordOperationListener {
        void onSuccess(String message);

        void onFailure(String errorMessage);
    }
}

