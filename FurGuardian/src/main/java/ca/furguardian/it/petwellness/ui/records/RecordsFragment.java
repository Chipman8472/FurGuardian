//Justin Chipman - N01598472
//Imran Zafurallah - N01585098
//Zane Aransevia - N01351168
//Tevadi Brookes - N01582563

package ca.furguardian.it.petwellness.ui.records;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.furguardian.it.petwellness.R;

public class RecordsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecordsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Record> records = new ArrayList<>();
        records.add(new Record("Vet Visit", "Routine check-up", "Details about the vet visit."));
        records.add(new Record("Vaccination", "Rabies vaccine", "Information about rabies vaccine."));
        records.add(new Record("Maintenance", "Cleaned feeder", "Feeder maintenance details."));

        adapter = new RecordsAdapter(records, getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }
}