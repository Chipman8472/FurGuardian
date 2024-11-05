package ca.furguardian.it.petwellness.ui.menu;

public class Pet {
    private String name;
    private int age;
    private double weight;
    private int profileImageId; // Resource ID for the pet image

    public Pet(String name, int age, double weight, int profileImageId) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.profileImageId = profileImageId;
    }

}
