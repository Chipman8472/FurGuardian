package ca.furguardian.it.petwellness.ui.login;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ca.furguardian.it.petwellness.MainActivity;
import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.model.User;
import ca.furguardian.it.petwellness.model.UserModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    public EditText loginEmail;
    public EditText loginPassword;
    public CheckBox rememberMeCheckbox;
    private Button loginButton, registerButton;
    private UserModel userModel;
    private static final int RC_SIGN_IN = 100; // Request code for Google Sign-In
    private GoogleSignInClient googleSignInClient; // Google Sign-In client
    private static final String GOOGLE_API_KEY = "276746006424-9c17b9lbag8363112kjebk56p5u27a1a.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPreferences = this.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("loggedIn", false);


        applySettings();


        if (isRemembered) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // Configure Google Sign-In with a separate API key
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_API_KEY)  // Use the separate API key here
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.googleSignInButton).setOnClickListener(v -> signInWithGoogle());

        userModel = new UserModel();

        loginEmail = findViewById(R.id.username);
        loginPassword = findViewById(R.id.password);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);


        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                userModel.loginUser(email, password, this,new UserModel.LoginCallback() {
                    @Override
                    public void onLoginSuccess(User user) {
                        // Handle "Remember Me"
                        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.email1), email);
                        editor.putString(getString(R.string.password), password);
                        editor.putBoolean("loggedIn", rememberMeCheckbox.isChecked());
                        editor.apply();

                        // Navigate to MainActivity
                        Toast.makeText(LoginActivity.this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onLoginFailed(String errorMessage) {
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
    }

    private void applySettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("settingPrefs", Context.MODE_PRIVATE);

        // Apply dark mode setting
        boolean isDarkModeOn = sharedPreferences.getBoolean("darkMode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkModeOn ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Apply orientation lock setting
        boolean isOrientationLocked = sharedPreferences.getBoolean("lockOrientation", false);
        setRequestedOrientation(isOrientationLocked ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                Log.d("GoogleSignIn", "Sign-In successful. Email: " + account.getEmail());
                // Pass the Google Sign-In account to UserModel for further processing
                String email = account.getEmail();
                String name = account.getDisplayName();
                String profilePicture = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";

                userModel.handleGoogleSignIn(email, name, profilePicture, LoginActivity.this, new UserModel.LoginCallback() {
                    @Override
                    public void onLoginSuccess(User user) {
                        // Successful login flow
                        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.email1), user.getEmail());
                        editor.putBoolean("loggedIn", true);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onLoginFailed(String errorMessage) {
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (ApiException e) {
            Log.e("GoogleSignIn", "Sign-In failed with status code: " + e.getStatusCode());
            Log.e("GoogleSignIn", "Error message: " + e.getMessage());
            Toast.makeText(this, "Google Sign-In failed. Error code: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }
}
