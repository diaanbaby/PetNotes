package classes;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

public class PetDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pets.db";
    private static final int DATABASE_VERSION = 1;

    public PetDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PetContract.SQL_CREATE_ENTRIES);
        db.execSQL(PetContract.SQL_CREATE_VACCINATION_ENTRIES);
        db.execSQL(PetContract.SQL_CREATE_VET_VISIT_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PetContract.SQL_DELETE_ENTRIES);
        db.execSQL(PetContract.SQL_DELETE_VACCINATION_ENTRIES);
        db.execSQL(PetContract.SQL_DELETE_VET_VISIT_ENTRIES);
        onCreate(db);
    }

    private static final String SQL_CREATE_VET_VISIT_TABLE =
            "CREATE TABLE " + PetContract.VetVisitEntry.TABLE_NAME + " (" +
                    PetContract.VetVisitEntry._ID + " INTEGER PRIMARY KEY," +
                    PetContract.VetVisitEntry.COLUMN_NAME_PET_ID + " INTEGER," +
                    PetContract.VetVisitEntry.COLUMN_NAME_DATE + " TEXT," +
                    PetContract.VetVisitEntry.COLUMN_NAME_NAME + " TEXT)";

    private static final String SQL_CREATE_VACCINATION_TABLE =
            "CREATE TABLE " + PetContract.VaccinationEntry.TABLE_NAME + " (" +
                    PetContract.VaccinationEntry._ID + " INTEGER PRIMARY KEY," +
                    PetContract.VaccinationEntry.COLUMN_NAME_PET_ID + " INTEGER," +
                    PetContract.VaccinationEntry.COLUMN_NAME_DATE + " TEXT," +
                    PetContract.VaccinationEntry.COLUMN_NAME_NAME + " TEXT)";

    public Pet getPet(long petId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Определяем столбцы, которые мы хотим извлечь
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
                PetContract.PetEntry.TABLE_NAME,   // Таблица для запроса
                projection,                        // Столбцы, которые хотим получить
                selection,                         // Условие выборки строк
                selectionArgs,                     // Аргументы для условия выборки
                null,                              // Не группировать строки
                null,                              // Не фильтровать группы строк
                null                               // Не сортировать порядок строк
        );

        Pet pet = null;
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(PetContract.PetEntry._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_NAME));
            String birthDate = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_BIRTH_DATE));
            int weight = cursor.getInt(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_NAME_WEIGHT));


            pet = new Pet(id, name, birthDate, weight, null);
        }

        if (cursor != null) {
            cursor.close();
        }

        return pet;
    }

    public long addVaccination(long petId, Vaccination vaccination) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PetContract.VaccinationEntry.COLUMN_NAME_PET_ID, petId);
        values.put(PetContract.VaccinationEntry.COLUMN_NAME_NAME, vaccination.getName());
        values.put(PetContract.VaccinationEntry.COLUMN_NAME_DATE, vaccination.getDateTime());
        return db.insert(PetContract.VaccinationEntry.TABLE_NAME, null, values);
    }

    public long addVetVisit(long petId, VetVisit vetVisit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PetContract.VetVisitEntry.COLUMN_NAME_PET_ID, petId);
        values.put(PetContract.VetVisitEntry.COLUMN_NAME_NAME, vetVisit.getName());
        values.put(PetContract.VetVisitEntry.COLUMN_NAME_DATE, vetVisit.getDateTime());
        return db.insert(PetContract.VetVisitEntry.TABLE_NAME, null, values);
    }

    public void deleteVetVisit(long petId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = PetContract.VetVisitEntry.COLUMN_NAME_PET_ID + " = ? AND " +
                PetContract.VetVisitEntry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {String.valueOf(petId), name};
        int deletedRows = db.delete(PetContract.VetVisitEntry.TABLE_NAME, selection, selectionArgs);
        Log.d(TAG, "Deleted " + deletedRows + " rows");
    }

    public ArrayList<Vaccination> getVaccinations(long petId) {
        ArrayList<Vaccination> vaccinations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                PetContract.VaccinationEntry.COLUMN_NAME_DATE,
                PetContract.VaccinationEntry.COLUMN_NAME_NAME
        };
        String selection = PetContract.VaccinationEntry.COLUMN_NAME_PET_ID + " = ?";
        String[] selectionArgs = { String.valueOf(petId) };
        Cursor cursor = db.query(
                PetContract.VaccinationEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.VaccinationEntry.COLUMN_NAME_NAME));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.VaccinationEntry.COLUMN_NAME_DATE));
            vaccinations.add(new Vaccination(name, date));
        }
        cursor.close();
        return vaccinations;
    }

    public ArrayList<VetVisit> getVetVisits(long petId) {
        ArrayList<VetVisit> vetVisits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                PetContract.VetVisitEntry.COLUMN_NAME_DATE,
                PetContract.VetVisitEntry.COLUMN_NAME_NAME
        };
        String selection = PetContract.VetVisitEntry.COLUMN_NAME_PET_ID + " = ?";
        String[] selectionArgs = { String.valueOf(petId) };
        Cursor cursor = db.query(
                PetContract.VetVisitEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.VetVisitEntry.COLUMN_NAME_NAME));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.VetVisitEntry.COLUMN_NAME_DATE));
            vetVisits.add(new VetVisit(name, date));
        }
        cursor.close();
        return vetVisits;
    }

}
