package ca.furguardian.it.petwellness.model;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String email;
    private String name;
    private String phoneNumber;
    private Map<String, Pet> pets; // Map to store pets, keyed by pet ID

    // Default constructor for Firebase
    public User() {
        pets = new HashMap<>();
    }

    // Constructor
    public User(String email, String name, String phoneNumber) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.pets = new HashMap<>();
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Map<String, Pet> getPets() {
        return pets;
    }

    public void setPets(Map<String, Pet> pets) {
        this.pets = pets;
    }

    // Add a pet to the user's pet list
    public void addPet(String petId, Pet pet) {
        if (pets == null) {
            pets = new HashMap<>();
        }
        pets.put(petId, pet);
    }
}
