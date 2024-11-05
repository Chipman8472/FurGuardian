package ca.furguardian.it.petwellness.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import ca.furguardian.it.petwellness.ui.login.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private boolean isReadyToProceed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the SplashScreen using the system's SplashScreen API
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        // Keep the splash screen visible until `isReadyToProceed` is true
        splashScreen.setKeepOnScreenCondition(() -> !isReadyToProceed);

        super.onCreate(savedInstanceState);

        // Set a 3-second delay before moving to LoginActivity
        new Handler().postDelayed(() -> {
            isReadyToProceed = true;  // Allow splash screen to be dismissed
            // Start LoginActivity after delay
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // Close SplashScreenActivity
        }, 3000); // 3000 ms = 3 seconds
    }
}
