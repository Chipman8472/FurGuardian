package ca.furguardian.it.petwellness.ui.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.databinding.FragmentPetprofileBinding;

public class PetFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST_CODE = 1234;
    private FragmentPetprofileBinding binding;
    private List<Pet> pets = new ArrayList<>();
    private int currentPetIndex = 0;
    private Uri selectedImageUri; // Keep track of the selected image URI
    private ImageView petImagePreview; // Declare petImagePreview as a class member

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPetprofileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Next and Previous Pet buttons
        binding.buttonNextPet.setOnClickListener(v -> {
            if (!pets.isEmpty()) {
                currentPetIndex = (currentPetIndex + 1) % pets.size();
                displayCurrentPet();
            }
        });

        binding.buttonPreviousPet.setOnClickListener(v -> {
            if (!pets.isEmpty()) {
                currentPetIndex = (currentPetIndex - 1 + pets.size()) % pets.size();
                displayCurrentPet();
            }
        });

        // Add Pet Button
        binding.buttonAddPet.setOnClickListener(v -> showAddPetDialog());

        // Delete Pet Button
        binding.buttonDelete.setOnClickListener(v -> deleteCurrentPet());

        // Override back button functionality for RemindersFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to the home page instead of exiting the app
                NavController navController = Navigation.findNavController(root);
                navController.navigate(R.id.navigation_home);  // Navigate directly to the home page
            }
        });

        return root;
    }

    // Display the current pet details on screen
    private void displayCurrentPet() {
        if (pets.isEmpty()) return;

        Pet currentPet = pets.get(currentPetIndex);
        binding.textPetInfo.setText(currentPet.getName());
        binding.petAgeText.setText("Age: " + currentPet.getAge() + " years");
        binding.petWeightText.setText("Weight: " + currentPet.getWeight() + " kg");
        binding.petBreedText.setText("Breed: " + currentPet.getBreed());
        binding.imagePet.setImageURI(currentPet.getProfileImageUri());
    }

    // Open Add Pet dialog
    private void showAddPetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add a New Pet");

        // Create a LinearLayout to hold the dialog contents
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create EditText for Pet Name
        EditText petNameInput = new EditText(requireContext());
        petNameInput.setHint("Pet Name");
        layout.addView(petNameInput);

        // Create EditText for Pet Age
        EditText petAgeInput = new EditText(requireContext());
        petAgeInput.setHint("Pet Age (years)");
        layout.addView(petAgeInput);

        // Create EditText for Pet Weight
        EditText petWeightInput = new EditText(requireContext());
        petWeightInput.setHint("Pet Weight (lbs)");
        layout.addView(petWeightInput);

        // Create RadioGroup for Pet Type
        RadioGroup petTypeRadioGroup = new RadioGroup(requireContext());

        RadioButton dogRadioButton = new RadioButton(requireContext());
        dogRadioButton.setText("Dog");
        petTypeRadioGroup.addView(dogRadioButton);

        RadioButton catRadioButton = new RadioButton(requireContext());
        catRadioButton.setText("Cat");
        petTypeRadioGroup.addView(catRadioButton);

        layout.addView(petTypeRadioGroup);

        // Create Spinner for Pet Breed
        Spinner petBreedSpinner = new Spinner(requireContext());
        layout.addView(petBreedSpinner);

        // Set up adapter for the breed spinner
        ArrayAdapter<String> breedAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petBreedSpinner.setAdapter(breedAdapter);

        // Update Spinner based on selected RadioButton
        petTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String[] breeds;
            if (checkedId == dogRadioButton.getId()) {
                breeds = getResources().getStringArray(R.array.dog_breeds);
            } else if (checkedId == catRadioButton.getId()) {
                breeds = getResources().getStringArray(R.array.cat_breeds);
            } else {
                breeds = new String[0]; // No selection
            }
            breedAdapter.clear();
            breedAdapter.addAll(breeds);
            breedAdapter.notifyDataSetChanged();
        });

        // Create ImageView for Pet Image Preview
        petImagePreview = new ImageView(requireContext()); // Initialize the class member
        petImagePreview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
        petImagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        layout.addView(petImagePreview);

        // Button to select image
        Button selectImageButton = new Button(requireContext());
        selectImageButton.setText("Select Image");
        selectImageButton.setOnClickListener(v -> openGallery());
        layout.addView(selectImageButton);

        // Set the layout to the dialog
        builder.setView(layout);

        // Positive button to add pet
        builder.setPositiveButton("Add Pet", (dialog, which) -> {
            String petName = petNameInput.getText().toString();
            String petBreed = petBreedSpinner.getSelectedItem() != null ? petBreedSpinner.getSelectedItem().toString() : "";

            // Get selected pet type
            String petType = "";
            int selectedId = petTypeRadioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = layout.findViewById(selectedId);
                petType = selectedRadioButton.getText().toString();
            }

            // Parse pet age and weight with safe defaults
            int petAge = 0;
            double petWeight = 0.0;
            try {
                petAge = Integer.parseInt(petAgeInput.getText().toString());
            } catch (NumberFormatException ignored) { }

            try {
                petWeight = Double.parseDouble(petWeightInput.getText().toString());
            } catch (NumberFormatException ignored) { }

            // Check if required fields are filled
            if (petName.isEmpty() || petBreed.isEmpty() || petType.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return; // Do not dismiss dialog
            }

            // Add pet and display current pet
            Uri petImageUri = selectedImageUri != null ? selectedImageUri : Uri.parse("android.resource://ca.furguardian.it.petwellness/" + R.drawable.dog_silhouette);
            addPetToList(petName, petBreed, petType, petAge, petWeight, petImageUri);
            displayCurrentPet();

            dialog.dismiss(); // Dismiss dialog only after adding pet
        });

        // Negative button to cancel
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }


    // Method to open the gallery and update the ImageView with the selected image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData(); // Store selected image URI
            petImagePreview.setImageURI(selectedImageUri); // Update preview
        }
    }

    // Add a pet to the list
    private void addPetToList(String name, String breed, String type, int age, double weight, Uri imageUri) {
        Pet newPet = new Pet(name, breed, type, age, weight, imageUri);
        pets.add(newPet);
        currentPetIndex = pets.size() - 1; // Set the new pet as the current pet
    }

    private void deleteCurrentPet() {
        if (!pets.isEmpty()) {
            pets.remove(currentPetIndex); // Remove the pet at the current index

            if (pets.isEmpty()) {
                clearPetDisplay(); // Clear display if no pets are left
            } else {
                // Update currentPetIndex to stay within bounds
                currentPetIndex = currentPetIndex % pets.size();
                displayCurrentPet(); // Display the next pet after deletion
            }
        } else {
            Toast.makeText(requireContext(), "No pet to delete.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to clear pet information from the screen
    private void clearPetDisplay() {
        binding.textPetInfo.setText("Pet Name");
        binding.petAgeText.setText("Age: 0");
        binding.petWeightText.setText("Weight: 0");
        binding.petBreedText.setText("Breed: Unknown");
        binding.imagePet.setImageResource(R.drawable.dog_silhouette);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}