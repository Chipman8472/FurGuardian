package ca.furguardian.it.petwellness.ui.peted;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ca.furguardian.it.petwellness.R;
//       Justin Chipman - RCB – N01598472
//	     Imran Zafurallah - RCB - N01585098
//	     Zane Aransevia - RCB- N01351168
//	     Tevadi Brookes - RCC - N01582563
public class PetEdAdapter extends RecyclerView.Adapter<PetEdAdapter.ViewHolder> {

    private List<String> mData;
    private List<String> mUrls;  // Declare the URLs list
    private Context mContext;    // Declare the context

    public PetEdAdapter(List<String> data, List<String> urls, Context context) {
        this.mData = data;
        this.mUrls = urls;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peted, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = mData.get(position);
        holder.textView.setText(item);

        // Handle item click for adding to calendar
        holder.itemView.setOnClickListener(v -> {
            if (item.equals("Vaccination Schedule")) {
                addEventToCalendar("Pet Vaccination", "Pet Vaccination Schedule", System.currentTimeMillis() + 86400000);  //  1 day from now
            } else {
                String url = mUrls.get(position); // Get the URL for the clicked item
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(intent);  // Open URL in the browser
            }
        });
    }

    // Method to add event to the calendar
    private void addEventToCalendar(String title, String description, long startTimeInMillis) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Pet Clinic")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeInMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTimeInMillis + 60 * 60 * 1000);  // 1 hour duration

        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_item);
        }
    }
}