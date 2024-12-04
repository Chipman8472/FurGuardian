package ca.furguardian.it.petwellness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowToast;

import ca.furguardian.it.petwellness.ui.login.LoginActivity;
import ca.furguardian.it.petwellness.ui.login.RegistrationActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {29})
public class LoginActivityTest {

    private LoginActivity loginActivity;

    @Before
    public void setUp() {
        // Mock Firebase initialization
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);

        // Build LoginActivity
        loginActivity = Robolectric.buildActivity(LoginActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }

    @Test
    public void testActivityInitialization() {
        assertNotNull("LoginActivity should not be null", loginActivity);
    }


}
