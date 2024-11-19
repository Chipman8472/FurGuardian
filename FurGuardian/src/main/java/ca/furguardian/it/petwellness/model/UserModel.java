package ca.furguardian.it.petwellness.model;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.controller.Format;
import ca.furguardian.it.petwellness.controller.PasswordUtil;
import ca.furguardian.it.petwellness.model.User;
import ca.furguardian.it.petwellness.ui.login.LoginActivity;

public class UserModel {

    private final DatabaseReference usersRef;
    private static final String TAG = "UserModel";

    public UserModel() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        this.usersRef = database.getReference("users");
    }



    public void registerUser(String email, String password, String name, String phoneNumber, Context context, RegistrationCallback callback) {
        String formattedEmail = Format.formatEmail(email);

        usersRef.child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback.onRegistrationFailed(context.getString(R.string.user_already_registered1));
                } else {
                    String salt = PasswordUtil.generateSalt();
                    String hashedPassword = PasswordUtil.hashPassword(password, salt);

                    User newUser = new User(email, hashedPassword, salt, name, phoneNumber);
                    usersRef.child(formattedEmail).setValue(newUser).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onRegistrationSuccess();
                        } else {
                            callback.onRegistrationFailed(context.getString(R.string.registration_failed_please_try_again));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onRegistrationFailed(context.getString(R.string.database_error1) + databaseError.getMessage());
            }
        });
    }


    public void loginUser(String email, String password, Context context, LoginCallback callback) {
        String formattedEmail = Format.formatEmail(email);

        usersRef.child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        boolean isPasswordValid = PasswordUtil.validatePassword(password, user.getHashedPassword(), user.getSalt());
                        if (isPasswordValid) {
                            callback.onLoginSuccess(user);
                        } else {
                            callback.onLoginFailed(context.getString(R.string.invalid_email_or_password));
                        }
                    } else {
                        callback.onLoginFailed(context.getString(R.string.invalid_email_or_password));
                    }
                } else {
                    callback.onLoginFailed(context.getString(R.string.user_not_found_please_register));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onLoginFailed(context.getString(R.string.database_error1) + databaseError.getMessage());
            }
        });
    }


    // Method to retrieve user data by email
    public void getUserData(String email, Context context, UserDataCallback callback) {
        String formattedEmail = Format.formatEmail(email);

        usersRef.child(formattedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        callback.onDataRetrieved(user);
                    } else {
                        callback.onDataFailed(context.getString(R.string.user_data_not_found));
                    }
                } else {
                    callback.onDataFailed(context.getString(R.string.user_does_not_exist));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataFailed(context.getString(R.string.database_error1) + databaseError.getMessage());
            }
        });
    }

    // Method to update user data in the database
    public void updateUserData(String email, User updatedUser, Context context, UpdateDataCallback callback) {
        String formattedEmail = Format.formatEmail(email);

        usersRef.child(formattedEmail).setValue(updatedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onUpdateSuccess();
            } else {
                callback.onUpdateFailed(context.getString(R.string.failed_to_update_user_data));
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
