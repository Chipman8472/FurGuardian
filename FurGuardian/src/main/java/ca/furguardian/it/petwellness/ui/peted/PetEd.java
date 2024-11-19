package ca.furguardian.it.petwellness.ui.peted;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563

//package ca.furguardian.it.petwellness.ui.peted;

// Imports
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.webkit.WebSettings;
import android.webkit.WebView;
import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.databinding.FragmentPetedBinding;
import java.util.Arrays;
import java.util.List;

public class PetEd extends Fragment {

    private FragmentPetedBinding binding;

    // URL Lists
    private List<String> petNutritionUrls = Arrays.asList("https://www.chewy.com");
    private List<String> groomingTipsUrls = Arrays.asList("https://hastingsvet.com");
    private List<String> vaccinationScheduleUrls = Arrays.asList("https://example.com/vaccination_schedule");
    private List<String> trainingTipsUrls = Arrays.asList(
            "/www.youtube.com/playlist?list=PL1wCnaQRu4BG_RhOZaT4UNspBbRnf4IvJ" ,// Embedded YouTube video
            "https://www.youtube.com/embed/tgbNymZ7vqY"  // Another example
    );

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PetEdViewModel petEdViewModel = new ViewModelProvider(this).get(PetEdViewModel.class);

        binding = FragmentPetedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configure WebView
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript for YouTube embedding
        binding.webView.setVisibility(View.GONE); // Initially hide WebView

        // Setup each spinner with its respective URLs
        setupSpinner(binding.spinnerPetNutrition, petNutritionUrls);
        setupSpinner(binding.spinnerGroomingTips, groomingTipsUrls);
        setupSpinner(binding.spinnerVaccinationSchedule, vaccinationScheduleUrls);
        setupSpinner(binding.spinnerTrainingTips, trainingTipsUrls);

