package ca.furguardian.it.petwellness.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ca.furguardian.it.petwellness.MainActivity;
import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.model.User;
import ca.furguardian.it.petwellness.model.UserModel;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private CheckBox rememberMeCheckbox;
    private Button loginButton, registerButton;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                userModel.loginUser(email, password, new UserModel.LoginCallback() {
                    @Override
                    public void onLoginSuccess(User user) {
                        // Handle "Remember Me"
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

                        // Navigate to MainActivity
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onLoginFailed(String errorMessage) {
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
    }
}
