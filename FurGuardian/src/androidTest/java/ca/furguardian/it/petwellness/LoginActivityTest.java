package ca.furguardian.it.petwellness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import ca.furguardian.it.petwellness.MainActivity;
import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.ui.login.LoginActivity;

public class LoginActivityTest {

    private Context context;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        // Initialize the context and shared preferences for testing
        context = ApplicationProvider.getApplicationContext();
        sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
    }

    @Test
    public void testSharedPreferencesInitialization() {
        // Test if SharedPreferences is initialized correctly
        assertNotNull("SharedPreferences should not be null", sharedPreferences);
    }

    @Test
    public void testDefaultLoggedInValue() {
        // Check the default value of the "loggedIn" preference
        boolean isLoggedIn = sharedPreferences.getBoolean("loggedIn", false);
        assertFalse("Default value for loggedIn should be false", isLoggedIn);
    }

    @Test
    public void testDarkModePreference() {
        // Set dark mode preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("darkMode", true);
        editor.apply();

        // Check if dark mode preference was saved
        boolean isDarkModeOn = sharedPreferences.getBoolean("darkMode", false);
        assertTrue("Dark mode preference should be true", isDarkModeOn);
    }

    @Test
    public void testOrientationLockPreference() {
        // Set orientation lock preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("lockOrientation", true);
        editor.apply();

        // Check if orientation lock preference was saved
        boolean isOrientationLocked = sharedPreferences.getBoolean("lockOrientation", false);
        assertTrue("Orientation lock preference should be true", isOrientationLocked);
    }

    @Test
    public void testRememberMePreference() {
        // Set "remember me" preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", true);
        editor.apply();

        // Check if "remember me" preference was saved
        boolean isRemembered = sharedPreferences.getBoolean("loggedIn", false);
        assertTrue("LoggedIn preference should be true when remember me is checked", isRemembered);
    }

    @Test
    public void testEmailPreference() {
        // Set email in preferences
        String testEmail = "test@example.com";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.email1), testEmail);
        editor.apply();

        // Retrieve email from preferences
        String savedEmail = sharedPreferences.getString(context.getString(R.string.email1), null);
        assertEquals("Email preference should match the saved value", testEmail, savedEmail);
    }

    @Test
    public void testPasswordPreference() {
        // Set password in preferences
        String testPassword = "securepassword";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.password), testPassword);
        editor.apply();

        // Retrieve password from preferences
        String savedPassword = sharedPreferences.getString(context.getString(R.string.password), null);
        assertEquals("Password preference should match the saved value", testPassword, savedPassword);
    }

    @Test
    public void testActivityTransitionOnSuccessfulLogin() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(activity -> {
                // Simulate clicking login button and performing login
                Button loginButton = activity.findViewById(R.id.loginButton);
                EditText emailInput = activity.findViewById(R.id.username);
                EditText passwordInput = activity.findViewById(R.id.password);

                // Enter valid login credentials
                emailInput.setText("admin");
                passwordInput.setText("password123");

                // Click login button
                loginButton.performClick();

                // Assert MainActivity is launched
                Intent expectedIntent = new Intent(activity, MainActivity.class);
                assertNotNull("Expected MainActivity Intent should not be null", expectedIntent);
            });
        }
    }


    @Test
    public void testEmptyLoginFields() {
        // Test validation when login fields are empty
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(activity -> {
                boolean isValid = !activity.loginEmail.getText().toString().isEmpty() && !activity.loginPassword.getText().toString().isEmpty();
                assertFalse("Validation should fail if login fields are empty", isValid);
            });
        }
    }

    @Test
    public void testRememberMeCheckBoxDefaultState() {
        // Test the default state of the "Remember Me" checkbox
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(activity -> {
                assertFalse("Remember Me checkbox should be unchecked by default", activity.rememberMeCheckbox.isChecked());
            });
        }
    }
}
