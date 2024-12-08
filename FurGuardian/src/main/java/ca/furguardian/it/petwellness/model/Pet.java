package ca.furguardian.it.petwellness.model;

public class Pet {
    private String petId; // Unique identifier for each pet
    private String name;
    private String breed;
    private String type;
    private int age;

    // Default constructor for Firebase
    public Pet() {}

    // Constructor with all fields
    public Pet(String petId, String name, String breed, String type, int age) {
        this.petId = petId;
        this.name = name;
        this.breed = breed;
        this.type = type;
        this.age = age;
    }

    // Getters and setters
    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
