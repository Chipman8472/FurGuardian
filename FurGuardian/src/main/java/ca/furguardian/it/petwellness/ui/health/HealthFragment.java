package ca.furguardian.it.petwellness.ui.health;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ca.furguardian.it.petwellness.R;

public class HealthFragment extends Fragment {

    private TimePicker timePicker;
    private RadioGroup radioGroupMeals;
    private RadioButton rbMeal1, rbMeal2, rbMeal3;
    private TextView tvMeal1Time, tvMeal2Time, tvMeal3Time;
    private Button btnSetTime, btnSaveAllTimes, btnClearTime;
    private DatabaseReference mDatabase;

    // Store meal times locally
    private String time1 = "";
    private String time2 = "";
    private String time3 = "";

    public HealthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_health, container, false);

        // Initialize views
        timePicker = view.findViewById(R.id.timePicker);
        radioGroupMeals = view.findViewById(R.id.radioGroupMeals);
        rbMeal1 = view.findViewById(R.id.rbMeal1);
        rbMeal2 = view.findViewById(R.id.rbMeal2);
        rbMeal3 = view.findViewById(R.id.rbMeal3);
        tvMeal1Time = view.findViewById(R.id.tvMeal1Time);
        tvMeal2Time = view.findViewById(R.id.tvMeal2Time);
        tvMeal3Time = view.findViewById(R.id.tvMeal3Time);
        btnSetTime = view.findViewById(R.id.btnSetTime);
        btnClearTime = view.findViewById(R.id.btnClearTime);
        btnSaveAllTimes = view.findViewById(R.id.btnSaveAllTimes);

        // Ensure TimePicker is in 24-hour mode
        timePicker.setIs24HourView(true);

        // Initialize Firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Load existing times from Firebase
        loadExistingTimes();

        // Set up the set time button listener
        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedMealTime();
            }
        });

        // Set up the clear time button listener
        btnClearTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelectedMealTime();
            }
        });

        // Set up the save all times button listener
        btnSaveAllTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAllTimes();
            }
        });

        return view;
    }

    private void loadExistingTimes() {
        mDatabase.child("mealTimes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get time values if they exist
                    if (dataSnapshot.hasChild("time1")) {
                        time1 = dataSnapshot.child("time1").getValue(String.class);
                        tvMeal1Time.setText("Time 1: " + time1);
                    } else {
                        tvMeal1Time.setText("Time 1: Not set");
                    }

                    if (dataSnapshot.hasChild("time2")) {
                        time2 = dataSnapshot.child("time2").getValue(String.class);
                        tvMeal2Time.setText("Time 2: " + time2);
                    } else {
                        tvMeal2Time.setText("Time 2: Not set");
                    }

                    if (dataSnapshot.hasChild("time3")) {
                        time3 = dataSnapshot.child("time3").getValue(String.class);
                        tvMeal3Time.setText("Time 3: " + time3);
                    } else {
                        tvMeal3Time.setText("Time 3: Not set");
                    }
                } else {
                    tvMeal1Time.setText("Time 1: Not set");
                    tvMeal2Time.setText("Time 2: Not set");
                    tvMeal3Time.setText("Time 3: Not set");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load times: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSelectedMealTime() {
        // Get selected time from TimePicker
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Format time as HH:mm (24-hour)
        String selectedTime = String.format("%02d:%02d", hour, minute);

        // Find which meal is selected
        int selectedId = radioGroupMeals.getCheckedRadioButtonId();

        if (selectedId == R.id.rbMeal1) {
            time1 = selectedTime;
            tvMeal1Time.setText("Time 1: " + selectedTime);
        } else if (selectedId == R.id.rbMeal2) {
            time2 = selectedTime;
            tvMeal2Time.setText("Time 2: " + selectedTime);
        } else if (selectedId == R.id.rbMeal3) {
            time3 = selectedTime;
            tvMeal3Time.setText("Time 3: " + selectedTime);
        }

        Toast.makeText(getContext(), "Time set! Click 'Save All Times' to save to database.", Toast.LENGTH_SHORT).show();
    }

    private void clearSelectedMealTime() {
        // Find which meal is selected
        int selectedId = radioGroupMeals.getCheckedRadioButtonId();

        if (selectedId == R.id.rbMeal1) {
            time1 = "";
            tvMeal1Time.setText("Time 1: Not set");
        } else if (selectedId == R.id.rbMeal2) {
            time2 = "";
            tvMeal2Time.setText("Time 2: Not set");
        } else if (selectedId == R.id.rbMeal3) {
            time3 = "";
            tvMeal3Time.setText("Time 3: Not set");
        }

        Toast.makeText(getContext(), "Time cleared! Click 'Save All Times' to update database.", Toast.LENGTH_SHORT).show();
    }

    private void saveAllTimes() {
        // Create a map for all meal times
        HashMap<String, Object> mealTimes = new HashMap<>();

        // Only add times that have been set
        if (!time1.isEmpty()) {
            mealTimes.put("time1", time1);
        } else {
            // If time is empty, remove it from Firebase if it exists
            mDatabase.child("mealTimes").child("time1").removeValue();
        }

        if (!time2.isEmpty()) {
            mealTimes.put("time2", time2);
        } else {
            mDatabase.child("mealTimes").child("time2").removeValue();
        }

        if (!time3.isEmpty()) {
            mealTimes.put("time3", time3);
        } else {
            mDatabase.child("mealTimes").child("time3").removeValue();
        }

        // If there's at least one time set, update Firebase
        if (!mealTimes.isEmpty()) {
            mDatabase.child("mealTimes")
                    .updateChildren(mealTimes)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Times saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to save times.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // If all times were cleared, show a message
            Toast.makeText(getContext(), "All times cleared from database.", Toast.LENGTH_SHORT).show();
        }
    }
}