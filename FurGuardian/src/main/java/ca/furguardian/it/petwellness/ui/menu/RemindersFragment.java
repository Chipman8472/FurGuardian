package ca.furguardian.it.petwellness.ui.menu;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Calendar;

import ca.furguardian.it.petwellness.R;

public class RemindersFragment extends Fragment {

    private EditText reminderNameEditText;
    private Button pickDateTimeButton, saveReminderButton;
    private TextView selectedDateTimeTextView;
    private NotificationManagerCompat notificationManager;
    private Calendar reminderCalendar;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        reminderNameEditText = view.findViewById(R.id.reminder_name);
        pickDateTimeButton = view.findViewById(R.id.pick_datetime_button);
        saveReminderButton = view.findViewById(R.id.save_reminder_button);
        selectedDateTimeTextView = view.findViewById(R.id.selected_datetime);

        notificationManager = NotificationManagerCompat.from(requireContext());
        reminderCalendar = Calendar.getInstance();

        pickDateTimeButton.setOnClickListener(v -> openDateTimePicker());
        saveReminderButton.setOnClickListener(v -> saveReminder());

        // Override back button functionality for RemindersFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to the home page instead of exiting the app
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.navigation_home);  // Navigate directly to the home page
            }
        });

        return view;
    }

    private void openDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    reminderCalendar.set(year, month, dayOfMonth);
                    openTimePicker();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void openTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    reminderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    reminderCalendar.set(Calendar.MINUTE, minute);
                    selectedDateTimeTextView.setText(getString(R.string.selected) + reminderCalendar.getTime().toString());
                },
                reminderCalendar.get(Calendar.HOUR_OF_DAY),
                reminderCalendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void saveReminder() {
        String reminderName = reminderNameEditText.getText().toString();
        if (reminderName.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.please_enter_a_reminder_name), Toast.LENGTH_SHORT).show();
            return;
        }

        // Check notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission();
                return;
            }
        }

        // Save reminder and trigger notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "reminder_channel")
                .setSmallIcon(R.drawable.cat_silhouette)
                .setContentTitle(getString(R.string.reminder))
                .setContentText(reminderName + getString(R.string.at) + reminderCalendar.getTime().toString())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(1, builder.build());

        Toast.makeText(getContext(), getString(R.string.reminder_saved), Toast.LENGTH_SHORT).show();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveReminder();
            } else {
                Toast.makeText(getContext(), getString(R.string.notification_permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
