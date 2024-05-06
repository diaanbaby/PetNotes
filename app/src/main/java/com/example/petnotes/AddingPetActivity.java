package com.example.petnotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petnotes.databinding.ActivityAddingPetBinding;

import java.io.ByteArrayOutputStream;

import classes.Pet;
import classes.PetContract;
import classes.PetDBHelper;

public class AddingPetActivity extends AppCompatActivity {
    private EditText nameEditText, birthDayEditText, birthMonthEditText, birthYearEditText, weightEditText;
    private ImageView photoImageView;
    private Button addButton, addPhotoButton;
    private PetDBHelper dbHelper;
    private static final int PICK_IMAGE_REQUEST = 1;
    private long petId;

    ActivityAddingPetBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddingPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(getColor(R.color.green));

        dbHelper = new PetDBHelper(this);

        nameEditText = findViewById(R.id.pet_name_edit_text);
        birthDayEditText = findViewById(R.id.enter_birth_day);
        birthMonthEditText = findViewById(R.id.enter_birth_month);
        birthYearEditText = findViewById(R.id.enter_birth_year);
        weightEditText = findViewById(R.id.pet_weight_edit_text);
        photoImageView = findViewById(R.id.image_view_pet_photo);
        addButton = findViewById(R.id.apply_button);
        addPhotoButton = findViewById(R.id.add_new_photo_button);

        Intent intent = getIntent();
        if (intent.hasExtra("pet_id")) {
            petId = intent.getLongExtra("pet_id", -1);
            if (petId != -1) {
                Pet pet = dbHelper.getPet(petId);
                if (pet != null) {
                    nameEditText.setText(pet.getName());
                    String[] birthDateParts = pet.getBirthDate().split("\\.");
                    if (birthDateParts.length == 3) {
                        binding.enterBirthDay.setText(birthDateParts[0]);
                        binding.enterBirthMonth.setText(birthDateParts[1]);
                        binding.enterBirthYear.setText(birthDateParts[2]);
                    }
                    weightEditText.setText(String.valueOf(pet.getWeight()));
                }
            }
        }

        addButton.setOnClickListener(view -> addOrUpdatePet());
        addPhotoButton.setOnClickListener(view -> openGallery());
    }

    private void addOrUpdatePet() {
        String name = nameEditText.getText().toString().trim();
        String birthDay = binding.enterBirthDay.getText().toString().trim();
        String birthMonth = binding.enterBirthMonth.getText().toString().trim();
        String birthYear = binding.enterBirthYear.getText().toString().trim();
        String birthDate = birthDay + "." + birthMonth + "." + birthYear;
        int weight = Integer.parseInt(weightEditText.getText().toString().trim());
        Bitmap photo = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_NAME_NAME, name);
        values.put(PetContract.PetEntry.COLUMN_NAME_BIRTH_DATE, birthDate);
        values.put(PetContract.PetEntry.COLUMN_NAME_WEIGHT, weight);
        values.put(PetContract.PetEntry.COLUMN_NAME_PHOTO, getByteArrayFromBitmap(photo));

        if (petId < 0) {
            long newRowId = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Pet added successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to add pet", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = db.update(PetContract.PetEntry.TABLE_NAME, values,
                    PetContract.PetEntry._ID + "=?", new String[]{String.valueOf(petId)});
            if (rowsAffected > 0) {
                Toast.makeText(this, "Pet updated successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update pet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                photoImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
