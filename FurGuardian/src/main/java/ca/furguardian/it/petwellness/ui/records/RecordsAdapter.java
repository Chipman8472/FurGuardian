package ca.furguardian.it.petwellness.ui.records;

import android.content.Context;
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

    private List<Record> records;
    private Context context;

    public RecordsAdapter(List<Record> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = records.get(position);
        holder.textTitle.setText(record.getSummary());
        holder.textDate.setText(record.getDate());
        holder.textDetails.setText(record.getDetails());

        boolean isExpanded = record.isExpanded();
        holder.textDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.buttonToggle.setText(isExpanded ? "View Less" : "View More");

        holder.buttonToggle.setOnClickListener(v -> {
            record.setExpanded(!isExpanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDate, textDetails;
        Button buttonToggle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textDate = itemView.findViewById(R.id.text_summary);
            textDetails = itemView.findViewById(R.id.text_details);
            buttonToggle = itemView.findViewById(R.id.button_toggle);
        }
    }
}