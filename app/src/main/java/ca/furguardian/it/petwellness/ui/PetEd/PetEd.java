package ca.furguardian.it.petwellness.ui.PetEd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import ca.furguardian.it.petwellness.databinding.FragmentPetedBinding;

public class PetEd extends Fragment {

    private FragmentPetedBinding binding;
    private RecyclerView recyclerView;
    private PetEdAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PetEdViewModel petEdViewModel =
                new ViewModelProvider(this).get(PetEdViewModel.class);

        binding = FragmentPetedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set the text from ViewModel
        final TextView textView = binding.textPeted;
        petEdViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Initialize RecyclerView
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data for the RecyclerView
        List<String> petEducationTopics = Arrays.asList(
                "Pet Nutrition",
                "Grooming Tips",
                "Vaccination Schedule",
                "Training and Obedience",
                "Exercise Needs"
        );

        // Set the adapter
        adapter = new PetEdAdapter(petEducationTopics);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}





//package ca.furguardian.it.petwellness.ui.PetEd;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//
//
//import ca.furguardian.it.petwellness.databinding.FragmentPetedBinding;
//
//public class PetEd extends Fragment {
//
//    private FragmentPetedBinding binding;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        PetEdViewModel PetEdViewModel =
//                new ViewModelProvider(this).get(PetEdViewModel.class);
//
//        binding = FragmentPetedBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textPeted;
//        PetEdViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}