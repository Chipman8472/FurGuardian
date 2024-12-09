package ca.furguardian.it.petwellness.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.ui.login.LoginActivity;
import ca.furguardian.it.petwellness.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private boolean isReadyToProceed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the SplashScreen
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        // Keep the splash screen visible until `isReadyToProceed` is true
        splashScreen.setKeepOnScreenCondition(() -> !isReadyToProceed);

        super.onCreate(savedInstanceState);

        // Load settings from SharedPreferences
        SharedPreferences settingPrefs = getSharedPreferences("settingPrefs", Context.MODE_PRIVATE);
        boolean darkModeEnabled = settingPrefs.getBoolean("darkMode", false);
        boolean lockOrientationEnabled = settingPrefs.getBoolean("lockOrientation", false);

        // Apply dark mode setting
        AppCompatDelegate.setDefaultNightMode(darkModeEnabled ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Apply orientation lock setting
        setRequestedOrientation(lockOrientationEnabled ?
                ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // Check shared preferences for rememberMe
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("loggedIn", false);

        // Get current FirebaseUser
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // If rememberMe is true and currentUser is not null, go straight to MainActivity
        // Else, go to LoginActivity after the splash delay
        new Handler().postDelayed(() -> {
            isReadyToProceed = true;  // Allow splash screen to be dismissed
            Intent intent;
            if (rememberMe && currentUser != null) {
                // User is remembered and logged in, go directly to MainActivity
                intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            } else {
                // Either rememberMe is false or user is not logged in, go to LoginActivity
                intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 3000); // 3 seconds delay
    }
}