        return root;
    }

    private void setupSpinner(Spinner spinner, List<String> urls) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                urls
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) { // Valid selection
                    String url = urls.get(position);
                    loadContentInWebView(url); // Load the URL in WebView
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    private void loadContentInWebView(String url) {
        binding.webView.setVisibility(View.VISIBLE); // Show WebView
        if (url.contains("youtube.com/embed")) {
            // Embed YouTube video
            String html = "<html><body style='margin:0;padding:0;'><iframe width='100%' height='100%' src='"
                    + url
                    + "' frameborder='0' allow='accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture' allowfullscreen></iframe></body></html>";
            binding.webView.loadData(html, "text/html", "utf-8");
        } else {
            // Load normal webpage
            binding.webView.loadUrl(url);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}














//
//// Imports
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Location;
//import android.location.Geocoder;
//import android.os.Bundle;
//import android.os.Looper;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.activity.OnBackPressedCallback;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.CancellationTokenSource;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//import ca.furguardian.it.petwellness.R;
//import ca.furguardian.it.petwellness.databinding.FragmentPetedBinding;
//
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
//import android.content.Intent;
//import android.net.Uri;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//public class PetEd extends Fragment {
//
//    private FragmentPetedBinding binding;
//    private List<String> petNutritionUrls;
//    private List<String> groomingTipsUrls;
//    private List<String> vaccinationScheduleUrls;
//    private List<String> trainingTipsUrls;
//    private PetEdAdapter adapter;
//    private FusedLocationProviderClient fusedLocationClient;
//    private WebView webView;
//
//    private List<String> petEducationTopics = Arrays.asList(
//            "Pet Nutrition", "Grooming Tips", "Vaccination Schedule", "Training and Obedience", "Exercise Needs"
//    );
//
//    private List<String> defaultUrls = Arrays.asList(
//            "https://www.chewy.com",
//            "https://hastingsvet.com",
//            "https://example.com/vaccination_schedule",
//            "https://www.youtube.com",
//            "https://www.youtube.com"
//    );
//
//    private List<String> canadianUrls = Arrays.asList(
//            "https://www.canadian-pet-food.com",
//            "https://canada-grooming-tips.com",
//            "https://canada-vaccine-schedule.com",
//            "https://www.youtube.com",
//            "https://www.youtube.com"
//    );
//
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        PetEdViewModel petEdViewModel = new ViewModelProvider(this).get(PetEdViewModel.class);
//
//        binding = FragmentPetedBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        webView = binding.webView;
//
//        // Configure WebView
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true); // Enable JavaScript
//        webView.setWebViewClient(new WebViewClient()); // Prevent opening in an external browser
//
//
//        // Initialize FusedLocationProviderClient
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
//
//        // Set the text from ViewModel
//        final TextView textView = binding.textPeted;
//        petEdViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//
//        // Override back button functionality
//        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                new AlertDialog.Builder(requireContext())
//                        .setIcon(R.mipmap.logo)
//                        .setTitle(R.string.exit_app)
//                        .setMessage(R.string.are_you_sure_you_want_to_exit)
//                        .setPositiveButton(R.string.yes, (dialog, which) -> requireActivity().finish())
//                        .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
//                        .show();
//            }
//        });
//
//        // Initialize URLs
//        petNutritionUrls = Arrays.asList("https://www.chewy.com", "https://www.canadian-pet-food.com");
//        groomingTipsUrls = Arrays.asList("https://hastingsvet.com", "https://canada-grooming-tips.com");
//        vaccinationScheduleUrls = Arrays.asList("https://example.com/vaccination_schedule", "https://canada-vaccine-schedule.com");
//        trainingTipsUrls = Arrays.asList("https://www.youtube.com");
//
//        // Initialize Spinners
//        setupSpinner(binding.spinnerPetNutrition, petNutritionUrls);
//        setupSpinner(binding.spinnerGroomingTips, groomingTipsUrls);
//        setupSpinner(binding.spinnerVaccinationSchedule, vaccinationScheduleUrls);
//        setupSpinner(binding.spinnerTrainingTips, trainingTipsUrls);
//
//
//        // Request location and set URLs accordingly
//        requestLocation();
//
//        return root;
//    }
//
//    private void setupSpinner(Spinner spinner, List<String> urls) {
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                getContext(),
//                android.R.layout.simple_spinner_item,
//                urls
//        );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != 0) { // Avoid triggering on the default "Select" option
//                    String url = urls.get(position - 1); // Adjust index for header
//                    loadUrlInWebView(url); // Load the URL in the WebView
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });
//    }
//
//    private void loadUrlInWebView(String url) {
//        webView.setVisibility(View.VISIBLE); // Make the WebView visible
//        webView.loadUrl(url); // Load the URL
//
//
//        private void requestLocation () {
//            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                return;
//            }
//            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, new CancellationTokenSource().getToken())
//                    .addOnSuccessListener(location -> {
//                        if (isAdded() && getContext() != null) { // Check if fragment is still attached
//                            if (location != null) {
//                                setUrlsBasedOnLocation(getContext(), location);
//                            } else {
//                                // Default URLs if location is unavailable
//                                setSpinnerData(defaultUrls, defaultUrls, defaultUrls, defaultUrls);
//                            }
//                        }
//                    });
//        }
//
//        private void setUrlsBasedOnLocation(Context context, Location location) {
//            if (context == null) {
//                setSpinnerData(defaultUrls, defaultUrls, defaultUrls, defaultUrls);
//                return;
//            }
//
//            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//            try {
//                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                if (!addresses.isEmpty()) {
//                    String countryCode = addresses.get(0).getCountryCode();
//                    if ("CA".equals(countryCode)) { // If in Canada
//                        setSpinnerData(
//                                canadianUrls.subList(0, 1),
//                                canadianUrls.subList(1, 2),
//                                canadianUrls.subList(2, 3),
//                                canadianUrls.subList(3, 5)
//                        );
//                    } else { // Default URLs for other locations
//                        setSpinnerData(defaultUrls, defaultUrls, defaultUrls, defaultUrls);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                setSpinnerData(defaultUrls, defaultUrls, defaultUrls, defaultUrls); // Fallback to default URLs in case of an error
//            }
//        }
//
//    }
//
//        private void setSpinnerData(List<String> petNutritionUrls, List<String> groomingTipsUrls, List<String> vaccinationScheduleUrls, List<String> trainingTipsUrls) {
//            // Update each Spinner with the respective URL list
//            setupSpinner(binding.spinnerPetNutrition, petNutritionUrls);
//            setupSpinner(binding.spinnerGroomingTips, groomingTipsUrls);
//            setupSpinner(binding.spinnerVaccinationSchedule, vaccinationScheduleUrls);
//            setupSpinner(binding.spinnerTrainingTips, trainingTipsUrls);
//        }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            requestLocation();
//        } else {
//            // Load default URLs if permission is denied
//            setSpinnerData(defaultUrls, defaultUrls, defaultUrls, defaultUrls);
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}
