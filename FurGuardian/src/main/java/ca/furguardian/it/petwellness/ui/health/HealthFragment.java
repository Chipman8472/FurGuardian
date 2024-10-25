// Justin Chipman - RCB – N01598472
// Imran Zafurallah - RCB - N01585098
// Zane Aransevia - RCB- N01351168
// Tevadi Brookes - RCC - N01582563

package ca.furguardian.it.petwellness.ui.health;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.databinding.FragmentHealthBinding;

public class HealthFragment extends Fragment {

    private FragmentHealthBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHealthBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI Elements for Health Metrics using View Binding
        initializeHealthMetrics();

        // Set click listener for adding manual weight entry
        binding.textCurrentWeight.setOnClickListener(v -> showAddWeightDialog());

        // Set click listener for adding medical record entry
        binding.textMedicalRecords.setOnClickListener(v -> showAddMedicalRecordDialog());

        // Override back button functionality for HealthFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(requireContext())
                        .setIcon(R.mipmap.logo)
                        .setTitle(R.string.exit_app)
                        .setMessage(R.string.are_you_sure_you_want_to_exit)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requireActivity().finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
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
        binding.textTemperature.setText("Temperature: 37.5 °C");

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

    // Show dialog to add weight entry
    private void showAddWeightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Weight Record");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter weight in kg");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String weight = input.getText().toString();
            if (!weight.isEmpty()) {
                binding.textCurrentWeight.setText("Current Weight: " + weight + " kg");
                Toast.makeText(requireContext(), "Weight record updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Weight cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // Show dialog to add medical record entry
    private void showAddMedicalRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Medical Record");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter medical record details");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String record = input.getText().toString();
            if (!record.isEmpty()) {
                binding.textMedicalRecords.setText(record);
                Toast.makeText(requireContext(), "Medical record updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Record cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
