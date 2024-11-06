package ca.furguardian.it.petwellness.ui.menu;

//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.ui.login.LoginActivity;

public class AccountFragment extends Fragment {

    private CheckBox checkBoxEditName, checkBoxEditPhone, checkBoxEditPassword;
    private EditText editTextName, editTextPhone, editTextNewPassword, editTextConfirmPassword;
    private Button buttonSaveChanges, buttonSignOut;
    private TextView textCurrentName, textCurrentPhone;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize UI elements
        checkBoxEditName = root.findViewById(R.id.checkBoxEditName);
        checkBoxEditPhone = root.findViewById(R.id.checkBoxEditPhone);
        checkBoxEditPassword = root.findViewById(R.id.checkBoxEditPassword);
        editTextName = root.findViewById(R.id.editTextName);
        editTextPhone = root.findViewById(R.id.editTextPhone);
        editTextNewPassword = root.findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = root.findViewById(R.id.editTextConfirmPassword);
        buttonSaveChanges = root.findViewById(R.id.buttonSaveChanges);
        buttonSignOut = root.findViewById(R.id.buttonSignOut);
        textCurrentName = root.findViewById(R.id.textCurrentName);
        textCurrentPhone = root.findViewById(R.id.textCurrentPhone);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Handle checkbox events to toggle visibility of EditText fields
        checkBoxEditName.setOnCheckedChangeListener((buttonView, isChecked) -> editTextName.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        checkBoxEditPhone.setOnCheckedChangeListener((buttonView, isChecked) -> editTextPhone.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        checkBoxEditPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int visibility = isChecked ? View.VISIBLE : View.GONE;
            editTextNewPassword.setVisibility(visibility);
            editTextConfirmPassword.setVisibility(visibility);
        });

        // Load user info from Firebase
        if (user != null) {
            loadUserInfo();
        }

        // Save Changes Button logic
        buttonSaveChanges.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.confirm_changes)
                    .setMessage(R.string.are_you_sure_you_want_to_save_these_changes)
                    .setPositiveButton("Yes", (dialog, which) -> saveChanges())
                    .setNegativeButton("No", null)
                    .show();
        });

        // Sign-out button
        buttonSignOut.setOnClickListener(v -> signOut());

        // Override back button functionality for AccountFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(root);
                navController.navigate(R.id.navigation_home);
            }
        });

        return root;
    }

    private void loadUserInfo() {
        String userEmail = user.getEmail().replace(".", ","); // Use this format for Firebase keys
        usersRef.child(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child(getString(R.string.name)).getValue(String.class);
                    String phone = dataSnapshot.child(getString(R.string.phone)).getValue(String.class);

                    // Set current name and phone to TextViews
                    textCurrentName.setText(name);
                    textCurrentPhone.setText(phone);

                    // Populate EditText fields if checkboxes are checked
                    if (checkBoxEditName.isChecked()) {
                        editTextName.setText(name);
                    }
                    if (checkBoxEditPhone.isChecked()) {
                        editTextPhone.setText(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load user information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChanges() {
        String newName = editTextName.getText().toString().trim();
        String newPhone = editTextPhone.getText().toString().trim();
        boolean isNameUpdated = checkBoxEditName.isChecked();
        boolean isPhoneUpdated = checkBoxEditPhone.isChecked();
        boolean isPasswordUpdated = checkBoxEditPassword.isChecked();

        // Update display name in Firebase Authentication
        if (isNameUpdated) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Name updated", Toast.LENGTH_SHORT).show();
                            textCurrentName.setText(newName); // Update displayed name
                        } else {
                            Toast.makeText(getContext(), "Failed to update name", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Update phone number in Firebase Realtime Database
        if (isPhoneUpdated) {
            String userEmail = user.getEmail().replace(".", ",");
            usersRef.child(userEmail).child("phone").setValue(newPhone)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Phone updated", Toast.LENGTH_SHORT).show();
                            textCurrentPhone.setText(newPhone); // Update displayed phone
                        } else {
                            Toast.makeText(getContext(), "Failed to update phone", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Update password if necessary
        if (isPasswordUpdated) {
            String newPassword = editTextNewPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            if (!newPassword.isEmpty() && newPassword.equals(confirmPassword)) {
                user.updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signOut() {
        auth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}