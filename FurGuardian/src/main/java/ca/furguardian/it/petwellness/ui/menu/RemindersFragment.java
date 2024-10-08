package ca.furguardian.it.petwellness.ui.menu;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RemindersFragment extends Fragment {

    private List<String> reminders;
    private RecyclerView recyclerView;
    private RemindersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Create a simple layout programmatically
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        Button addReminderButton = new Button(getContext());
        addReminderButton.setText("Add Reminder");
        layout.addView(addReminderButton);

        // Set up RecyclerView
        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        layout.addView(recyclerView);

        // Initialize reminders list
        reminders = new ArrayList<>();
        reminders.add("Feed the dog");
        reminders.add("Give cat medicine");

        // Set up the adapter
        adapter = new RemindersAdapter(reminders);
        recyclerView.setAdapter(adapter);

        // Handle button click to add a new reminder
        addReminderButton.setOnClickListener(v -> {
            addNewReminder("New Reminder");  // Simulate adding a new reminder
        });

        return layout;
    }

    // Method to handle adding a new reminder
    private void addNewReminder(String reminder) {
        reminders.add(reminder);
        adapter.notifyDataSetChanged();  // Refresh the RecyclerView
        Toast.makeText(getContext(), "New reminder added", Toast.LENGTH_SHORT).show();
    }

    // Adapter class defined within the same file
    private static class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder> {

        private final List<String> reminders;

        public RemindersAdapter(List<String> reminders) {
            this.reminders = reminders;
        }

        @NonNull
        @Override
        public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView reminderText = new TextView(parent.getContext());
            reminderText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            reminderText.setPadding(16, 16, 16, 16);
            return new ReminderViewHolder(reminderText);
        }

        @Override
        public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
            holder.reminderText.setText(reminders.get(position));
        }

        @Override
        public int getItemCount() {
            return reminders.size();
        }

        static class ReminderViewHolder extends RecyclerView.ViewHolder {
            TextView reminderText;

            public ReminderViewHolder(@NonNull TextView itemView) {
                super(itemView);
                reminderText = itemView;
            }
        }
    }
}