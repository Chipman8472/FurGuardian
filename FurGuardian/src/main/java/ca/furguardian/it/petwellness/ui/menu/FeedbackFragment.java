package ca.furguardian.it.petwellness.ui.menu;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import ca.furguardian.it.petwellness.R;

public class FeedbackFragment extends Fragment {

    private EditText nameEditText, phoneEditText, emailEditText, commentEditText;
    private RatingBar ratingBar;
    private TextView deviceModelTextView;
    private FirebaseDatabase db;
    private DatabaseReference feedbackRef;
    private String deviceModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        commentEditText = view.findViewById(R.id.commentEditText);
        ratingBar = view.findViewById(R.id.ratingBar);
        deviceModelTextView = view.findViewById(R.id.deviceModelTextView);

        // Initialize Firestore
        db = FirebaseDatabase.getInstance();
        feedbackRef= db.getReference("feedback");

        // Set device model and display it
        deviceModel = Build.MANUFACTURER + " " + Build.MODEL;
        deviceModelTextView.setText("Device Model: " + deviceModel);

        // Set up submit button
        view.findViewById(R.id.submitButton).setOnClickListener(v -> submitFeedback());

        return view;
    }

    private void submitFeedback() {
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String comment = commentEditText.getText().toString();
        float rating = ratingBar.getRating();

        if (validateInputs(name, phone, email)) {
            // Prepare feedback data
            Map<String, Object> feedback = new HashMap<>();
            feedback.put("name", name);
            feedback.put("phone", phone);
            feedback.put("email", email);
            feedback.put("comment", comment);
            feedback.put("rating", rating);
            feedback.put("deviceModel", deviceModel);

            // Add feedback data to Firestore under 'feedback' collection
            feedbackRef.child("feedback")
                    .setValue(feedback)
                    .addOnSuccessListener(documentReference ->
                            Toast.makeText(getContext(), "Thank you for your feedback!", Toast.LENGTH_LONG).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to submit feedback. Please try again.", Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private boolean validateInputs(String name, String phone, String email) {
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}