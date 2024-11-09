package ca.furguardian.it.petwellness.ui.menu;

import static ca.furguardian.it.petwellness.controller.InputValidator.validateFeedbackInputs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.controller.InputValidator;
import ca.furguardian.it.petwellness.model.FeedbackModel;

public class FeedbackFragment extends Fragment {

    private EditText nameEditText, phoneEditText, emailEditText, commentEditText;
    private RatingBar ratingBar;
    private Button submitButton;
    private FeedbackModel feedbackModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        commentEditText = view.findViewById(R.id.commentEditText);
        ratingBar = view.findViewById(R.id.ratingBar);
        submitButton = view.findViewById(R.id.submitButton);

        // Initialize FeedbackModel
        feedbackModel = new FeedbackModel();

        // Set up submit button
        submitButton.setOnClickListener(v -> submitFeedback());

        return view;
    }

    private void submitFeedback() {
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String comment = commentEditText.getText().toString();
        float rating = ratingBar.getRating();

        if (InputValidator.validateFeedbackInputs(name, phone, email)) {
            // Use FeedbackModel to submit feedback
            feedbackModel.submitFeedback(name, phone, email, comment, rating, getContext());
        }else {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }


}
