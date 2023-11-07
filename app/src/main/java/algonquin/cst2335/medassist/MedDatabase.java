package algonquin.cst2335.medassist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MedDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "medicine_db";
    private static final int DATABASE_VERSION = 1;

    /**
     * User
     */
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PASSCODE = "passcode";

    /**
     * Medicine
     */
    private static final String TABLE_MEDICINE = "medicine";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DOSAGE = "dosage";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_FREQUENCY = "frequency";
    private static final String COLUMN_REFILL = "refill";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_EXPIRATION = "expiration";
    private static final String COLUMN_INSTRUCTIONS = "instructions";


    public MedDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSQL = "CREATE TABLE " + TABLE_MEDICINE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_DOSAGE + " TEXT," +
                COLUMN_QUANTITY + " TEXT," +
                COLUMN_FREQUENCY + " TEXT," +
                COLUMN_REFILL + " TEXT," +
                COLUMN_DURATION + " TEXT," +
                COLUMN_EXPIRATION + " TEXT," +
                COLUMN_INSTRUCTIONS + " TEXT" +

                ")";
        db.execSQL(createTableSQL);

        String createUserTableSQL = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USERNAME + " TEXT," +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_PASSCODE + " TEXT" +
                ")";
        db.execSQL(createUserTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades, if needed
    }

    // Method to insert a MedicineDTO object into the database
    public long insertMedicine(Medicine medicine) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, medicine.getName());
        values.put(COLUMN_DOSAGE, medicine.getDosage());
        values.put(COLUMN_QUANTITY, medicine.getQuantity());
        values.put(COLUMN_FREQUENCY, medicine.getFrequency());
        values.put(COLUMN_REFILL, medicine.getRefills());
        values.put(COLUMN_DURATION, medicine.getDuration());
        values.put(COLUMN_EXPIRATION, medicine.getExpiration());
        values.put(COLUMN_INSTRUCTIONS, medicine.getInstructions());

        long newRowId = db.insert(TABLE_MEDICINE, null, values);
        db.close();

        return newRowId;
    }

    public long insertUser(String username, String password, String passcode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PASSCODE, passcode);

        long newRowId = db.insert(TABLE_USERS, null, values);
        db.close();

        return newRowId;
    }



    // Method to retrieve all Medicine objects from the database
    public List<Medicine> getAllMedicines() {
        List<Medicine> medicineList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID, COLUMN_NAME, COLUMN_DOSAGE, COLUMN_QUANTITY, COLUMN_FREQUENCY, COLUMN_REFILL, COLUMN_DURATION, COLUMN_EXPIRATION, COLUMN_INSTRUCTIONS
        };

        Cursor cursor = db.query(
                TABLE_MEDICINE, projection, null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String dosage = cursor.getString(cursor.getColumnIndex(COLUMN_DOSAGE));
            String quantity = cursor.getString(cursor.getColumnIndex(COLUMN_QUANTITY));
            String frequency = cursor.getString(cursor.getColumnIndex(COLUMN_FREQUENCY));
            String refill = cursor.getString(cursor.getColumnIndex(COLUMN_REFILL));
            String duration = cursor.getString(cursor.getColumnIndex(COLUMN_DURATION));
            String expiration = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRATION));
            String instructions = cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTIONS));


            Medicine medicine = new Medicine(name, dosage, quantity, frequency, refill, duration, expiration, instructions);
            medicine.setId(id);

            medicineList.add(medicine);
        }

        cursor.close();
        db.close();

        return medicineList;
    }

    // Method to update a MedicineDTO object in the database
    public int updateMedicine(long id, Medicine medicine) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, medicine.getName());
        values.put(COLUMN_DOSAGE, medicine.getDosage());
        values.put(COLUMN_QUANTITY, medicine.getQuantity());
        values.put(COLUMN_FREQUENCY, medicine.getFrequency());
        values.put(COLUMN_REFILL, medicine.getRefills());
        values.put(COLUMN_DURATION, medicine.getDuration());
        values.put(COLUMN_EXPIRATION, medicine.getExpiration());
        values.put(COLUMN_INSTRUCTIONS, medicine.getInstructions());

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(TABLE_MEDICINE, values, selection, selectionArgs);
        db.close();

        return count;
    }

    public List<Medicine> getPastMedicines(int pastDurationInDays) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MEDICINE + " WHERE strftime('%s', " + COLUMN_EXPIRATION + ") < strftime('%s', 'now') - ? * 24 * 60 * 60";
        String[] selectionArgs = { String.valueOf(pastDurationInDays) };

        Cursor cursor = db.rawQuery(query, selectionArgs);

        List<Medicine> pastMedicines = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Medicine medicine = new Medicine();
                medicine.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                medicine.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                medicine.setDosage(cursor.getString(cursor.getColumnIndex(COLUMN_DOSAGE)));
                medicine.setFrequency(cursor.getString(cursor.getColumnIndex(COLUMN_FREQUENCY)));
                medicine.setRefills(cursor.getString(cursor.getColumnIndex(COLUMN_REFILL)));
                medicine.setExpiration(cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRATION)));

                pastMedicines.add(medicine);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return pastMedicines;
    }


    public void deleteMostRecentMedicine() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_MEDICINE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            long maxTimestamp = cursor.getLong(0); // Get the most recent timestamp
            cursor.close();

            // Define a condition to identify the most recent entry
            String selection = COLUMN_ID + " = ?";
            String[] selectionArgs = { String.valueOf(maxTimestamp) };

            // Delete the most recent entry
            db.delete(TABLE_MEDICINE, selection, selectionArgs);
        }
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_PASSWORD};

        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USERS, projection, selection, selectionArgs, null, null, null);

        boolean userExists = cursor.moveToFirst(); // Check if a matching record is found

        cursor.close();
        db.close();

        return userExists;
    }
}
