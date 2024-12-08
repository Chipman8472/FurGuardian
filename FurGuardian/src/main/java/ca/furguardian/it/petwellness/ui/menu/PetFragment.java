package ca.furguardian.it.petwellness.ui.menu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.databinding.FragmentPetprofileBinding;
import ca.furguardian.it.petwellness.model.Pet;

public class PetFragment extends Fragment {
    private FragmentPetprofileBinding binding;
    private List<Pet> pets = new ArrayList<>();
    private int currentPetIndex = 0;

    private FirebaseAuth auth;
    private DatabaseReference userPetsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPetprofileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userPetsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("pets");
            loadPetsFromFirebase();
        }

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

        binding.buttonAddPet.setOnClickListener(v -> showAddPetDialog());
        binding.buttonDelete.setOnClickListener(v -> deleteCurrentPet());

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController navController = Navigation.findNavController(root);
                navController.navigate(R.id.navigation_home);
            }
        });

        return root;
    }

    private void loadPetsFromFirebase() {
        userPetsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pets.clear();
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    Pet pet = petSnapshot.getValue(Pet.class);
                    if (pet != null) {
                        pets.add(pet);
                    }
                }
                if (!pets.isEmpty()) {
                    currentPetIndex = 0;
                    displayCurrentPet();
                } else {
                    clearPetDisplay();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), getString(R.string.failed_to_load_pets), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCurrentPet() {
        if (binding == null || pets.isEmpty()) return;

        Pet currentPet = pets.get(currentPetIndex);
        binding.textPetInfo.setText(currentPet.getName());
        binding.petAgeText.setText(getString(R.string.age) + " " + currentPet.getAge() + " " + getString(R.string.years));
        binding.petBreedText.setText(getString(R.string.breed) + " " + currentPet.getBreed());
    }

    private void showAddPetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.add_a_new_pet);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText petNameInput = new EditText(requireContext());
        petNameInput.setHint(R.string.pet_name);
        layout.addView(petNameInput);

        EditText petAgeInput = new EditText(requireContext());
        petAgeInput.setHint(R.string.pet_age_years);
        layout.addView(petAgeInput);

        RadioGroup petTypeRadioGroup = new RadioGroup(requireContext());
        RadioButton dogRadioButton = new RadioButton(requireContext());
        dogRadioButton.setText(R.string.dog);
        petTypeRadioGroup.addView(dogRadioButton);
        RadioButton catRadioButton = new RadioButton(requireContext());
        catRadioButton.setText(R.string.cat);
        petTypeRadioGroup.addView(catRadioButton);
        layout.addView(petTypeRadioGroup);

        Spinner petBreedSpinner = new Spinner(requireContext());
        layout.addView(petBreedSpinner);
        ArrayAdapter<String> breedAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petBreedSpinner.setAdapter(breedAdapter);

        petTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String[] breeds;
            if (checkedId == dogRadioButton.getId()) {
                breeds = getResources().getStringArray(R.array.dog_breeds);
            } else if (checkedId == catRadioButton.getId()) {
                breeds = getResources().getStringArray(R.array.cat_breeds);
            } else {
                breeds = new String[0];
            }
            breedAdapter.clear();
            breedAdapter.addAll(breeds);
            breedAdapter.notifyDataSetChanged();
        });

        builder.setView(layout);

        builder.setPositiveButton(R.string.add_pet, (dialog, which) -> {
            String petName = petNameInput.getText().toString();
            String petBreed = petBreedSpinner.getSelectedItem() != null ? petBreedSpinner.getSelectedItem().toString() : "";
            String petType = "";
            int selectedId = petTypeRadioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = layout.findViewById(selectedId);
                petType = selectedRadioButton.getText().toString();
            }

            int petAge = 0;
            try {
                petAge = Integer.parseInt(petAgeInput.getText().toString());
            } catch (NumberFormatException ignored) {
            }

            if (petName.isEmpty() || petBreed.isEmpty() || petType.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            Pet newPet = new Pet(UUID.randomUUID().toString(), petName, petBreed, petType, petAge);
            userPetsRef.child(newPet.getPetId()).setValue(newPet).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    pets.add(newPet);
                    currentPetIndex = pets.size() - 1;
                    displayCurrentPet();
                    Toast.makeText(requireContext(), R.string.pet_added_successfully, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.failed_to_add_pet, Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteCurrentPet() {
        if (pets.isEmpty() || currentPetIndex < 0 || currentPetIndex >= pets.size()) {
            Toast.makeText(requireContext(), getString(R.string.no_pet_to_delete), Toast.LENGTH_SHORT).show();
            return;
        }

        Pet petToDelete = pets.get(currentPetIndex);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_pet)
                .setMessage(getString(R.string.are_you_sure_delete_pet) + " " + petToDelete.getName())
                .setPositiveButton(R.string.yes2, (dialog, which) -> {
                    userPetsRef.child(petToDelete.getPetId()).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            pets.remove(currentPetIndex);
                            if (!pets.isEmpty()) {
                                currentPetIndex = Math.max(0, Math.min(currentPetIndex, pets.size() - 1));
                                displayCurrentPet();
                            } else {
                                clearPetDisplay();
                            }
                            Toast.makeText(requireContext(), R.string.pet_deleted_successfully, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), R.string.failed_to_delete_pet, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void clearPetDisplay() {
        binding.textPetInfo.setText(R.string.pet_name1);
        binding.petAgeText.setText(R.string.age_0);
        binding.petBreedText.setText(R.string.breed_unknown);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
