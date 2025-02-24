package ca.furguardian.it.petwellness.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private WebView webView;
    private DatabaseReference streamUrlRef;

    private FragmentHomeBinding binding;
    private final Handler handler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        webView = root.findViewById(R.id.webView);

        // Simulate data for Steps
        binding.textStepsToday.setText(getString(R.string.steps_4500));
        binding.progressStepsGoal.setProgress(4500);  // Update with the real-time step count

        // Configure the WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // Initialize Firebase Database references
        streamUrlRef = FirebaseDatabase.getInstance().getReference("cameraStream/url");

        // Simulate data for Device Records
        binding.textFoodDispenserUsage.setText(getString(R.string.last_feeding_08_30_am));

        // Simulate a rotating Pet Education Tip
        binding.textPetTip.setText(getString(R.string.tip_keep_your_pet_hydrated));

        // Set up placeholders for Camera, Microphone, and Speaker
        binding.imageCamera.setOnClickListener(v -> handleCameraClick());
        binding.imageMicrophone.setOnClickListener(v -> handleMicrophoneClick());
        binding.imageSpeaker.setOnClickListener(v -> handleSpeakerClick());

        // Override back button functionality for HomeFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(requireContext())
                        .setIcon(R.mipmap.logo)
                        .setTitle(R.string.exit_app)
                        .setMessage(R.string.are_you_sure_you_want_to_exit)
                        .setPositiveButton(R.string.exit, (dialog, which) -> requireActivity().moveTaskToBack(true))
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        startStream();

        return root;
    }

    private void handleCameraClick() {
        Toast.makeText(requireContext(), getString(R.string.camera_connecting), Toast.LENGTH_SHORT).show();
        handler.postDelayed(() ->
                Toast.makeText(requireContext(), getString(R.string.camera_connected), Toast.LENGTH_SHORT).show(), 3000);
    }

    private void handleMicrophoneClick() {
        Toast.makeText(requireContext(), getString(R.string.microphone_start_speaking), Toast.LENGTH_SHORT).show();
        handler.postDelayed(() ->
                Toast.makeText(requireContext(), getString(R.string.microphone_sent), Toast.LENGTH_SHORT).show(), 3000);
    }

    private void handleSpeakerClick() {
        Toast.makeText(requireContext(), getString(R.string.speaker_playing_audio), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null); // Clear all handler callbacks
        binding = null;
    }

    @Override
    public void onResume() {
        startStream();
        super.onResume();
    }

    public void startStream(){
        // Listen for changes to the stream URL
        streamUrlRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String streamUrl = snapshot.getValue(String.class);
                if (streamUrl != null && !streamUrl.isEmpty()) {
                    webView.loadUrl(streamUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors here (e.g., log them)
            }
        });
    }
}
