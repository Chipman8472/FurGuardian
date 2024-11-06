package ca.furguardian.it.petwellness.ui.menu;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import ca.furguardian.it.petwellness.R;

public class SettingsFragment extends Fragment {

    private SwitchCompat toggleDarkMode, toggleLockOrientation, toggleNotifications, toggleLocation;
    private ImageView settingImage;
    private SharedPreferences sharedPreferences;

    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        toggleDarkMode = view.findViewById(R.id.toggleDarkMode);
        toggleLockOrientation = view.findViewById(R.id.toggleLockOrientation);
        toggleNotifications = view.findViewById(R.id.toggleNotifications);
        toggleLocation = view.findViewById(R.id.toggleLocation);
        settingImage = view.findViewById(R.id.image_header);

        sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);

        // Load saved preferences
        initializeToggles();

        // Handle back button navigation
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.navigation_home);
            }
        });

        setupToggleListeners();

        return view;
    }

    private void initializeToggles() {
        // Set initial states of toggles based on saved preferences
        toggleDarkMode.setChecked(sharedPreferences.getBoolean("darkMode", false));
        toggleLockOrientation.setChecked(sharedPreferences.getBoolean("lockOrientation", false));
        toggleNotifications.setChecked(sharedPreferences.getBoolean("notifications", false));
        toggleLocation.setChecked(sharedPreferences.getBoolean("location", false));

        // Apply dark mode setting
        AppCompatDelegate.setDefaultNightMode(toggleDarkMode.isChecked() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        // Apply screen orientation lock
        getActivity().setRequestedOrientation(toggleLockOrientation.isChecked() ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void setupToggleListeners() {
        toggleDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("darkMode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        toggleLockOrientation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("lockOrientation", isChecked).apply();
            getActivity().setRequestedOrientation(isChecked ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        });

        toggleNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications", isChecked).apply();
            if (isChecked) {
                requestNotificationPermission();
            } else {
                openAppSettings();
            }
        });

        toggleLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("location", isChecked).apply();
            if (isChecked) {
                requestLocationPermission();
            } else {
                openAppSettings();
            }
        });
    }

    // Method to open app settings for the user to disable permissions
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    // Method to request notification permission in-app
    private void requestNotificationPermission() {
        // Define the permission request action
        // For Android 13 and higher, request POST_NOTIFICATIONS permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    // Method to request location permission in-app
    private void requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    // Launchers for permission requests
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    toggleNotifications.setChecked(false);  // Disable toggle if permission denied
                    Toast.makeText(getContext(), "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    toggleLocation.setChecked(false);  // Disable toggle if permission denied
                    Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });
}
