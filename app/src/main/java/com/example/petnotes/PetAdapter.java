package com.example.petnotes;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import classes.Pet;

public class PetAdapter extends ArrayAdapter<Pet> {
    private Context mContext;
    private ArrayList<Pet> mPetList;

    public PetAdapter(Context context, ArrayList<Pet> petList) {
        super(context, 0, petList);
        mContext = context;
        mPetList = petList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_pet, parent, false);
        }

        Pet currentPet = mPetList.get(position);

        ImageView photoImageView = listItem.findViewById(R.id.imageView_photo);
        photoImageView.setImageBitmap(currentPet.getPhoto());

        TextView nameTextView = listItem.findViewById(R.id.textView_name);
        nameTextView.setText(currentPet.getName());

        TextView birthDateTextView = listItem.findViewById(R.id.textView_birth_date);
        birthDateTextView.setText(currentPet.getBirthDate());

        TextView weightTextView = listItem.findViewById(R.id.textView_weight);
        weightTextView.setText(currentPet.getWeight() + " Kg");

        return listItem;
    }
}
