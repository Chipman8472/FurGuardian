package ca.furguardian.it.petwellness.ui.menu;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import ca.furguardian.it.petwellness.R;

public class SettingsFragment extends Fragment {

    private static final String NOTIFICATION_CHANNEL_ID = "user_notifications";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private SwitchCompat toggleDarkMode, toggleLockOrientation, toggleNotifications, toggleLocation;
    private ImageView settingImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        toggleDarkMode = view.findViewById(R.id.toggleDarkMode);
        toggleLockOrientation = view.findViewById(R.id.toggleLockOrientation);
        toggleNotifications = view.findViewById(R.id.toggleNotifications);
        toggleLocation = view.findViewById(R.id.toggleLocation);
        settingImage = view.findViewById(R.id.image_header);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        boolean isDarkModeOn = sharedPreferences.getBoolean("darkMode", false);
        boolean isOrientationLocked = sharedPreferences.getBoolean("lockOrientation", false);
        boolean isNotificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", true);
        boolean isLocationEnabled = sharedPreferences.getBoolean("locationEnabled", true);

        toggleDarkMode.setChecked(isDarkModeOn);
        toggleLockOrientation.setChecked(isOrientationLocked);
        toggleNotifications.setChecked(isNotificationsEnabled);
        toggleLocation.setChecked(isLocationEnabled);

        // Apply dark mode and orientation settings based on preferences
        AppCompatDelegate.setDefaultNightMode(isDarkModeOn ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        getActivity().setRequestedOrientation(isOrientationLocked ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        toggleDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("darkMode", isChecked);
            editor.apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        toggleLockOrientation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("lockOrientation", isChecked);
            editor.apply();
            getActivity().setRequestedOrientation(isChecked ? ActivityInfo.SCREEN_ORIENTATION_LOCKED : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        });

        toggleNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationsEnabled", isChecked);
            editor.apply();
            if (isChecked) {
                enableNotifications();
            } else {
                disableNotifications();
            }
        });

        toggleLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("locationEnabled", isChecked);
            editor.apply();
            if (isChecked) {
                requestLocationPermission();
            } else {
                revokeLocationPermission();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.navigation_home);
            }
        });

        return view;
    }

    private void enableNotifications() {
        NotificationChannel channel = new NotificationChannel(
                getString(R.string.reminder_channel),
                getString(R.string.reminder_notifications),
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription(getString(R.string.channel_for_reminder_notifications));
        NotificationManager manager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        Toast.makeText(getContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.deleteNotificationChannel(getString(R.string.reminder_channel));
            Toast.makeText(getContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(getContext(), "Location access enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void revokeLocationPermission() {
        // Direct revocation of location permission by the app is not possible in Android.
        // However, you can guide the user to disable location permission manually if needed.
        Toast.makeText(getContext(), "Location access disabled. Please disable manually if required.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                toggleLocation.setChecked(false); // Reset toggle if permission denied
            }
        }
    }
}
