package ca.furguardian.it.petwellness.ui.menu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EmergencyContactFragment extends Fragment {

    private RecyclerView contactsRecyclerView;
    private ContactsAdapter contactsAdapter;
    private List<EmergencyContactFragment> emergencyContacts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_contact, container, false);

        // Initialize RecyclerView
        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerView);
        contactsAdapter = new ContactsAdapter(emergencyContacts, this::makeCall, this::sendMessage);
        contactsRecyclerView.setAdapter(contactsAdapter);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Handle adding a new contact
        Button addContactButton = view.findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(v -> {
            // Code to add a new contact (e.g., open a dialog to input contact details)
        });

        return view;
    }

    private void makeCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }

    private void sendMessage(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", "This is an emergency message!");
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, proceed with the intended action if necessary
        }
    }

    public int getPhoneNumber() {
        return 0;
    }

    public int getName() {
        return 0;
    }
}
