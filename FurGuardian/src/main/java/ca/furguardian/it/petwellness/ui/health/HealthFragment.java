package ca.furguardian.it.petwellness.ui.health;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
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

    public void initializeHealthMetrics() {
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
                Toast.makeText(getContext(), getString(R.string.failed_to_load_health_data), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startRealTimeUpdates() {
        updateMetricsRunnable = new Runnable() {
            @Override
            public void run() {
                // Simulate data and send it to Firebase
                dataModel.sendDataToDatabase(getContext());

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
                        Toast.makeText(getContext(), getString(R.string.failed_to_load_health_data1), Toast.LENGTH_SHORT).show();
                    }
                });

                // Schedule the next update in 5 seconds
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(updateMetricsRunnable);
    }

    public void updateUIWithHealthData(Map<String, Object> data) {
        // Safely retrieve and cast data from the map
        int heartRate = ((Number) data.get("heartRate")).intValue();
        int respiratoryRate = ((Number) data.get("respiratoryRate")).intValue();
        int steps = ((Number) data.get("steps")).intValue();
        double distance = ((Number) data.get("distance")).doubleValue();
        int sleepHours = ((Number) data.get("sleepHours")).intValue();
        double weight = ((Number) data.get("weight")).doubleValue();

        // Update UI elements with the retrieved values
        binding.textHeartRate.setText(getString(R.string.heart_rate) + heartRate + getString(R.string.bpm));
        binding.textRespiratoryRate.setText(getString(R.string.respiratory_rate) + respiratoryRate + getString(R.string.bpm));
        binding.textSteps.setText(getString(R.string.steps) + steps);
        binding.textDistance.setText(String.format(getString(R.string.distance_2f_km), distance));
        binding.textSleepHours.setText(getString(R.string.sleep_hours) + sleepHours + getString(R.string.hrs));
        binding.textCurrentWeight.setText(getString(R.string.current_weight) + weight + getString(R.string.kg));

        // Update health tips
        updateHealthTips(heartRate, steps, sleepHours);
    }


    public void updateHealthTips(int heartRate, int steps, int sleepHours) {
        List<String> healthTips = new ArrayList<>();

        if (heartRate > 90) {
            healthTips.add(getString(R.string.your_heart_rate_is_elevated_take_a_few_moments_to_relax));
        }
        if (steps < 3500) {
            healthTips.add(getString(R.string.you_haven_t_reached_your_daily_step_goal_try_to_be_more_active));
        }
        if (sleepHours < 7) {
            healthTips.add(getString(R.string.you_might_need_more_sleep_for_optimal_health));
        }

        if (healthTips.isEmpty()) {
            healthTips.add(getString(R.string.great_job_keep_up_the_healthy_habits));
        }

        binding.textHealthTips.setText("• " + TextUtils.join("\n• ", healthTips));
    }

    public void showAddWeightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.add_weight_record));

        final EditText input = new EditText(getContext());
        input.setHint(getString(R.string.enter_weight_in_kg));
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.add), (dialog, which) -> {
            String weight = input.getText().toString();
            if (!weight.isEmpty()) {
                binding.textCurrentWeight.setText(getString(R.string.current_weight) + weight + getString(R.string.kg));
                Toast.makeText(getContext(), getString(R.string.weight_record_updated), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), getString(R.string.weight_cannot_be_empty), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateMetricsRunnable);
    }
}
