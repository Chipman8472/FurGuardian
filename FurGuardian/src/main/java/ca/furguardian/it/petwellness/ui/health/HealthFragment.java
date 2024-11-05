package ca.furguardian.it.petwellness.ui.health;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.databinding.FragmentHealthBinding;
import ca.furguardian.it.petwellness.model.DataModel;

public class HealthFragment extends Fragment {

    private FragmentHealthBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private DataModel dataModel;
    private Runnable updateMetricsRunnable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHealthBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize HealthDataSimulator
        dataModel = new DataModel();

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

    private void initializeHealthMetrics() {
        // Retrieve data from Firebase on initialization
        dataModel.retrieveDataFromDatabase(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                    if (data != null) {
                        updateUIWithHealthData(data);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load health data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startRealTimeUpdates() {
        updateMetricsRunnable = new Runnable() {
            @Override
            public void run() {
                // Simulate data and send it to Firebase
                dataModel.sendDataToDatabase();

                // Retrieve the updated data from Firebase to update the UI
                dataModel.retrieveDataFromDatabase(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Map<String, Object> data = (Map<String, Object>) snapshot.getValue();
                            if (data != null) {
                                updateUIWithHealthData(data);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load health data.", Toast.LENGTH_SHORT).show();
                    }
                });

                // Schedule the next update in 5 seconds
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(updateMetricsRunnable);
    }

    private void updateUIWithHealthData(Map<String, Object> data) {
        int heartRate = ((Long) data.get("heartRate")).intValue();
        int respiratoryRate = ((Long) data.get("respiratoryRate")).intValue();
        int steps = ((Long) data.get("steps")).intValue();
        double distance = (double) data.get("distance");
        int sleepHours = ((Long) data.get("sleepHours")).intValue();
        double weight = (double) data.get("weight");

        binding.textHeartRate.setText("Heart Rate: " + heartRate + " bpm");
        binding.textRespiratoryRate.setText("Respiratory Rate: " + respiratoryRate + " bpm");
        binding.textSteps.setText("Steps: " + steps);
        binding.textDistance.setText(String.format("Distance: %.2f km", distance));
        binding.textSleepHours.setText("Sleep Hours: " + sleepHours + " hrs");
        binding.textCurrentWeight.setText("Current Weight: " + weight + " kg");

        updateHealthTips(heartRate, steps, sleepHours);
    }

    private void updateHealthTips(int heartRate, int steps, int sleepHours) {
        List<String> healthTips = new ArrayList<>();

        if (heartRate > 90) {
            healthTips.add("Your heart rate is elevated. Take a few moments to relax.");
        }
        if (steps < 3500) {
            healthTips.add("You haven't reached your daily step goal. Try to be more active!");
        }
        if (sleepHours < 7) {
            healthTips.add("You might need more sleep for optimal health.");
        }

        if (healthTips.isEmpty()) {
            healthTips.add("Great job! Keep up the healthy habits.");
        }

        binding.textHealthTips.setText("• " + TextUtils.join("\n• ", healthTips));
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateMetricsRunnable);
    }
}
