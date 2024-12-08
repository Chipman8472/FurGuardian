package ca.furguardian.it.petwellness.ui.records;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.furguardian.it.petwellness.R;
import ca.furguardian.it.petwellness.model.PetModel;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    private final List<Record> records;
    private final Context context;

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
        Record record = records.get(position); // Get the current record
        holder.textTitle.setText(record.getSummary());
        holder.textDate.setText(record.getDate());
        holder.textDetails.setText(record.getDetails());

        // Handle expansion toggle
        boolean isExpanded = record.isExpanded();
        holder.textDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.buttonDelete.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.buttonToggle.setText(isExpanded ? context.getString(R.string.view_less) : context.getString(R.string.view_more));

        // Set up the toggle button
        holder.buttonToggle.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Record currentRecord = records.get(currentPosition);
                currentRecord.setExpanded(!currentRecord.isExpanded());
                notifyItemChanged(currentPosition); // Notify adapter about the change
            }
        });

        // Set up the delete button
        holder.buttonDelete.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Record currentRecord = records.get(currentPosition);

                // Show a confirmation dialog
                new AlertDialog.Builder(context)
                        .setTitle(R.string.delete_record)
                        .setMessage(R.string.are_you_sure_delete)
                        .setPositiveButton(R.string.yes2, (dialog, which) -> {
                            PetModel databaseHelper = new PetModel();
                            databaseHelper.deleteRecord(currentRecord.getDate(), new PetModel.OnRecordOperationListener() {
                                @Override
                                public void onSuccess(String message) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    // Ensure the index is valid before removing the item
                                    if (currentPosition >= 0 && currentPosition < records.size()) {
                                        records.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, records.size()); // Update positions of remaining items
                                    } else {
                                        Toast.makeText(context, R.string.invalid_record_index, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDate, textDetails;
        Button buttonToggle, buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textDate = itemView.findViewById(R.id.text_summary);
            textDetails = itemView.findViewById(R.id.text_details);
            buttonToggle = itemView.findViewById(R.id.button_toggle);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}
