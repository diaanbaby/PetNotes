package classes;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petnotes.R;

import java.util.ArrayList;
import java.util.List;

public class PetListAdapter extends ArrayAdapter<Pet> {
    private Context mContext;
    private List<Pet> mPetList;
    private PetDBHelper dbHelper;

    public PetListAdapter(Context context, ArrayList<Pet> list) {
        super(context, 0 , list);
        mContext = context;
        mPetList = list;
        dbHelper = new PetDBHelper(context);
    }

    @Override
    public int getCount() {
        return mPetList.size();
    }

    @Override
    public Pet getItem(int position) {
        return mPetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_pet,parent,false);

        Pet currentPet = mPetList.get(position);

        TextView name = listItem.findViewById(R.id.textView_name);
        name.setText(currentPet.getName());

        TextView birthDate = listItem.findViewById(R.id.textView_birth_date);
        birthDate.setText(currentPet.getBirthDate());

        ImageView petImage = listItem.findViewById(R.id.imageView_pet_photo);
        petImage.setImageBitmap(currentPet.getPhoto());

        return listItem;
    }

    static class ViewHolder {
        ImageView petImageView;
        TextView nameTextView;
        TextView birthDateTextView;
    }

    public void deletePet(long id) {
        deletePetFromDatabase(id);
    }


    private void deletePetFromDatabase(long petId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = PetContract.PetEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(petId) };
        int deletedRows = db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
        if (deletedRows > 0) {
            Toast.makeText(mContext, "Pet deleted from database", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Failed to delete pet from database", Toast.LENGTH_SHORT).show();
        }
    }
}
