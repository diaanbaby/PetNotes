package classes;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.petnotes.R;

import java.util.ArrayList;

import classes.Vaccination;

public class VaccinationListAdapter extends ArrayAdapter<Vaccination> {

    private Context context;
    private ArrayList<Vaccination> vaccinations;

    ListView listViewVaccinations;

    public VaccinationListAdapter(@NonNull Context context, ArrayList<Vaccination> vaccinations) {
        super(context, 0, vaccinations);
        this.context = context;
        this.vaccinations = vaccinations;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_vaccination, parent, false);
        }

        Vaccination currentVaccination = vaccinations.get(position);

        TextView nameTextView = listItem.findViewById(R.id.textView_vaccination_name);
        nameTextView.setText(currentVaccination.getName());

        TextView dateTimeTextView = listItem.findViewById(R.id.textView_vaccination_date_time);
        dateTimeTextView.setText(currentVaccination.getDateTime());

        return listItem;
    }
}
