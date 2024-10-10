package ca.furguardian.it.petwellness.ui.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

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
