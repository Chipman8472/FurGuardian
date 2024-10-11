package ca.furguardian.it.petwellness.ui.peted;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
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
                        .setTitle(R.string.exit_app)
                        .setMessage(R.string.are_you_sure_you_want_to_exit)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requireActivity().finish();

                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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
                getString(R.string.pet_nutrition),
                getString(R.string.grooming_tips),
                getString(R.string.vaccination_schedule),
                getString(R.string.training_and_obedience),
                getString(R.string.exercise_needs)
        );

        // Corresponding URLs for each topic
        List<String> petEducationUrls = Arrays.asList(
                getString(R.string.https_www_chewy_com_ca_s_query_pet_20food_utm_source_google_utm_medium_cpc_utm_campaign_20526903563_utm_content_154065940540_gad_source_1_gclid_eaiaiqobchmi3pkmzmd_iamvby7uar2qwbgkeaayasaaegjcuvd_bwe),
                getString(R.string.https_hastingsvet_com_six_helpful_grooming_tips_for_your_dog_or_cat),
                getString(R.string.https_example_com_vaccination_schedule),
                getString(R.string.https_www_youtube_com_playlist_list_pl1wcnaqru4bg_rhozat4unspbbrnf4ivj),
                getString(R.string.https_www_youtube_com_watch_v_pzsrsrrwzyu)
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