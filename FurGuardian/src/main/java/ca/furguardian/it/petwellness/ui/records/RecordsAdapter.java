package ca.furguardian.it.petwellness.ui.records;
//Justin Chipman - N01598472
//Imran Zafurallah - N01585098
//Zane Aransevia - N01351168
//Tevadi Brookes - N01582563
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
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper();
                            databaseHelper.deleteRecord(currentRecord.getDate(), new FirebaseDatabaseHelper.OnRecordOperationListener() {
                                @Override
                                public void onSuccess(String message) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    // Remove the record from the list to reflect the deletion in the UI
                                    records.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
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