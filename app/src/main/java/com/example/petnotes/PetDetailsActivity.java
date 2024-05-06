package com.example.petnotes;

import static java.security.AccessController.getContext;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.petnotes.databinding.ActivityPetDetailsBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import classes.AlarmReceiver;
import classes.NotificationReceiver;
import classes.NotificationScheduler;
import classes.Pet;
import classes.PetContract;
import classes.PetDBHelper;
import classes.PetDetailsPagerAdapter;
import classes.PetListAdapter;
import classes.Vaccination;
import classes.VetVisit;

public class PetDetailsActivity extends AppCompatActivity {

    ActivityPetDetailsBinding binding;
    private PetDBHelper dbHelper;
    private Pet pet;

    VetVisitsFragment vetVisitsFragment;
    VaccinationsFragment vaccinationsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPetDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(PetDetailsActivity.this, R.color.green));
        createNotificationChannel();

        dbHelper = new PetDBHelper(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pet_id")) {
            long petId = intent.getLongExtra("pet_id", -1);
            if (petId != -1) {
                pet = getPetFromDatabase(petId);
                if (pet != null) {
                    binding.textViewPetName.setText(pet.getName());
                    binding.textViewBirthDate.setText(pet.getBirthDate());
                    binding.textViewWeight.setText(String.valueOf(pet.getWeight()));
                    binding.imageViewPetPhoto.setImageBitmap(pet.getPhoto());
                }
            }
        }

        binding.cancelAddNewNoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (binding.addNewNoteLayout.getVisibility() == View.VISIBLE) {
                    binding.noteDateEditText.setText("");
                    binding.noteNameEditText.setText("");
                    binding.addNewNoteLayout.setVisibility(View.GONE);
                }
                else
                {
                    if (isEnabled()) {
                        setEnabled(false);
                        onBackPressed();
                    }
                }
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);


        binding.noteDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(PetDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int dayOfMonth) {
                                binding.noteDateEditText.setText(selectedYear + "-" + (selectedMonth + 1) + "-" + dayOfMonth);

                                Calendar timeCalendar = Calendar.getInstance();
                                int hourOfDay = timeCalendar.get(Calendar.HOUR_OF_DAY);
                                int minute = timeCalendar.get(Calendar.MINUTE);

                                TimePickerDialog timePickerDialog = new TimePickerDialog(PetDetailsActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                binding.noteDateEditText.setText(binding.noteDateEditText.getText().toString() +
                                                        " " + hourOfDay + ":" + minute);

                                                binding.addNewNoteButton.setBackgroundColor(ContextCompat.getColor(PetDetailsActivity.this, R.color.green));
                                                binding.addNewNoteButton.setEnabled(true);
                                            }
                                        }, hourOfDay, minute, true);

                                timePickerDialog.show();
                            }
                        }, year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });


        setupViewPager();

        binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedTabIndex = binding.tabLayoutNotes.getSelectedTabPosition();
                switch (selectedTabIndex) {
                    case 1:
                        showAddNoteDialog(true);
                        break;
                    case 0:
                        showAddNoteDialog(false);
                        break;
                }
            }
        });
    }

    private void setupViewPager() {
        PetDetailsPagerAdapter adapter = new PetDetailsPagerAdapter(getSupportFragmentManager());

        vetVisitsFragment = VetVisitsFragment.newInstance(dbHelper, pet);
        adapter.addFragment(vetVisitsFragment, getString(R.string.vet_visits));

        vaccinationsFragment = VaccinationsFragment.newInstance(dbHelper, pet);
        adapter.addFragment(vaccinationsFragment, getString(R.string.vaccinations));

        binding.viewPagerNotes.setAdapter(adapter);

        binding.tabLayoutNotes.setupWithViewPager(binding.viewPagerNotes);
    }

    private void showAddNoteDialog(boolean isVaccinationTabSelected) {
        binding.addNewNoteLayout.setVisibility(View.VISIBLE);

        binding.addNewNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteName = binding.noteNameEditText.getText().toString().trim();
                String noteDate = binding.noteDateEditText.getText().toString().trim();

                if (isVaccinationTabSelected) {
                    Vaccination vaccination = new Vaccination(noteName, noteDate);
                    dbHelper.addVaccination(pet.getId(), vaccination);
                    vaccinationsFragment.UpdateVaccinationList();
                } else {
                    VetVisit vetVisit = new VetVisit(noteName, noteDate);
                    dbHelper.addVetVisit(pet.getId(), vetVisit);
                    vetVisitsFragment.UpdateVetVisitsList();
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                NotificationCompat.Builder builder = new NotificationCompat.Builder(PetDetailsActivity.this, "com.example.petnotes.channel")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Pet note!")
                        .setContentText(pet.getName() + " " + noteName + "\t " + noteDate)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                Notification notification = builder.build();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PetDetailsActivity.this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Код для отправки уведомления
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(PetDetailsActivity.this, "com.example.petnotes.channel")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle("Pet note!")
                                .setContentText(pet.getName() + getString(R.string.need_to) + noteName + "\t " + noteDate)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        Notification notification = builder.build();
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PetDetailsActivity.this);

                        notificationManager.notify(1, notification);
                    }
                }, 600000);


                binding.noteDateEditText.setText("");
                binding.noteNameEditText.setText("");
                binding.addNewNoteLayout.setVisibility(View.GONE);
            }
        });
    }

    private Pet getPetFromDatabase(long petId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_NAME_NAME,
                PetContract.PetEntry.COLUMN_NAME_BIRTH_DATE,
                PetContract.PetEntry.COLUMN_NAME_WEIGHT,
                PetContract.PetEntry.COLUMN_NAME_PHOTO
        };

        String selection = PetContract.PetEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(petId) };

        Cursor cursor = db.query(
                PetContract.PetEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        Pet pet = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_NAME));
            String birthDate = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_BIRTH_DATE));
            int weight = cursor.getInt(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_WEIGHT));
            byte[] photoByteArray = cursor.getBlob(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_PHOTO));
            Bitmap photo = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.length);
            pet = new Pet(petId, name, birthDate, weight, photo);
        }
        cursor.close();
        return pet;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pet Notes Channel";
            String description = "Channel for Pet Notes";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("com.example.petnotes.channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
