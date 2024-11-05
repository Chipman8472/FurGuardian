package ca.furguardian.it.petwellness.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.furguardian.it.petwellness.MainActivity;
import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.ui.login.RegistrationActivity;

public class LoginActivity extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1001;

    private EditText loginEmail, loginPassword;
    private CheckBox rememberMeCheckbox;
    private Button loginButton, registerButton;
    private SignInButton googleSignInButton;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the saved preference for dark mode
        SharedPreferences sharedPreferences = this.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        boolean isDarkModeOn = sharedPreferences.getBoolean("darkMode", false);

        // Set the theme based on the preference
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        loginEmail = findViewById(R.id.username);
        loginPassword = findViewById(R.id.password);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);

        // Set up Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Load saved credentials if "Remember Me" was checked
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            loginEmail.setText(sharedPreferences.getString("email", ""));
            loginPassword.setText(sharedPreferences.getString("password", ""));
            rememberMeCheckbox.setChecked(true);
        }

        // Login button action
        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                loginUser(email, password);
            } else {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Register button action to navigate to RegistrationActivity
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        // Google Sign-In button action
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    // Method to login a user
    private void loginUser(String email, String password) {
        String formattedEmail = formatEmail(email);

        usersRef.child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.getPassword().equals(password)) {
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // Save credentials if "Remember Me" is checked
                        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (rememberMeCheckbox.isChecked()) {
                            editor.putBoolean("rememberMe", true);
                            editor.putString("email", email);
                            editor.putString("password", password);
                        } else {
                            editor.clear();
                        }
                        editor.apply();

                        // Navigate to MainActivity after successful login
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close LoginActivity so it doesn't stay in the back stack
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User not found. Please register.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method for Google Sign-In
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show();
                    // Proceed to MainActivity after Google Sign-In
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // Helper method to format email for Firebase storage
    private String formatEmail(String email) {
        return email.replace(".", "_").replace("@", "_");
    }
}
