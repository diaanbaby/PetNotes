package classes;

import android.provider.BaseColumns;

public final class PetContract {
    private PetContract() {}

    public static class PetEntry implements BaseColumns {
        public static final String TABLE_NAME = "pets";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_BIRTH_DATE = "birth_date";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_PHOTO = "photo";
    }

    public static class VaccinationEntry implements BaseColumns {
        public static final String TABLE_NAME = "vaccinations";
        public static final String COLUMN_NAME_PET_ID = "pet_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DATE = "date";
    }

    public static class VetVisitEntry implements BaseColumns {
        public static final String TABLE_NAME = "vet_visits";
        public static final String COLUMN_NAME_PET_ID = "pet_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DATE = "date";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PetEntry.TABLE_NAME + " (" +
                    PetEntry._ID + " INTEGER PRIMARY KEY," +
                    PetEntry.COLUMN_NAME_NAME + " TEXT," +
                    PetEntry.COLUMN_NAME_BIRTH_DATE + " TEXT," +
                    PetEntry.COLUMN_NAME_WEIGHT + " INTEGER," +
                    PetEntry.COLUMN_NAME_PHOTO + " BLOB)";

    public static final String SQL_CREATE_VACCINATION_ENTRIES =
            "CREATE TABLE " + VaccinationEntry.TABLE_NAME + " (" +
                    VaccinationEntry._ID + " INTEGER PRIMARY KEY," +
                    VaccinationEntry.COLUMN_NAME_PET_ID + " INTEGER," +
                    VaccinationEntry.COLUMN_NAME_NAME + " TEXT," +
                    VaccinationEntry.COLUMN_NAME_DATE + " TEXT)";

    public static final String SQL_CREATE_VET_VISIT_ENTRIES =
            "CREATE TABLE " + VetVisitEntry.TABLE_NAME + " (" +
                    VetVisitEntry._ID + " INTEGER PRIMARY KEY," +
                    VetVisitEntry.COLUMN_NAME_PET_ID + " INTEGER," +
                    VetVisitEntry.COLUMN_NAME_NAME + " TEXT," +
                    VetVisitEntry.COLUMN_NAME_DATE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;

    public static final String SQL_DELETE_VACCINATION_ENTRIES =
            "DROP TABLE IF EXISTS " + VaccinationEntry.TABLE_NAME;

    public static final String SQL_DELETE_VET_VISIT_ENTRIES =
            "DROP TABLE IF EXISTS " + VetVisitEntry.TABLE_NAME;
}
