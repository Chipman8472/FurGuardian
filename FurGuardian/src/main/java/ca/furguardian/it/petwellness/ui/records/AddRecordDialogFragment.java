package ca.furguardian.it.petwellness.ui.records;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.furguardian.it.petwellness.R;

public class AddRecordDialogFragment extends DialogFragment {

    private DatePicker recordDatePicker;
    private EditText recordTypeEditText, recordDetailsEditText;
    private Button addRecordButton;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_medical_record_form, container, false);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("pets").child("12334").child("records");

        // Initialize UI components
        recordDatePicker = rootView.findViewById(R.id.recordDatePicker);
        recordTypeEditText = rootView.findViewById(R.id.recordTypeEditText);
        recordDetailsEditText = rootView.findViewById(R.id.recordDetailsEditText);
        addRecordButton = rootView.findViewById(R.id.addRecordButton);

        addRecordButton.setOnClickListener(view -> {
            int day = recordDatePicker.getDayOfMonth();
            int month = recordDatePicker.getMonth() + 1;
            int year = recordDatePicker.getYear();

            String recordDate = String.format("%04d-%02d-%02d", year, month, day);
            String recordType = recordTypeEditText.getText().toString();
            String recordDetails = recordDetailsEditText.getText().toString();

            if (TextUtils.isEmpty(recordType) || TextUtils.isEmpty(recordDetails)) {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                uploadRecord(recordDate, recordType, recordDetails);
            }
        });

        return rootView;
    }

    private void uploadRecord(String date, String type, String details) {
        MedicalRecord record = new MedicalRecord(date, type, details);
        databaseReference.child(date).setValue(record).addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Record added successfully", Toast.LENGTH_SHORT).show();
            dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to add record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
