package ca.furguardian.it.petwellness.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.model.Pet;

public class UserModel {

    private final FirebaseAuth auth;
    private final DatabaseReference usersRef;

    public UserModel() {
        this.auth = FirebaseAuth.getInstance();
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    // Register a new user
    public void registerUser(String email, String password, String name, String phoneNumber, Context context, RegistrationCallback callback) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();

                    // Store user details in Realtime Database
                    User newUser = new User(email, name, phoneNumber);
                    usersRef.child(userId).setValue(newUser).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            callback.onRegistrationSuccess();
                        } else {
                            callback.onRegistrationFailed(context.getString(R.string.failed_to_save_user_data));
                        }
                    });
                }
            } else {
                callback.onRegistrationFailed(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    // Login an existing user
    public void loginUser(String email, String password, Context context, LoginCallback callback) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    retrieveUserData(firebaseUser.getUid(), context, callback);
                }
            } else {
                callback.onLoginFailed(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    // Retrieve user data
    public void retrieveUserData(String userId, Context context, LoginCallback callback) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        callback.onLoginSuccess(user);
                    } else {
                        callback.onLoginFailed(context.getString(R.string.user_data_not_found));
                    }
                } else {
                    callback.onLoginFailed(context.getString(R.string.user_not_found_please_register));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onLoginFailed(error.getMessage());
            }
        });
    }

    public void retrieveOrCreateUser(FirebaseUser firebaseUser, Context context, LoginCallback callback) {
        String userId = firebaseUser.getUid();

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        callback.onLoginSuccess(user);
                    } else {
                        callback.onLoginFailed(context.getString(R.string.user_data_not_found));
                    }
                } else {
                    // Create a new user
                    User newUser = new User(
                            firebaseUser.getEmail(),
                            firebaseUser.getDisplayName(),
                            firebaseUser.getPhoneNumber() != null ? firebaseUser.getPhoneNumber() : ""
                    );
                    usersRef.child(userId).setValue(newUser).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onLoginSuccess(newUser);
                        } else {
                            callback.onLoginFailed(context.getString(R.string.user_creation_failed));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onLoginFailed(error.getMessage());
            }
        });
    }

    // Method to update user data in the database
    public void updateUserData(String userId, User updatedUser, Context context, UpdateDataCallback callback) {
        if (userId == null || updatedUser == null) {
            callback.onUpdateFailed("Failed");
            return;
        }

        // Update the user's data in the database
        usersRef.child(userId).setValue(updatedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onUpdateSuccess();
            } else {
                callback.onUpdateFailed(context.getString(R.string.failed_to_update_user_data));
            }
        }).addOnFailureListener(e -> {
            callback.onUpdateFailed(context.getString(R.string.database_error1) + e.getMessage());
        });
    }

    public void addPet(String userId, String petId, Pet pet, Context context, UpdateDataCallback callback) {
        DatabaseReference petRef = usersRef.child(userId).child("pets").child(petId);
        petRef.setValue(pet).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onUpdateSuccess();
            } else {
                callback.onUpdateFailed(context.getString(R.string.failed_to_add_pet));
            }
        }).addOnFailureListener(e -> {
            callback.onUpdateFailed(context.getString(R.string.database_error1) + e.getMessage());
        });
    }

    public void getPets(String userId, Context context, PetsCallback callback) {
        DatabaseReference petsRef = usersRef.child(userId).child("pets");
        petsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Pet> pets = new HashMap<>();
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    Pet pet = petSnapshot.getValue(Pet.class);
                    if (pet != null) {
                        pets.put(petSnapshot.getKey(), pet);
                    }
                }
                callback.onPetsRetrieved(pets);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onPetsRetrievalFailed(error.getMessage());
            }
        });
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

    // Callback interface for update user data
    public interface UpdateDataCallback {
        void onUpdateSuccess();

        void onUpdateFailed(String errorMessage);
    }

    public interface PetsCallback {
        void onPetsRetrieved(Map<String, Pet> pets);
        void onPetsRetrievalFailed(String errorMessage);
    }

}
