package ca.furguardian.it.petwellness.ui.records;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.furguardian.it.petwellness.R;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    private final List<MedicalRecord> recordsList;

    public RecordsAdapter(List<MedicalRecord> recordsList) {
        this.recordsList = recordsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_medical_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicalRecord record = recordsList.get(position);
        holder.recordDateTextView.setText("Date: " + record.getDate());
        holder.recordTypeTextView.setText("Type: " + record.getType());
        holder.recordDetailsTextView.setText("Details: " + record.getDetails());

        holder.deleteRecordButton.setOnClickListener(view -> {
            recordsList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recordDateTextView, recordTypeTextView, recordDetailsTextView;
        Button deleteRecordButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recordDateTextView = itemView.findViewById(R.id.recordDateTextView);
            recordTypeTextView = itemView.findViewById(R.id.recordTypeTextView);
            recordDetailsTextView = itemView.findViewById(R.id.recordDetailsTextView);
            deleteRecordButton = itemView.findViewById(R.id.deleteRecordButton);
        }
    }
}
