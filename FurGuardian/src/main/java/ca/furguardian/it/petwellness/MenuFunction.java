package ca.furguardian.it.petwellness;

import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

public class MenuFunction {
    public boolean createOptionsMenu(MenuInflater inflater, MenuFunction menu) {
        inflater.inflate(R.menu.main_menu, (android.view.Menu) menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            return true;
        }
        if (item.getItemId() == R.id.action_reminders) {
            return true;
        }
        if (item.getItemId() == R.id.action_emergency) {
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return false;
    }

    public boolean handleMenuItemSelection(MenuItem item) {
        return false;
    }
}