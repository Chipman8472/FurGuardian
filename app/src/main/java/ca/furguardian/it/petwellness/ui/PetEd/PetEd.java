package ca.furguardian.it.petwellness.ui.PetEd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import ca.furguardian.it.petwellness.databinding.FragmentPetedBinding;

public class PetEd extends Fragment {

    private FragmentPetedBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PetEdViewModel PetEdViewModel =
                new ViewModelProvider(this).get(PetEdViewModel.class);

        binding = FragmentPetedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPeted;
        PetEdViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}