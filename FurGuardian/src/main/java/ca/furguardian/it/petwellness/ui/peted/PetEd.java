package ca.furguardian.it.petwellness.ui.peted;

// Imports
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.databinding.FragmentPetedBinding;

public class PetEd extends Fragment {

    private FragmentPetedBinding binding;
    private RecyclerView recyclerView;
    private PetEdAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;

    private List<String> petEducationTopics = Arrays.asList(
            "Pet Nutrition", "Grooming Tips", "Vaccination Schedule", "Training and Obedience", "Exercise Needs"
    );

    private List<String> defaultUrls = Arrays.asList(
            "https://www.chewy.com",
            "https://hastingsvet.com",
            "https://example.com/vaccination_schedule",
            "https://www.youtube.com",
            "https://www.youtube.com"
    );

    private List<String> canadianUrls = Arrays.asList(
            "https://www.canadian-pet-food.com",
            "https://canada-grooming-tips.com",
            "https://canada-vaccine-schedule.com",
            "https://www.youtube.com",
            "https://www.youtube.com"
    );

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PetEdViewModel petEdViewModel = new ViewModelProvider(this).get(PetEdViewModel.class);

        binding = FragmentPetedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

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

        // Initialize RecyclerView and set an empty adapter immediately
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PetEdAdapter(new ArrayList<>(), new ArrayList<>(), getContext()); // Empty adapter to prevent delay
        recyclerView.setAdapter(adapter);

        // Request location and set URLs accordingly
        requestLocation();

        return root;
    }

    private void setRecyclerViewData(List<String> topics, List<String> urls) {
        // Update the adapter with actual data once available
        adapter = new PetEdAdapter(topics, urls, getContext());
        recyclerView.setAdapter(adapter);
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, new CancellationTokenSource().getToken())
                .addOnSuccessListener(location -> {
                    if (isAdded() && getContext() != null) { // Check if fragment is still attached
                        if (location != null) {
                            setUrlsBasedOnLocation(getContext(), location);
                        } else {
                            // Default URLs if location is unavailable
                            setRecyclerViewData(petEducationTopics, defaultUrls);
                        }
                    }
                });
    }

    private void setUrlsBasedOnLocation(Context context, Location location) {
        if (context == null) {
            setRecyclerViewData(petEducationTopics, defaultUrls);
            return;
        }

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                String countryCode = addresses.get(0).getCountryCode();
                if ("CA".equals(countryCode)) { // Adjusted for Canada
                    setRecyclerViewData(petEducationTopics, canadianUrls);
                } else {
                    setRecyclerViewData(petEducationTopics, defaultUrls);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            setRecyclerViewData(petEducationTopics, defaultUrls); // Load default URLs if there's an error
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            // Load default URLs if permission is denied
            setRecyclerViewData(petEducationTopics, defaultUrls);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
