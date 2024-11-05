package ca.furguardian.it.petwellness.ui.menu;

import android.net.Uri;

public class Pet {
    private String name;
    private String breed;
    private String type;
    private int age;
    private double weight;
    private Uri profileImageUri;

    public Pet(String name, String breed, String type, int age, double weight, Uri profileImageUri) {
        this.name = name;
        this.breed = breed;
        this.type = type;
        this.age = age;
        this.weight = weight;
        this.profileImageUri = profileImageUri;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public String getBreed() {
        return breed;
    }

    public String getType() {
        return type;
    }

    public Uri getProfileImageUri() {
        return profileImageUri;
    }
}