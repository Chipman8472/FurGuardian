package ca.furguardian.it.petwellness.model;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DataModel {

    private final DatabaseReference dataRef;
    private final Random random;

    public DataModel() {
        // Reference to the "data" table in Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dataRef = database.getReference("data");
        random = new Random();
    }

    // Method to simulate health data
    public Map<String, Object> simulateData() {
        int heartRate = 60 + random.nextInt(41);          // Random heart rate between 60 and 100
        int respiratoryRate = 15 + random.nextInt(6);     // Random respiratory rate between 15 and 20
        int steps = 3000 + random.nextInt(500);           // Random step count around 3000-3500
        double distance = 2.0 + random.nextDouble() * 0.5; // Random distance between 2.0 and 2.5 km
        int sleepHours = 6 + random.nextInt(7);           // Random sleep hours between 6 and 12
        double weight = 6.0 + random.nextDouble() * 2.0;  // Random weight between 6.0 and 8.0 kg

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

    // Method to send the simulated data to Firebase
    public void sendDataToDatabase() {
        Map<String, Object> data = simulateData();
        dataRef.setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Data successfully sent to database.");
                    } else {
                        System.out.println("Failed to send data to database.");
                    }
                });
    }

    // Method to retrieve data from Firebase
    public void retrieveDataFromDatabase(ValueEventListener listener) {
        dataRef.addListenerForSingleValueEvent(listener);
    }
}
