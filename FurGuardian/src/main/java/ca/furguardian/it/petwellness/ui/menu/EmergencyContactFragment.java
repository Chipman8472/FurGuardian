package ca.furguardian.it.petwellness.ui.menu;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.furguardian.it.petwellness.R;

public class EmergencyContactFragment extends Fragment {

    private LinearLayout contactsContainer;
    private List<Contact> contactsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_contact, container, false);

        // Initialize contacts container
        contactsContainer = view.findViewById(R.id.contactsContainer);

        // Initialize contact list (In practice, this would come from a data source or database)
        contactsList = getContacts(); // Example method to get list of contacts

        // Display each contact in the layout
        displayContacts();

        // Override back button functionality for RemindersFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to the home page instead of exiting the app
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.navigation_home);  // Navigate directly to the home page
            }
        });

        return view;
    }

    private void displayContacts() {
        for (Contact contact : contactsList) {
            // Create a new TextView for each contact
            TextView contactView = new TextView(getContext());
            contactView.setText("Name: " + contact.getName() + "\nPhone: " + contact.getPhoneNumber());
            contactView.setTextSize(20);
            contactView.setPadding(0, 50, 0, 50);
            contactView.setTextColor(getResources().getColor(R.color.black));

            // Add each contact view to the container
            contactsContainer.addView(contactView);
        }
    }

    // Example method to retrieve contacts (replace with real data)
    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("Humberwood Animal Hospital", "416-213-0999"));
        contacts.add(new Contact("Veterinary Emergency Clinic", "416-920-2002"));
        contacts.add(new Contact("Pet Smart", "123-456-7890"));
        contacts.add(new Contact("PetCo", "098-765-4321"));
        // Add more contacts as needed
        return contacts;
    }
}
