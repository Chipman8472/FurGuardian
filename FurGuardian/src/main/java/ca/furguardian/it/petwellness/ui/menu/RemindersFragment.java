package ca.furguardian.it.petwellness.ui.menu;

import android.app.DatePickerDialog;
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
                    selectedDateTimeTextView.setText("Selected: " + dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveReminder() {
        String reminderName = reminderNameEditText.getText().toString();
        if (reminderName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a reminder name", Toast.LENGTH_SHORT).show();
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "reminder_channel")
                .setSmallIcon(R.drawable.cat_silhouette)
                .setContentTitle("Reminder")
                .setContentText(reminderName)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(1, builder.build());

        Toast.makeText(getContext(), "Reminder saved!", Toast.LENGTH_SHORT).show();
    }
}
