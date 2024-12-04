package ca.furguardian.it.petwellness;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ca.furguardian.it.petwellness.ui.health.HealthFragment;

@RunWith(RobolectricTestRunner.class)
public class HealthFragmentTest {

    @Test
    public void testFragmentInitialization() {
        FragmentScenario<HealthFragment> scenario = FragmentScenario.launchInContainer(HealthFragment.class);
        scenario.onFragment(fragment -> {
            assertNotNull(fragment);
            assertNotNull(fragment.getView());
        });
    }

    @Test
    public void testHealthMetricsAreDisplayed() {
        FragmentScenario<HealthFragment> scenario = FragmentScenario.launchInContainer(HealthFragment.class);
        scenario.onFragment(fragment -> {
            TextView heartRateView = fragment.getView().findViewById(R.id.textHeartRate);
            TextView respiratoryRateView = fragment.getView().findViewById(R.id.textRespiratoryRate);

            assertNotNull(heartRateView);
            assertNotNull(respiratoryRateView);

            assertTrue(heartRateView.getText().toString().contains("bpm"));
            assertTrue(respiratoryRateView.getText().toString().contains("bpm"));
        });
    }

    @Test
    public void testHealthTipsDisplayedCorrectly() {
        FragmentScenario<HealthFragment> scenario = FragmentScenario.launchInContainer(HealthFragment.class);
        scenario.onFragment(fragment -> {
            TextView healthTipsView = fragment.getView().findViewById(R.id.textHealthTips);
            assertNotNull(healthTipsView);
            assertTrue(healthTipsView.getText().toString().contains("•")); // Check for bullet points
        });
    }

    @Test
    public void testAddWeightDialogAppears() {
        FragmentScenario<HealthFragment> scenario = FragmentScenario.launchInContainer(HealthFragment.class);
        scenario.onFragment(fragment -> {
            fragment.getView().findViewById(R.id.buttonAddWeight).performClick();

            // Check if the dialog appears
            assertNotNull(fragment.getActivity().getSupportFragmentManager().findFragmentByTag("dialog"));
        });
    }

    @Test
    public void testRealTimeUpdates() {
        FragmentScenario<HealthFragment> scenario = FragmentScenario.launchInContainer(HealthFragment.class);
        scenario.onFragment(fragment -> {
            // Simulate real-time update behavior
            fragment.getView().findViewById(R.id.buttonAddWeight).performClick();
            // Add assertions to validate real-time updates
            TextView heartRateView = fragment.getView().findViewById(R.id.textHeartRate);
            assertNotNull(heartRateView);
        });
    }
}
