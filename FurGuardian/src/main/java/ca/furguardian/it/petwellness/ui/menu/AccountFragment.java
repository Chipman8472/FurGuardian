package ca.furguardian.it.petwellness.ui.menu;

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

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.model.User;
import ca.furguardian.it.petwellness.model.UserModel;
import ca.furguardian.it.petwellness.ui.login.LoginActivity;

public class AccountFragment extends Fragment {

    private CheckBox checkBoxEditName, checkBoxEditPhone, checkBoxEditPassword;
    private EditText editTextName, editTextPhone, editTextNewPassword, editTextConfirmPassword;
    private TextView textCurrentName, textCurrentPhone;
    private UserModel userModel;
    private User currentUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

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
        Button buttonSaveChanges = root.findViewById(R.id.buttonSaveChanges);
        Button buttonSignOut = root.findViewById(R.id.buttonSignOut);
        textCurrentName = root.findViewById(R.id.textCurrentName);
        textCurrentPhone = root.findViewById(R.id.textCurrentPhone);

        userModel = new UserModel();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            loadUserInfo(firebaseUser.getUid());
        } else {
            Toast.makeText(getContext(), "not logged in", Toast.LENGTH_SHORT).show();
        }

        // Handle checkbox events to toggle visibility of EditText fields
        checkBoxEditName.setOnCheckedChangeListener((buttonView, isChecked) -> editTextName.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        checkBoxEditPhone.setOnCheckedChangeListener((buttonView, isChecked) -> editTextPhone.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        checkBoxEditPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int visibility = isChecked ? View.VISIBLE : View.GONE;
            editTextNewPassword.setVisibility(visibility);
            editTextConfirmPassword.setVisibility(visibility);
        });

        // Save Changes Button logic
        buttonSaveChanges.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.confirm_changes1)
                    .setMessage(R.string.are_you_sure_you_want_to_save_these_changes1)
                    .setPositiveButton(R.string.yes, (dialog, which) -> saveChanges())
                    .setNegativeButton(R.string.no, null)
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

    private void loadUserInfo(String userId) {
        userModel.retrieveUserData(userId, getContext(), new UserModel.LoginCallback() {
            @Override
            public void onLoginSuccess(User user) {
                currentUser = user;

                // Display current name and phone
                textCurrentName.setText(user.getName());
                textCurrentPhone.setText(user.getPhoneNumber());

                // Prepopulate EditText fields if checkboxes are checked
                if (checkBoxEditName.isChecked()) {
                    editTextName.setText(user.getName());
                }
                if (checkBoxEditPhone.isChecked()) {
                    editTextPhone.setText(user.getPhoneNumber());
                }
            }

            @Override
            public void onLoginFailed(String errorMessage) {
                Toast.makeText(getContext(), getString(R.string.failed_to_load_user_information) + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChanges() {
        if (currentUser == null || firebaseUser == null) {
            Toast.makeText(getContext(), R.string.user_data_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isNameUpdated = checkBoxEditName.isChecked();
        boolean isPhoneUpdated = checkBoxEditPhone.isChecked();
        boolean isPasswordUpdated = checkBoxEditPassword.isChecked();

        // Update name if checked
        if (isNameUpdated) {
            String newName = editTextName.getText().toString().trim();
            currentUser.setName(newName);
            textCurrentName.setText(newName);
        }

        // Update phone if checked
        if (isPhoneUpdated) {
            String newPhone = editTextPhone.getText().toString().trim();
            currentUser.setPhoneNumber(newPhone);
            textCurrentPhone.setText(newPhone);
        }

        // Update password using Firebase Authentication
        if (isPasswordUpdated) {
            String newPassword = editTextNewPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            if (!newPassword.isEmpty() && newPassword.equals(confirmPassword)) {
                firebaseUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), getString(R.string.password_updated), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "failed to update password", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Update user data in Realtime Database
        userModel.updateUserData(firebaseUser.getUid(), currentUser, getContext(), new UserModel.UpdateDataCallback() {
            @Override
            public void onUpdateSuccess() {
                Toast.makeText(getContext(), getString(R.string.changes_saved_successfully), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpdateFailed(String errorMessage) {
                Toast.makeText(getContext(), getString(R.string.failed_to_save_changes) + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signOut() {
        // Clear the "Remember Me" preference
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("remember_me"); // Or editor.clear() if you want to clear all preferences
        editor.apply();

        // Sign out from Firebase
        firebaseAuth.signOut();

        // Show a toast message
        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show();

        // Navigate to LoginActivity
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
        startActivity(intent);
        requireActivity().finish(); // Close the current activity
    }

}
