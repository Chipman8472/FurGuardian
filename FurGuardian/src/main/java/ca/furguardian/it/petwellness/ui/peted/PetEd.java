package ca.furguardian.it.petwellness.ui.peted;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import ca.furguardian.it.petwellness.R;
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

        // Override back button functionality for RemindersFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // This will show the "Exit App" dialog unless overridden in a fragment
                new AlertDialog.Builder(requireContext())
                        .setIcon(R.mipmap.logo)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requireActivity().finish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();  // Navigate directly to the home page
            }
        });

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

        // Corresponding URLs for each topic
        List<String> petEducationUrls = Arrays.asList(
                "https://www.chewy.com/ca/s?query=pet%20food&utm_source=google&utm_medium=cpc&utm_campaign=20526903563&utm_content=154065940540&gad_source=1&gclid=EAIaIQobChMI3PKMzMD_iAMVby7UAR2qwBgkEAAYASAAEgJcuvD_BwE",
                "https://hastingsvet.com/six-helpful-grooming-tips-for-your-dog-or-cat/",
                "https://example.com/vaccination-schedule",
                "https://www.youtube.com/playlist?list=PL1wCnaQRu4BG_RhOZaT4UNspBbRnf4IvJ",
                "https://www.youtube.com/watch?v=PzsrsRRWZYU"
        );

        // Set the adapter
        adapter = new PetEdAdapter(petEducationTopics,  petEducationUrls, getContext());
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}