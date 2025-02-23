package ca.furguardian.it.petwellness.ui.health;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.furguardian.it.petwellness.R;

public class HealthFragment extends Fragment {

    private WebView webView;
    private DatabaseReference streamUrlRef;
    private DatabaseReference dispenseRef;

    public HealthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment (fragment_health.xml)
        View view = inflater.inflate(R.layout.fragment_health, container, false);

        // Find the views
        webView = view.findViewById(R.id.webView);
        Button btnDispense = view.findViewById(R.id.btnDispense);

        // Configure the WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // Initialize Firebase Database references
        streamUrlRef = FirebaseDatabase.getInstance().getReference("cameraStream/url");
        dispenseRef = FirebaseDatabase.getInstance().getReference("dispense");

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

        // Handle button click: set "dispense" to true
        btnDispense.setOnClickListener(v -> {
            dispenseRef.setValue(true);
        });

        return view;
    }
}
