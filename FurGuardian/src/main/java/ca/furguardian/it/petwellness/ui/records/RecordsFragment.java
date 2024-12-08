package ca.furguardian.it.petwellness.ui.records;
//Justin Chipman - N01598472
//Imran Zafurallah - N01585098
//Zane Aransevia - N01351168
//Tevadi Brookes - N01582563
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.furguardian.it.petwellness.R;

public class RecordsFragment extends Fragment {

    private RecordsAdapter adapter;
    private List<Record> records;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button addRecordButton = view.findViewById(R.id.addRecordButton);
        records = new ArrayList<>();
        adapter = new RecordsAdapter(records, getContext());
        recyclerView.setAdapter(adapter);

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("pets").child("12334").child("records");

        // Load existing records from Firebase
        loadRecordsFromFirebase();

        // Open Add Record dialog
        addRecordButton.setOnClickListener(v -> {
            AddRecordDialogFragment dialog = new AddRecordDialogFragment();
            dialog.show(getParentFragmentManager(), "AddRecordDialogFragment");
        });

        return view;
    }

    private void loadRecordsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                records.clear(); // Clear the list to avoid duplication
                for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                    MedicalRecord medicalRecord = recordSnapshot.getValue(MedicalRecord.class);
                    if (medicalRecord != null) {
                        records.add(new Record(
                                medicalRecord.getDate(),
                                medicalRecord.getType(),
                                medicalRecord.getDetails()
                        ));
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), R.string.couldn_t_get_records_from_db,Toast.LENGTH_SHORT).show();
            }
        });
    }
}