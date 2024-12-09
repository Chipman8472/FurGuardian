package ca.furguardian.it.petwellness.ui.records;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.furguardian.it.petwellness.R;

public class RecordsFragment extends Fragment {

    private static final String ARG_PET_ID = "pet_id";

    private RecordsAdapter adapter;
    private List<Record> records;
    private DatabaseReference databaseReference;
    private String petId;

    public RecordsFragment() {
        // Required empty constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        petId = sharedPreferences.getString("currentpet", null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button addRecordButton = view.findViewById(R.id.addRecordButton);
        records = new ArrayList<>();
        adapter = new RecordsAdapter(records, getContext(), petId);
        recyclerView.setAdapter(adapter);

        // Validate petId
        if (petId == null || petId.isEmpty()) {
            Toast.makeText(getContext(), "Pet ID is missing. Cannot load records.", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Initialize Firebase reference
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("pets")
                    .child(petId)
                    .child("records");
        }

        loadRecordsFromFirebase();

        addRecordButton.setOnClickListener(v -> {
            if (petId == null || petId.isEmpty()) {
                Toast.makeText(getContext(), "Pet ID is missing. Cannot add record.", Toast.LENGTH_SHORT).show();
                return;
            }

            AddRecordDialogFragment dialog = AddRecordDialogFragment.newInstance(petId);
            dialog.show(getParentFragmentManager(), "AddRecordDialogFragment");
        });


        return view;
    }


    private void loadRecordsFromFirebase() {
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    records.clear();
                    for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                        MedicalRecord medicalRecord = recordSnapshot.getValue(MedicalRecord.class);
                        if (medicalRecord != null) {
                            records.add(new Record(
                                    medicalRecord.getUid(),
                                    medicalRecord.getDate(),
                                    medicalRecord.getType(),
                                    medicalRecord.getDetails()
                            ));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load records.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
