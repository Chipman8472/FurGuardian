package ca.furguardian.it.petwellness.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.furguardian.it.petwellness.controller.Format;
import ca.furguardian.it.petwellness.model.User;
import ca.furguardian.it.petwellness.ui.login.LoginActivity;

public class UserModel {

    private final DatabaseReference usersRef;

    public UserModel() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.usersRef = database.getReference("users");
    }



    // Method to register a user in the database
    public void registerUser(String email, String password, String name, String phoneNumber, Context context, RegistrationCallback callback) {
        String formattedEmail = Format.formatEmail(email);

        usersRef.child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback.onRegistrationFailed("User already registered.");
                } else {
                    User newUser = new User(email, password, name, phoneNumber);
                    usersRef.child(formattedEmail).setValue(newUser).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onRegistrationSuccess();
                        } else {
                            callback.onRegistrationFailed("Registration failed. Please try again.");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onRegistrationFailed("Database error: " + databaseError.getMessage());
            }
        });
    }

    // Method to login a user by retrieving data from the database
    public void loginUser(String email, String password, LoginCallback callback) {
        String formattedEmail = Format.formatEmail(email);

        usersRef.child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.getPassword().equals(password)) {
                        callback.onLoginSuccess(user);
                    } else {
                        callback.onLoginFailed("Invalid email or password.");
                    }
                } else {
                    callback.onLoginFailed("User not found. Please register.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onLoginFailed("Database error: " + databaseError.getMessage());
            }
        });
    }

    // Method to retrieve user data by email
    public void getUserData(String email, UserDataCallback callback) {
        String formattedEmail = Format.formatEmail(email);

        usersRef.child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        callback.onDataRetrieved(user);
                    } else {
                        callback.onDataFailed("User data not found.");
                    }
                } else {
                    callback.onDataFailed("User does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataFailed("Database error: " + databaseError.getMessage());
            }
        });
    }

    // Method to update user data in the database
    public void updateUserData(String email, User updatedUser, UpdateDataCallback callback) {
        String formattedEmail = Format.formatEmail(email);

        usersRef.child(formattedEmail).setValue(updatedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onUpdateSuccess();
            } else {
                callback.onUpdateFailed("Failed to update user data.");
            }
        });
    }

    // Sign out method
    public void signOut(Context context) {
        // Clear stored session data
        SharedPreferences sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Clear all data in SharedPreferences
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear back stack
        context.startActivity(intent);
    }

    // Callback interfaces
    public interface RegistrationCallback {
        void onRegistrationSuccess();
        void onRegistrationFailed(String errorMessage);
    }

    public interface LoginCallback {
        void onLoginSuccess(User user);
        void onLoginFailed(String errorMessage);
    }

    public interface UserDataCallback {
        void onDataRetrieved(User user);
        void onDataFailed(String errorMessage);
    }

    public interface UpdateDataCallback {
        void onUpdateSuccess();
        void onUpdateFailed(String errorMessage);
    }
}
