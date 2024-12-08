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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.UUID;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.model.PetModel;

public class AddRecordDialogFragment extends DialogFragment {

    private static final String ARG_PET_ID = "petId";
    private DatePicker recordDatePicker;
    private EditText recordTypeEditText, recordDetailsEditText;
    private PetModel databaseHelper;
    private String petId;

    // Static factory method to create a new instance of the dialog with arguments
    public static AddRecordDialogFragment newInstance(String petId) {
        AddRecordDialogFragment fragment = new AddRecordDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PET_ID, petId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_medical_record_form, container, false);

        // Get petId from arguments
        if (getArguments() != null) {
            petId = getArguments().getString(ARG_PET_ID);
        }

        if (petId == null || petId.isEmpty()) {
            Toast.makeText(getContext(), "Pet ID is missing. Cannot add record.", Toast.LENGTH_SHORT).show();
            dismiss();
            return rootView;
        }

        // Initialize Firebase helper
        databaseHelper = new PetModel();

        // Initialize UI components
        recordDatePicker = rootView.findViewById(R.id.recordDatePicker);
        recordTypeEditText = rootView.findViewById(R.id.recordTypeEditText);
        recordDetailsEditText = rootView.findViewById(R.id.recordDetailsEditText);
        Button addRecordButton = rootView.findViewById(R.id.addRecordButton);

        addRecordButton.setOnClickListener(view -> {
            int day = recordDatePicker.getDayOfMonth();
            int month = recordDatePicker.getMonth() + 1;
            int year = recordDatePicker.getYear();

            String recordDate = String.format(Locale.CANADA, "%04d-%02d-%02d", year, month, day);
            String recordType = recordTypeEditText.getText().toString();
            String recordDetails = recordDetailsEditText.getText().toString();

            if (TextUtils.isEmpty(recordType) || TextUtils.isEmpty(recordDetails)) {
                Toast.makeText(getContext(), R.string.all_fields_are_required, Toast.LENGTH_SHORT).show();
            } else {
                uploadRecord(recordDate, recordType, recordDetails);
            }
        });

        return rootView;
    }

    private void uploadRecord(String date, String type, String details) {
        // Generate a unique ID for the record
        String uid = UUID.randomUUID().toString();

        // Create a MedicalRecord object with the UID
        MedicalRecord record = new MedicalRecord(uid, date, type, details);

        // Add the record to the database
        databaseHelper.addRecord(record, petId, new PetModel.OnRecordOperationListener() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

