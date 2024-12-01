package ca.furguardian.it.petwellness.model;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
public class User {
    private String email;
    private String hashedPassword; // Store hashed password, not plain text
    private String salt; // Salt for hashing
    private String name;
    private String phoneNumber;

    // Default constructor for Firebase
    public User() {
    }

    // Constructor with all fields
    public User(String email, String hashedPassword, String salt, String name, String phoneNumber) {
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

