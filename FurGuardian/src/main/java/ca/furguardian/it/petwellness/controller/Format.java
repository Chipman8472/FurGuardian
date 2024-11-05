package ca.furguardian.it.petwellness.controller;

public class Format {

    public static String formatEmail(String email) {
        return email.replace(".", "_").replace("@", "_");
    }
}
