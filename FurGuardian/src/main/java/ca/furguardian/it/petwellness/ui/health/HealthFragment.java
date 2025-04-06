package  ca.furguardian.it.petwellness.ui.health;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import ca.furguardian.it.petwellness.R;

public class HealthFragment extends Fragment {

    // Quick comment

    private TimePicker timePicker;
    private Button btnSaveTime;
    private DatabaseReference mDatabase;

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
        btnSaveTime = view.findViewById(R.id.btnSaveTime);

        // Ensure TimePicker is in 24-hour mode
        timePicker.setIs24HourView(true);

        // Initialize Firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set up the save button listener
        btnSaveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get selected time from TimePicker
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Format time as HH:mm (24-hour)
                String selectedTime = String.format("%02d:%02d", hour, minute);

                HashMap<String, Object> updateMap = new HashMap<>();
                updateMap.put("date", selectedTime);
                updateMap.put("once", true);

                // Save the time to Firebase at a generic database node.
                // Replace "selectedTime" with your desired database path.
                mDatabase.child("dateObject")
                        .updateChildren(updateMap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Time saved successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to save time.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }
}
