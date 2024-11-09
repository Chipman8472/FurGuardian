package ca.furguardian.it.petwellness.model;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FeedbackModel {

    private final DatabaseReference feedbackRef;

    public FeedbackModel() {
        // Initialize Firebase reference for feedback
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        feedbackRef = database.getReference("feedback");
    }

    // Method to submit feedback
    public void submitFeedback(String name, String phone, String email, String comment, float rating, Context context) {
        // Prepare feedback data
        String deviceModel = Build.MODEL;
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("name", name);
        feedback.put("phone", phone);
        feedback.put("email", email);
        feedback.put("comment", comment);
        feedback.put("rating", rating);
        feedback.put("deviceModel", deviceModel);

        // Add feedback data to Firebase
        feedbackRef.push().setValue(feedback)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_LONG).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to submit feedback. Please try again.", Toast.LENGTH_SHORT).show()
                );
    }
}

