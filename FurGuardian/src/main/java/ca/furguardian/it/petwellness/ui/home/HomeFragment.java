// Justin Chipman - RCB – N01598472
// Imran Zafurallah - RCB - N01585098
// Zane Aransevia - RCB- N01351168
// Tevadi Brookes - RCC - N01582563
package ca.furguardian.it.petwellness.ui.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Simulate data for Steps
        binding.textStepsToday.setText(getString(R.string.steps_4500));
        binding.progressStepsGoal.setProgress(4500);  // Update with the real-time step count

        // Simulate data for Device Records
        binding.textFoodDispenserUsage.setText(getString(R.string.last_feeding_08_30_am));

        // Simulate a rotating Pet Education Tip
        binding.textPetTip.setText(getString(R.string.tip_keep_your_pet_hydrated));

        // Set up placeholders for Camera, Microphone, and Speaker
        binding.imageCamera.setOnClickListener(v -> {
            Toast.makeText(getContext(), getString(R.string.camera_connecting), Toast.LENGTH_SHORT).show();

            // Use Handler to create a delay
            new Handler().postDelayed(() -> {
                Toast.makeText(getContext(), getString(R.string.camera_connected), Toast.LENGTH_SHORT).show();
            }, 3000); // Delay
        });

        binding.imageMicrophone.setOnClickListener(v -> {
            Toast.makeText(getContext(), getString(R.string.microphone_start_speaking), Toast.LENGTH_SHORT).show();

            // Use Handler to create a delay
            new Handler().postDelayed(() -> {
                Toast.makeText(getContext(), getString(R.string.microphone_sent), Toast.LENGTH_SHORT).show();
            }, 3000); // Delay
        });

        binding.imageSpeaker.setOnClickListener(v -> {
            Toast.makeText(getContext(), getString(R.string.speaker_playing_audio), Toast.LENGTH_SHORT).show();
        });

        // Override back button functionality for RemindersFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
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
                        .show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
