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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ca.furguardian.it.petwellness.MainActivity;
import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.model.User;
import ca.furguardian.it.petwellness.model.UserModel;

public class LoginActivity extends AppCompatActivity {

    public EditText loginEmail;
    public EditText loginPassword;
    public CheckBox rememberMeCheckbox;
    private Button loginButton, registerButton;
    private UserModel userModel;

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
}
