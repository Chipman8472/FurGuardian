package ca.furguardian.it.petwellness.ui.records;
//Justin Chipman - N01598472
//Imran Zafurallah - N01585098
//Zane Aransevia - N01351168
//Tevadi Brookes - N01582563
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

import java.util.Locale;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.model.PetModel;

public class AddRecordDialogFragment extends DialogFragment {

    private DatePicker recordDatePicker;
    private EditText recordTypeEditText, recordDetailsEditText;
    private PetModel databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_medical_record_form, container, false);

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
        MedicalRecord record = new MedicalRecord(date, type, details);
        databaseHelper.addRecord(record, date, new PetModel.OnRecordOperationListener() {
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