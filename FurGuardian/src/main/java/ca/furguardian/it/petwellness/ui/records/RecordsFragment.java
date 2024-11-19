//Justin Chipman - N01598472
//Imran Zafurallah - N01585098
//Zane Aransevia - N01351168
//Tevadi Brookes - N01582563

package ca.furguardian.it.petwellness.ui.records;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.furguardian.it.petwellness.R;

public class RecordsFragment extends Fragment {

    private DatePicker recordsDatePicker;
    private Button searchButton;
    private RecyclerView recordsRecyclerView;
    private FloatingActionButton addRecordFab;

    private RecordsAdapter recordsAdapter;
    private List<MedicalRecord> recordsList = new ArrayList<>();

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_records, container, false);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("pets");

        // Initialize UI components
        recordsDatePicker = rootView.findViewById(R.id.recordsDatePicker);
        searchButton = rootView.findViewById(R.id.searchButton);
        recordsRecyclerView = rootView.findViewById(R.id.recordsRecyclerView);
        addRecordFab = rootView.findViewById(R.id.addRecordFab);

        // Set up RecyclerView
        recordsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recordsAdapter = new RecordsAdapter(recordsList);
        recordsRecyclerView.setAdapter(recordsAdapter);

        // Handle Search Button Click
        searchButton.setOnClickListener(view -> {
            int selectedDay = recordsDatePicker.getDayOfMonth();
            int selectedMonth = recordsDatePicker.getMonth() + 1; // Months are zero-based
            int selectedYear = recordsDatePicker.getYear();

            String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, selectedDay);
            fetchRecords(selectedYear, selectedMonth);
        });

        // Handle Add Record FloatingActionButton Click
        addRecordFab.setOnClickListener(view -> openAddRecordDialog());

        return rootView;
    }

    private void fetchRecords(int year, int month) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recordsList.clear();
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot recordSnapshot : petSnapshot.child("records").getChildren()) {
                        MedicalRecord record = recordSnapshot.getValue(MedicalRecord.class);

                        // Filter records by selected year and month
                        if (record != null) {
                            String[] dateParts = record.getDate().split("-");
                            int recordYear = Integer.parseInt(dateParts[0]);
                            int recordMonth = Integer.parseInt(dateParts[1]);

                            if (recordYear == year && recordMonth == month) {
                                recordsList.add(record);
                            }
                        }
                    }
                }

                recordsAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), recordsList.size() + " records found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch records: " + error.getMessage());
            }
        });
    }

    private void openAddRecordDialog() {
        AddRecordDialogFragment dialogFragment = new AddRecordDialogFragment();
        dialogFragment.show(getChildFragmentManager(), "AddRecordDialog");
    }
}
