package ca.furguardian.it.petwellness.ui.login;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.furguardian.it.petwellness.MainActivity;
import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.controller.Format;
import ca.furguardian.it.petwellness.controller.InputValidator;
import ca.furguardian.it.petwellness.model.User;
import ca.furguardian.it.petwellness.model.UserModel;

public class RegistrationActivity extends AppCompatActivity {



    private EditText nameField, phoneField, emailField, passwordField, confirmPasswordField;
    private Button registerButton;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userModel = new UserModel();

        nameField = findViewById(R.id.name);
        phoneField = findViewById(R.id.phone);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (!InputValidator.isValidEmail(email)) {
                Toast.makeText(this, getString(R.string.invalid_email_format), Toast.LENGTH_SHORT).show();
            } else if (!InputValidator.isValidPhoneNumber(phone)) {
                Toast.makeText(this, getString(R.string.phone_number_must_be_10_digits), Toast.LENGTH_SHORT).show();
            } else if (!InputValidator.isValidPassword(password)) {
                Toast.makeText(this, getString(R.string.password_must_be_8_16_characters_contain_at_least_1_uppercase_letter_1_number_and_1_symbol), Toast.LENGTH_LONG).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, getString(R.string.passwords_do_not_match1), Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.email), email);
                editor.putString(getString(R.string.password1), password);
                editor.apply();

                userModel.registerUser(email, password, name, phone, this, new UserModel.RegistrationCallback() {
                    @Override
                    public void onRegistrationSuccess() {
                        Toast.makeText(RegistrationActivity.this, getString(R.string.registration_successful1), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onRegistrationFailed(String errorMessage) {
                        Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

}



