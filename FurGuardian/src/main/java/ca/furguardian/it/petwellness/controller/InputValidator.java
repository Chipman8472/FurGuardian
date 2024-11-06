package ca.furguardian.it.petwellness.controller;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {

    // Validates if the email is in a valid format
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validates if the phone number is exactly 10 digits (for North American numbers)
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("\\d{10}");
    }

    // Validates if the password meets the required criteria
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 16) {
            return false;
        }

        Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*+=]).{8,16}$");
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
}
