package classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.petnotes.R;

import java.util.ArrayList;

public class VetVisitListAdapter extends ArrayAdapter<VetVisit> {

    private Context context;
    private ArrayList<VetVisit> vetVisits;

    PetDBHelper dbHelper;

    public VetVisitListAdapter(@NonNull Context context, ArrayList<VetVisit> vetVisits) {
        super(context, 0, vetVisits);
        this.context = context;
        this.vetVisits = vetVisits;

        dbHelper = new PetDBHelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_vaccination, parent, false);
        }

        VetVisit currentVetVisit = vetVisits.get(position);

        TextView nameTextView = listItem.findViewById(R.id.textView_vaccination_name);
        nameTextView.setText(currentVetVisit.getName());

        TextView dateTimeTextView = listItem.findViewById(R.id.textView_vaccination_date_time);
        dateTimeTextView.setText(currentVetVisit.getDateTime());

        return listItem;
    }
}
