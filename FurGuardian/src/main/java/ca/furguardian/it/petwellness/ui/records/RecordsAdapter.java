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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ca.furguardian.it.petwellness.R;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder> {

    private final List<Record> records;
    private final Context context;
    private final String petId;

    public RecordsAdapter(List<Record> records, Context context, String petId) {
        this.records = records;
        this.context = context;
        this.petId = petId;
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
        holder.buttonDelete.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.buttonToggle.setText(isExpanded ? context.getString(R.string.view_less) : context.getString(R.string.view_more));

        holder.buttonToggle.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Record currentRecord = records.get(currentPosition);
                currentRecord.setExpanded(!currentRecord.isExpanded());
                notifyItemChanged(currentPosition);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Record currentRecord = records.get(currentPosition);
                new AlertDialog.Builder(context)
                        .setTitle(R.string.delete_record)
                        .setMessage(R.string.are_you_sure_delete)
                        .setPositiveButton(R.string.yes2, (dialog, which) -> {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                DatabaseReference recordRef = FirebaseDatabase.getInstance()
                                        .getReference("users").child(userId).child("pets").child(petId).child("records").child(currentRecord.getId());

                                recordRef.removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            records.remove(currentPosition);
                                            notifyItemRemoved(currentPosition);
                                            Toast.makeText(context, "Record deleted!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete record.", Toast.LENGTH_SHORT).show());
                            }
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
