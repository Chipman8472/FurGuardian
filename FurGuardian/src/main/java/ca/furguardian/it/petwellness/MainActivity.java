package ca.furguardian.it.petwellness;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ca.furguardian.it.petwellness.databinding.ActivityMainBinding;
import ca.furguardian.it.petwellness.ui.menu.RemindersFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the SplashScreen using the system's SplashScreen API
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        // Customize the splash screen duration to last for 3 seconds
        splashScreen.setKeepOnScreenCondition(() -> {
            // Delay splash screen for 3 seconds using Handler
            new Handler().postDelayed(() -> {
                // After 3 seconds, allow the splash screen to disappear
                splashScreen.setKeepOnScreenCondition(() -> false);
            }, 3000); // 3000 ms = 3 seconds
            return true;  // Keep the splash screen until the handler completes the delay
        });

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of IDs because each
        // menu should be considered as top-level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_health, R.id.navigation_records, R.id.navigation_peted)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public void onBackPressed() {
        // Create an AlertDialog to confirm exit
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.logo)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Exit the app
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog and stay in the app
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            // Handle profile action
            return true;
        } else if (item.getItemId() == R.id.action_reminders) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, new RemindersFragment())
                    .addToBackStack(null)  // Optional for back navigation
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.action_emergency) {
            // Handle emergency action
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            // Handle settings action
            return true;
        } else {
            return false;
        }
    }
}
