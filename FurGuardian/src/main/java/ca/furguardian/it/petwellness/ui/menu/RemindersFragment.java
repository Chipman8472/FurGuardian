package ca.furguardian.it.petwellness.ui.menu;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

import ca.furguardian.it.petwellness.MainActivity;
import ca.furguardian.it.petwellness.R;

public class RemindersFragment extends Fragment {

    private EditText reminderNameEditText;
    private Button pickDateTimeButton, saveReminderButton;
    private TextView selectedDateTimeTextView;

    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private Calendar selectedDateTime;

    private NotificationManagerCompat notificationManager;

    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    saveReminder();
                } else {
                    Toast.makeText(getContext(), "Notification permission is required to set reminders", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        reminderNameEditText = view.findViewById(R.id.reminder_name);
        pickDateTimeButton = view.findViewById(R.id.pick_datetime_button);
        saveReminderButton = view.findViewById(R.id.save_reminder_button);
        selectedDateTimeTextView = view.findViewById(R.id.selected_datetime);

        notificationManager = NotificationManagerCompat.from(requireContext());

        pickDateTimeButton.setOnClickListener(v -> openDateTimePicker());
        saveReminderButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                } else {
                    saveReminder();
                }
            } else {
                saveReminder();
            }
        });

        return view;
    }

    private void openDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedYear = year;
                    selectedMonth = month;
                    selectedDay = dayOfMonth;

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            getContext(),
                            (timeView, hourOfDay, minute) -> {
                                selectedHour = hourOfDay;
                                selectedMinute = minute;

                                selectedDateTime = Calendar.getInstance();
                                selectedDateTime.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);

                                selectedDateTimeTextView.setText("Selected: " + selectedDateTime.getTime().toString());
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveReminder() {
        String reminderName = reminderNameEditText.getText().toString();
        if (reminderName.isEmpty() || selectedDateTime == null) {
            Toast.makeText(getContext(), "Please enter a reminder name and select a date/time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check notification permission for Android 13+ (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request notification permission
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                return;  // Exit here; we'll handle the permission result in the ActivityResultLauncher
            }
        }

        // If permission is already granted or running on older Android versions
        // Schedule notification using AlarmManager, WorkManager, or other timing solutions (not shown here).
        sendNotification(reminderName);
    }


    private void sendNotification(String reminderName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "reminder_channel")
                .setSmallIcon(R.drawable.cat_silhouette)
                .setContentTitle("Reminder")
                .setContentText(reminderName)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());

        Toast.makeText(getContext(), "Reminder saved!", Toast.LENGTH_SHORT).show();
    }
}
