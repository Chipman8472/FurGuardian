package ca.furguardian.it.petwellness.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import ca.furguardian.it.petwellness.MainActivity;
import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.controller.InputValidator;
import ca.furguardian.it.petwellness.model.User;
import ca.furguardian.it.petwellness.model.UserModel;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "LoginActivity";

    private EditText loginEmail, loginPassword;
    private CheckBox rememberMeCheckbox;
    private UserModel userModel;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userModel = new UserModel();
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        loginEmail = findViewById(R.id.username);
        loginPassword = findViewById(R.id.password);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        SignInButton googleSignInButton = findViewById(R.id.googleSignInButton);

        // Set up Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // From google-services.json
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check if user is already signed in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            validateUserSession(currentUser);
        }

        // Login with email and password
        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();

            if (!isInputValid(email, password)) {
                Toast.makeText(LoginActivity.this, getString(R.string.invalid_email_or_password), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isConnectedToInternet()) {
                Toast.makeText(LoginActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                return;
            }

            userModel.loginUser(email, password, this, new UserModel.LoginCallback() {
                @Override
                public void onLoginSuccess(User user) {
                    if (rememberMeCheckbox.isChecked()) {
                        saveLoginState(email);
                    }
                    navigateToMainActivity();
                }

                @Override
                public void onLoginFailed(String errorMessage) {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });

        // Register button
        registerButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));

        // Set click listener for Google Sign-In
        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private boolean isInputValid(String email, String password) {
        return InputValidator.isValidEmail(email) && InputValidator.isValidPassword(password);
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            android.net.Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork != null) {
                android.net.NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
                return capabilities != null &&
                        (capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET));
            }
        }
        return false;
    }


    private void validateUserSession(FirebaseUser user) {
        userModel.retrieveOrCreateUser(user, this, new UserModel.LoginCallback() {
            @Override
            public void onLoginSuccess(User user) {
                navigateToMainActivity();
            }

            @Override
            public void onLoginFailed(String errorMessage) {
                firebaseAuth.signOut();
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google sign-in failed", e);
                Toast.makeText(this, getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            validateUserSession(firebaseUser);
                        }
                    } else {
                        Log.e(TAG, "Google Sign-In failed: ", task.getException());
                        Toast.makeText(LoginActivity.this, getString(R.string.google_sign_in_failed), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveLoginState(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putBoolean("loggedIn", true);
        editor.apply();
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
