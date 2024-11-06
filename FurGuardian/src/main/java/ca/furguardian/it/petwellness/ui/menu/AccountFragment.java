package ca.furguardian.it.petwellness.ui.menu;

//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563

import android.app.AlertDialog;
import android.content.Context;
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

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.model.User;
import ca.furguardian.it.petwellness.model.UserModel;

public class AccountFragment extends Fragment {

    private CheckBox checkBoxEditName, checkBoxEditPhone, checkBoxEditPassword;
    private EditText editTextName, editTextPhone, editTextNewPassword, editTextConfirmPassword;
    private Button buttonSaveChanges, buttonSignOut;
    private TextView textCurrentName, textCurrentPhone;
    private UserModel userModel;
    private User currentUser;

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

        userModel = new UserModel();

        // Retrieve user email from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "");

        // Load user info using UserModel
        loadUserInfo(userEmail);

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
                    .setPositiveButton(R.string.yes, (dialog, which) -> saveChanges(userEmail))
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

    private void loadUserInfo(String email) {
        userModel.getUserData(email, getContext(),new UserModel.UserDataCallback() {
            @Override
            public void onDataRetrieved(User user) {
                currentUser = user;
                // Set current name and phone to TextViews
                textCurrentName.setText(user.getName());
                textCurrentPhone.setText(user.getPhoneNumber());

                // Populate EditText fields if checkboxes are checked
                if (checkBoxEditName.isChecked()) {
                    editTextName.setText(user.getName());
                }
                if (checkBoxEditPhone.isChecked()) {
                    editTextPhone.setText(user.getPhoneNumber());
                }
            }

            @Override
            public void onDataFailed(String errorMessage) {
                Toast.makeText(getContext(), getString(R.string.failed_to_load_user_information) + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChanges(String email) {
        boolean isNameUpdated = checkBoxEditName.isChecked();
        boolean isPhoneUpdated = checkBoxEditPhone.isChecked();
        boolean isPasswordUpdated = checkBoxEditPassword.isChecked();

        if (currentUser == null) {
            Toast.makeText(getContext(), getString(R.string.user_data_not_available), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update name if checked
        if (isNameUpdated) {
            String newName = editTextName.getText().toString().trim();
            currentUser.setName(newName);
            textCurrentName.setText(newName); // Update displayed name
        }

        // Update phone if checked
        if (isPhoneUpdated) {
            String newPhone = editTextPhone.getText().toString().trim();
            currentUser.setPhoneNumber(newPhone);
            textCurrentPhone.setText(newPhone); // Update displayed phone
        }

        // Update password if checked and confirmed
        if (isPasswordUpdated) {
            String newPassword = editTextNewPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            if (!newPassword.isEmpty() && newPassword.equals(confirmPassword)) {
                currentUser.setPassword(newPassword);
                Toast.makeText(getContext(), getString(R.string.password_updated), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Update user data in the database
        userModel.updateUserData(email, currentUser, getContext(), new UserModel.UpdateDataCallback() {
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
        userModel.signOut(requireContext());
    }
}
