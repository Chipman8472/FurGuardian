package ca.furguardian.it.petwellness.ui.menu;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import androidx.navigation.Navigation;

import ca.furguardian.it.petwellness.R;

public class SettingsFragment extends Fragment {

    private SwitchCompat toggleDarkMode;  // Use SwitchCompat instead of Switch

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the toggle switch
        toggleDarkMode = view.findViewById(R.id.toggleDarkMode);

        // Load the saved preference for dark mode
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userPrefs", getContext().MODE_PRIVATE);
        boolean isDarkModeOn = sharedPreferences.getBoolean("darkMode", false);
        toggleDarkMode.setChecked(isDarkModeOn);

        // Set the theme based on the preference
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Override back button functionality for RemindersFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to the home page instead of exiting the app
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.navigation_home);  // Navigate directly to the home page
            }
        });

        // Add listener for toggle switch
        toggleDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("darkMode", true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("darkMode", false);
            }
            editor.apply();
        });

        return view;
    }
}
