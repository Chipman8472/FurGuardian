package ca.furguardian.it.petwellness;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.furguardian.it.petwellness.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize BottomNavigationView and NavController
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Define top-level destinations for AppBarConfiguration
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_health, R.id.navigation_records, R.id.navigation_peted
        ).build();

        // Set up ActionBar and BottomNavigationView with NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Handle BottomNavigationView selection
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                navigateToBottomNavFragment(R.id.navigation_home);
                return true;
            } else if (itemId == R.id.navigation_health) {
                navigateToBottomNavFragment(R.id.navigation_health);
                return true;
            } else if (itemId == R.id.navigation_records) {
                navigateToBottomNavFragment(R.id.navigation_records);
                return true;
            } else if (itemId == R.id.navigation_peted) {
                navigateToBottomNavFragment(R.id.navigation_peted);
                return true;
            }
            return false;
        });

        // *** NEW: Listen for WebRTC connection status changes ***
        DatabaseReference webrtcStatusRef = FirebaseDatabase.getInstance().getReference("webrtc/connectionStatus");
        webrtcStatusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if ("connected".equalsIgnoreCase(status)) {
                    // Navigate to the WebRTC fragment when connection is established.
                    navController.navigate(R.id.navigation_health, null, new NavOptions.Builder()
                            .setPopUpTo(R.id.mobile_navigation, false)
                            .build());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log error if needed
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu); // Inflate the top menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Use NavController to handle navigation for the top menu
        if (item.getItemId() == R.id.action_profile) {
            navigateToTopMenuFragment(R.id.petprofilefragment);
            return true;
        } else if (item.getItemId() == R.id.action_reminders) {
            navigateToTopMenuFragment(R.id.remindersFragment);
            return true;
        } else if (item.getItemId() == R.id.action_account) {
            navigateToTopMenuFragment(R.id.accountFragment);
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            navigateToTopMenuFragment(R.id.settingsFragment);
            return true;
        } else if (item.getItemId() == R.id.action_feedback) {
            navigateToTopMenuFragment(R.id.feedFragment);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToTopMenuFragment(int fragmentId) {
        // Clear back stack when navigating to a new fragment from the top menu
        navController.navigate(fragmentId, null, new NavOptions.Builder()
                .setPopUpTo(R.id.mobile_navigation, false)
                .build());
    }

    private void navigateToBottomNavFragment(int fragmentId) {
        // Ensure proper navigation when switching between bottom navigation items
        navController.navigate(fragmentId, null, new NavOptions.Builder()
                .setPopUpTo(R.id.mobile_navigation, false)
                .build());
    }
}
