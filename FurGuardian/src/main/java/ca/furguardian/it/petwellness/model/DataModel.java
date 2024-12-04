package ca.furguardian.it.petwellness.model;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ca.furguardian.it.petwellness.R;

public class DataModel {

    private final DatabaseReference dataRef;
    private final Random random;

    public DataModel() {
        // Reference to the "data" table in Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dataRef = database.getReference("data");
        random = new Random();
    }

    // Method to simulate more realistic health data
    public Map<String, Object> simulateData() {
        // Heart rate (bpm): Normal resting heart rate for adults is between 60 and 100
        int heartRate = generateHeartRate();

        // Respiratory rate (breaths per minute): Normal is between 12 and 20 for an adult
        int respiratoryRate = generateRespiratoryRate();

        // Steps: Average steps per day for an adult can range between 4000 and 12000, depending on activity
        int steps = generateStepCount();

        // Distance (km): Daily distance walked based on steps, average stride length is 0.762 meters
        double distance = steps * 0.000762; // Convert steps to km (0.762m per step)

        // Sleep hours: Adults need 7-9 hours, but it can vary slightly for shorter or longer sleepers
        double sleepHours = generateSleepHours();

        // Weight (kg): Slight fluctuations based on meals, hydration, and activity
        double weight = generateWeight();

        // Prepare the data map to store in Firebase
        Map<String, Object> data = new HashMap<>();
        data.put("heartRate", heartRate);
        data.put("respiratoryRate", respiratoryRate);
        data.put("steps", steps);
        data.put("distance", distance);
        data.put("sleepHours", sleepHours);
        data.put("weight", weight);

        return data;
    }

    // Simulates realistic heart rate
    private int generateHeartRate() {
        double activityFactor = Math.random();
        if (activityFactor < 0.2) { // Resting
            return 60 + random.nextInt(10); // 60-69 bpm
        } else if (activityFactor < 0.7) { // Light activity
            return 70 + random.nextInt(20); // 70-89 bpm
        } else { // High activity
            return 90 + random.nextInt(20); // 90-109 bpm
        }
    }

    // Simulates realistic respiratory rate
    private int generateRespiratoryRate() {
        double activityFactor = Math.random();
        if (activityFactor < 0.3) { // Resting
            return 12 + random.nextInt(3); // 12-14 breaths/min
        } else if (activityFactor < 0.8) { // Light activity
            return 15 + random.nextInt(4); // 15-18 breaths/min
        } else { // High activity
            return 19 + random.nextInt(2); // 19-20 breaths/min
        }
    }

    // Simulates realistic step count
    private int generateStepCount() {
        double activityLevel = Math.random();
        if (activityLevel < 0.3) { // Sedentary day
            return 2000 + random.nextInt(2000); // 2000-3999 steps
        } else if (activityLevel < 0.7) { // Moderately active day
            return 4000 + random.nextInt(4000); // 4000-7999 steps
        } else { // Very active day
            return 8000 + random.nextInt(5000); // 8000-12999 steps
        }
    }

    // Simulates realistic sleep hours
    private double generateSleepHours() {
        double sleepQuality = Math.random();
        if (sleepQuality < 0.3) { // Poor sleep
            return 5 + random.nextDouble(); // 5-6 hours
        } else if (sleepQuality < 0.7) { // Average sleep
            return 7 + random.nextDouble() * 2; // 7-9 hours
        } else { // Good sleep
            return 9 + random.nextDouble(); // 9-10 hours
        }
    }

    // Simulates realistic weight fluctuations
    private double generateWeight() {
        double baselineWeight = 7.0; // Assuming 7.0kg as average for the subject
        return baselineWeight + (random.nextDouble() * 0.3 - 0.15); // +/- 0.15kg fluctuation
    }


    // Method to send the simulated data to Firebase
    public void sendDataToDatabase(Context context) {
        Map<String, Object> data = simulateData();
        dataRef.setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        System.out.println(context.getString(R.string.data_successfully_sent_to_database));
                    else {
                        System.out.println(context.getString(R.string.failed_to_send_data_to_database));
                    }
                });
    }

    // Method to retrieve data from Firebase
    public void retrieveDataFromDatabase(ValueEventListener listener) {
        dataRef.addListenerForSingleValueEvent(listener);
    }
}
