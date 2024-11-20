package ca.furguardian.it.petwellness.ui.peted;

// Imports
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.databinding.FragmentPetedBinding;

public class PetEd extends Fragment {

    private FragmentPetedBinding binding;
    private Spinner spinner;
    private WebView webView;
    private FusedLocationProviderClient fusedLocationClient;
    private ExecutorService executor;

    private List<String> petEducationTopics = Arrays.asList(
            "Pet Nutrition", "Grooming Tips", "Vaccination Schedule", "Training and Obedience", "Exercise Needs"
    );

    private List<String> defaultUrls = Arrays.asList(
            "https://www.chewy.com",
            "https://hastingsvet.com",
            "https://example.com/vaccination_schedule",
            "https://www.youtube.com/playlist?list=PL1wCnaQRu4BG_RhOZaT4UNspBbRnf4IvJ",
            "https://www.youtube.com/watch?v=PzsrsRRWZYU"
    );

    private List<String> canadianUrls = Arrays.asList(
            "https://www.canadian-pet-food.com",
            "https://canada-grooming-tips.com",
            "https://canada-vaccine-schedule.com",
            "https://www.youtube.com/playlist?list=PL1wCnaQRu4BG_RhOZaT4UNspBbRnf4IvJ",
            "https://www.youtube.com/watch?v=PzsrsRRWZYU"
    );

    private List<String> currentUrls;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PetEdViewModel petEdViewModel = new ViewModelProvider(this).get(PetEdViewModel.class);

        binding = FragmentPetedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ExecutorService for background tasks
        executor = Executors.newSingleThreadExecutor();

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Set the text from ViewModel
        final TextView textView = binding.textPeted;
        petEdViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Override back button functionality
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(requireContext())
                        .setIcon(R.mipmap.logo)
                        .setTitle(R.string.exit_app)
                        .setMessage(R.string.are_you_sure_you_want_to_exit)
                        .setPositiveButton(R.string.yes, (dialog, which) -> requireActivity().finish())
                        .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        // Initialize Spinner
        spinner = binding.spinner;
        setSpinnerData(petEducationTopics, defaultUrls); // Use default data for initial setup

        // Fetch location and update URLs in the background
        executor.execute(this::fetchAndSetLocationData);

        return root;
    }

    private void setSpinnerData(List<String> topics, List<String> urls) {
        // Update current URLs
        currentUrls = urls;

        // Ensure Fragment is attached and spinner is initialized
        if (!isAdded() || spinner == null) {
            return; // Exit if Fragment is not in a valid state
        }

        requireActivity().runOnUiThread(() -> {
            try {
                // Initialize spinner with topics
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, topics);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                // Set OnItemSelectedListener
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            if (position < petEducationTopics.size() && petEducationTopics.get(position).equals(getString(R.string.vaccination_schedule1))) {
                                addEventToCalendar(
                                        getString(R.string.pet_vaccination),
                                        getString(R.string.pet_vaccination_schedule),
                                        System.currentTimeMillis() + 86400000
                                );
                            } else if (position < currentUrls.size()) {
                                initializeWebView(currentUrls.get(position)); // Lazy initialize WebView
                            }
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace(); // Log and handle index errors gracefully
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Handle when no item is selected, if necessary
                    }
                });
            } catch (IllegalStateException e) {
                e.printStackTrace(); // Log and handle invalid state gracefully
            }
        });
    }


    private void initializeWebView(String url) {
        if (webView == null) {
            webView = binding.webView;
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webView.setWebViewClient(new WebViewClient());
            webView.setWebChromeClient(new WebChromeClient());
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webView.loadUrl(url);
    }

    private void fetchAndSetLocationData() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            setSpinnerData(petEducationTopics, defaultUrls); // Fallback to default
            return;
        }

        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, new CancellationTokenSource().getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        updateUrlsBasedOnLocation(location);
                    } else {
                        setSpinnerData(petEducationTopics, defaultUrls); // Fallback to default
                    }
                });
    }

    private void updateUrlsBasedOnLocation(Location location) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                String countryCode = addresses.get(0).getCountryCode();
                if ("CA".equals(countryCode)) {
                    setSpinnerData(petEducationTopics, canadianUrls);
                } else {
                    setSpinnerData(petEducationTopics, defaultUrls);
                }
            } else {
                setSpinnerData(petEducationTopics, defaultUrls); // Fallback if no address found
            }
        } catch (Exception e) {
            e.printStackTrace();
            setSpinnerData(petEducationTopics, defaultUrls); // Fallback on error
        }
    }

    private void addEventToCalendar(String title, String description, long startTimeInMillis) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Pet Clinic")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeInMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTimeInMillis + 60 * 60 * 1000); // 1 hour duration

        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executor != null) {
            executor.shutdown(); // Clean up executor
        }
        binding = null;
    }
}
