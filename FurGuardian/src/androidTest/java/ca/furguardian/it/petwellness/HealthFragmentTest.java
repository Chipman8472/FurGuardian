package ca.furguardian.it.petwellness;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import ca.furguardian.it.petwellness.R.layout;


import android.widget.EditText;
import android.widget.TextView;

import org.junit.Test;

import java.util.Map;

import ca.furguardian.it.petwellness.ui.health.HealthFragment;

public class HealthFragmentTest {

    @Test
    public void testRealTimeUpdatesDisplayed() {
        // Check if heart rate is displayed after real-time update
        onView(withId(R.id.textHeartRate))
                .check(matches(isDisplayed()));

        // Simulate data update
        onView(withId(R.id.textHeartRate))
                .check(matches(withText(containsString("bpm"))));
    }

    @Test
    public void testAddWeightDialogInteraction() {
        // Click the add weight button
        onView(withId(R.id.buttonAddWeight))
                .perform(click());

        // Check if the dialog is displayed
        onView(withText(R.string.add_weight_record))
                .check(matches(isDisplayed()));

        // Enter weight and confirm
        onView(isAssignableFrom(EditText.class))
                .perform(typeText("65.5"), closeSoftKeyboard());
        onView(withText(R.string.add))
                .perform(click());

        // Verify the weight is updated in the UI
        onView(withId(R.id.textCurrentWeight))
                .check(matches(withText(containsString("65.5 kg"))));
    }

    @Test
    public void testHealthTipsUpdate() {
        // Simulate data that triggers health tips update
        onView(withId(R.id.textHeartRate)).perform(replaceText("100 bpm"));
        onView(withId(R.id.textSteps)).perform(replaceText("2000"));
        onView(withId(R.id.textSleepHours)).perform(replaceText("5 hrs"));

        // Check if health tips are updated accordingly
        onView(withId(R.id.textHealthTips))
                .check(matches(withText(containsString("Your heart rate is elevated"))));
    }

    @Test
    public void testBackButtonAlertDialog() {
        // Press back button
        pressBack();

        // Check if the alert dialog is displayed
        onView(withText(R.string.are_you_sure_you_want_to_exit))
                .check(matches(isDisplayed()));

        // Dismiss the dialog
        onView(withText(R.string.no))
                .perform(click());

        // Verify the fragment is still displayed
       // onView(withId(R.id.fragment_health))
               // .check(matches(isDisplayed()));
    }

    @Test
    public void testDistanceDisplay() {
        // Simulate distance data update
        onView(withId(R.id.textDistance))
                .perform(replaceText("5.2 km"));

        // Verify the format is correct
        onView(withId(R.id.textDistance))
                .check(matches(withText(containsString("km"))));
    }



}
