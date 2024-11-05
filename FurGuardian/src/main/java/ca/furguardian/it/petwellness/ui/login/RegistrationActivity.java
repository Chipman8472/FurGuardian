package ca.furguardian.it.petwellness.ui.login;

import android.content.Intent;
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

public class RegistrationActivity extends AppCompatActivity {

    private EditText nameField, phoneField, emailField, passwordField, confirmPasswordField;
    private Button registerButton;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

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
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            } else if (!InputValidator.isValidPhoneNumber(phone)) {
                Toast.makeText(this, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
            } else if (!InputValidator.isValidPassword(password)) {
                Toast.makeText(this, "Password must be 8-16 characters, contain at least 1 uppercase letter, 1 number, and 1 symbol", Toast.LENGTH_LONG).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(name, phone, email, password);
            }
        });

    }

    private void registerUser(String email, String password, String name, String phoneNumber) {
        String formattedEmail = Format.formatEmail(email);

        usersRef.child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.user_already_registered), Toast.LENGTH_LONG).show();
                } else {
                    User newUser = new User(email, password, name, phoneNumber);
                    usersRef.child(formattedEmail).setValue(newUser).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                            // After registration, redirect to MainActivity
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegistrationActivity.this, getString(R.string.registration_failed), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegistrationActivity.this, getString(R.string.database_error) + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
