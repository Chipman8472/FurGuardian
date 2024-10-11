package ca.furguardian.it.petwellness;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ca.furguardian.it.petwellness.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ca.furguardian.it.petwellness.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NotificationChannel channel = new NotificationChannel(
                getString(R.string.reminder_channel),
                getString(R.string.reminder_notifications),
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription(getString(R.string.channel_for_reminder_notifications));

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_health, R.id.navigation_records, R.id.navigation_peted)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        if (item.getItemId() == android.R.id.home) {
            // Handle the back button in the toolbar
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_profile) {
            // Handle profile action
            return true;
        } else if (item.getItemId() == R.id.action_reminders) {
            navController.navigate(R.id.remindersFragment);  // Use NavController for fragment navigation
            return true;
        } else if (item.getItemId() == R.id.action_emergency) {
            // Handle emergency action
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            navController.navigate(R.id.settingsFragment);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
