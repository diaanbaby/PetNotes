package com.example.petnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.petnotes.databinding.ActivityMainBinding;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

import classes.Pet;
import classes.PetContract;
import classes.PetDBHelper;
import classes.PetListAdapter;

public class MainActivity extends AppCompatActivity {
    private ListView petListView;
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    private PetListAdapter petListAdapter;
    private ArrayList<Pet> petList;
    private PetDBHelper dbHelper;

    ActivityMainBinding binding;
    public static final int REQUEST_ADD_PET = 1;
    public static final int REQUEST_EDIT_PET = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(getColor(R.color.green));

        dbHelper = new PetDBHelper(this);

        petListView = findViewById(R.id.pets_list_view);
        petList = new ArrayList<>();
        petAdapter = new PetAdapter(this, petList);
        petListAdapter = new PetListAdapter(this, petList);
        petListView.setAdapter(petAdapter);

        loadPetsFromDatabase();

        binding.addPetButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddingPetActivity.class);
            intent.putExtra("pet_id", -1);
            startActivityForResult(intent, REQUEST_ADD_PET);
        });

        binding.petsListView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            Pet selectedPet = petList.get(position);
            showEditOrDeleteDialog(selectedPet);
            return true;
        });

        binding.petsListView.setOnItemClickListener((adapterView, view, position, id) -> {
            Pet selectedPet = petList.get(position);
            Intent intent = new Intent(MainActivity.this, PetDetailsActivity.class);
            intent.putExtra("pet_id", selectedPet.getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_PET && resultCode == RESULT_OK) {
            loadPetsFromDatabase();
        } else if (requestCode == REQUEST_EDIT_PET && resultCode == RESULT_OK) {
            loadPetsFromDatabase();
        }
    }

    private void showEditOrDeleteDialog(Pet pet) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_action)
                .setItems(new CharSequence[]{getString(R.string.edit), getString(R.string.delete)}, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit
                            editPet(pet);
                            break;
                        case 1: // Delete
                            deletePet(pet);
                            break;
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void editPet(Pet pet) {
        Intent intent = new Intent(MainActivity.this, AddingPetActivity.class);
        intent.putExtra("pet_id", pet.getId());
        startActivityForResult(intent, REQUEST_EDIT_PET);
    }

    private void deletePet(Pet pet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete(
                PetContract.PetEntry.TABLE_NAME,
                PetContract.PetEntry._ID + "=?",
                new String[]{String.valueOf(pet.getId())}
        );

        if (deletedRows > 0) {
            Toast.makeText(this, "Pet deleted successfully!", Toast.LENGTH_SHORT).show();
            loadPetsFromDatabase();
        } else {
            Toast.makeText(this, "Failed to delete pet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPetsFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_NAME_NAME,
                PetContract.PetEntry.COLUMN_NAME_BIRTH_DATE,
                PetContract.PetEntry.COLUMN_NAME_WEIGHT,
                PetContract.PetEntry.COLUMN_NAME_PHOTO
        };

        Cursor cursor = db.query(
                PetContract.PetEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        petList.clear();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(PetContract.PetEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_NAME));
            String birthDate = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_BIRTH_DATE));
            int weight = cursor.getInt(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_WEIGHT));
            byte[] photoByteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_PHOTO));
            Bitmap photo = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.length);
            petList.add(new Pet(id, name, birthDate, weight, photo));
        }
        cursor.close();
        petAdapter.notifyDataSetChanged();
    }
}