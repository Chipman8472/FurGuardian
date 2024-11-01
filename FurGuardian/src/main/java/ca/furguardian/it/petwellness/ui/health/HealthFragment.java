// Justin Chipman - RCB – N01598472
// Imran Zafurallah - RCB - N01585098
// Zane Aransevia - RCB- N01351168
// Tevadi Brookes - RCC - N01582563

package ca.furguardian.it.petwellness.ui.health;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
]import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.databinding.FragmentHealthBinding;

public class HealthFragment extends Fragment {

    private FragmentHealthBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private Runnable updateMetricsRunnable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHealthBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI Elements for Health Metrics using View Binding
        initializeHealthMetrics();

        // Start real-time updates
        startRealTimeUpdates();

        // Set click listener for adding manual weight entry
        binding.buttonAddWeight.setOnClickListener(v -> showAddWeightDialog());

        // Set click listener for adding medical record entry
        binding.buttonAddMedicalRecord.setOnClickListener(v -> showAddMedicalRecordDialog());

        // Override back button functionality for HealthFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(requireContext())
                        .setIcon(R.mipmap.logo)
                        .setTitle(R.string.exit_app)
                        .setMessage(R.string.are_you_sure_you_want_to_exit)
                        .setPositiveButton(R.string.yes, (dialog, which) -> requireActivity().finish())
                        .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        return root;
    }

    // Method to Initialize Health Metrics
    private void initializeHealthMetrics() {
        // Vitals
        binding.textHeartRate.setText("Heart Rate: 80 bpm");
        binding.textRespiratoryRate.setText("Respiratory Rate: 20 bpm");

        // Activity
        binding.textSteps.setText("Steps: 3500");
        binding.textDistance.setText("Distance: 2.5 km");
        binding.textActiveTime.setText("Active Time: 3 hrs");

        // Weight Management
        binding.textCurrentWeight.setText("Current Weight: 7 kg");
        binding.textIdealWeight.setText("Ideal Weight: 6-7 kg");

        // Sleep Tracking
        binding.textSleepHours.setText("Sleep Hours: 12 hrs");
        binding.textSleepQuality.setText("Quality: Good");

        // Health Tips
        binding.textHealthTips.setText("Ensure regular hydration and exercise.");
    }

    private void startRealTimeUpdates() {
        updateMetricsRunnable = new Runnable() {
            @Override
            public void run() {
                updateHealthMetrics();
                handler.postDelayed(this, 5000); // Update every 5 seconds
            }
        };
        handler.post(updateMetricsRunnable);
    }

    private void updateHealthMetrics() {
        int heartRate = 60 + random.nextInt(41);  // Random heart rate between 60 and 100
        int respiratoryRate = 15 + random.nextInt(6);  // Random respiratory rate between 15 and 20
        int steps = 3000 + random.nextInt(500);  // Random step count around 3000-3500
        double distance = 2.0 + random.nextDouble() * 0.5;  // Random distance between 2.0 and 2.5 km
        int sleepHours = 6 + random.nextInt(7);  // Random sleep hours between 6 and 12

        // Update TextViews
        binding.textHeartRate.setText("Heart Rate: " + heartRate + " bpm");
        binding.textRespiratoryRate.setText("Respiratory Rate: " + respiratoryRate + " bpm");
        binding.textSteps.setText("Steps: " + steps);
        binding.textDistance.setText(String.format("Distance: %.2f km", distance));
        binding.textSleepHours.setText("Sleep Hours: " + sleepHours + " hrs");

        // Update Health Tips based on current metrics
        updateHealthTips(heartRate, steps, sleepHours);
    }

    // Method to dynamically update health tips based on metrics
    private void updateHealthTips(int heartRate, int steps, int sleepHours) {
        List<String> healthTips = new ArrayList<>();

        // Weight conditions
        double currentWeight = parseMetric(binding.textCurrentWeight.getText().toString());
        double idealWeightLow = 6.0;
        double idealWeightHigh = 7.0;

        // Check heart rate condition
        if (heartRate > 90) {
            healthTips.add("Your heart rate is elevated. Take a few moments to relax.");
        }

        // Check step count condition
        if (steps < 3500) {
            healthTips.add("You haven't reached your daily step goal. Try to be more active!");
        }

        // Check sleep hours condition
        if (sleepHours < 7) {
            healthTips.add("You might need more sleep for optimal health.");
        }

        if (currentWeight < idealWeightLow) {
            healthTips.add("Consider increasing caloric intake with balanced nutrients.");
        } else if (currentWeight > idealWeightHigh) {
            healthTips.add("Focus on regular exercise and portion control.");
        }

        // Default positive tip if no conditions are met
        if (healthTips.isEmpty()) {
            healthTips.add("Great job! Keep up the healthy habits.");
        }

        // Join all tips into a single string with bullet points or new lines
        String combinedTips = TextUtils.join("\n• ", healthTips);
        combinedTips = "• " + combinedTips;  // Adding bullet to the first tip

        // Display the combined tips
        binding.textHealthTips.setText(combinedTips);
    }


    // Show dialog to add weight entry
    private void showAddWeightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Weight Record");

        final EditText input = new EditText(getContext());
        input.setHint("Enter weight in kg");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String weight = input.getText().toString();
            if (!weight.isEmpty()) {
                binding.textCurrentWeight.setText("Current Weight: " + weight + " kg");
                Toast.makeText(getContext(), "Weight record updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Weight cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Show dialog to add medical record entry
    private void showAddMedicalRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Medical Record");

        final EditText input = new EditText(getContext());
        input.setHint("Enter medical record details");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String record = input.getText().toString();
            if (!record.isEmpty()) {
                binding.textMedicalRecords.setText(record);
                Toast.makeText(getContext(), "Medical record updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Record cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Utility method to parse metric values safely
    private double parseMetric(String metricText) {
        // Extract only the numeric part of the string using regex
        String numericPart = metricText.replaceAll("[^\\d.]", ""); // Remove any non-numeric characters

        // Convert to double, handling empty or invalid cases
        try {
            return Double.parseDouble(numericPart);
        } catch (NumberFormatException e) {
            // Return a default value or handle the error as needed
            return 0.0;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateMetricsRunnable);
    }
}
