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

    private static final String TABLE_MEDICINE = "medicine";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DOSAGE = "dosage";
    private static final String COLUMN_FREQUENCY = "frequency";
    private static final String COLUMN_EXPIRATION = "expiration";
    private static final String COLUMN_REFILL_DATE = "refill_date";

    public MedDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSQL = "CREATE TABLE " + TABLE_MEDICINE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_DOSAGE + " TEXT," +
                COLUMN_FREQUENCY + " TEXT," +
                COLUMN_EXPIRATION + " TEXT," +
                COLUMN_REFILL_DATE + " TEXT" +
                ")";
        db.execSQL(createTableSQL);
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
        values.put(COLUMN_FREQUENCY, medicine.getFrequency());
        values.put(COLUMN_EXPIRATION, medicine.getExpiration());
        values.put(COLUMN_REFILL_DATE, medicine.getRefillDate());

        long newRowId = db.insert(TABLE_MEDICINE, null, values);
        db.close();

        return newRowId;
    }

    // Method to retrieve all Medicine objects from the database
    public List<Medicine> getAllMedicines() {
        List<Medicine> medicineList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID, COLUMN_NAME, COLUMN_DOSAGE, COLUMN_FREQUENCY, COLUMN_EXPIRATION, COLUMN_REFILL_DATE
        };

        Cursor cursor = db.query(
                TABLE_MEDICINE, projection, null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
            String dosage = cursor.getString(cursor.getColumnIndex(COLUMN_DOSAGE));
            String frequency = cursor.getString(cursor.getColumnIndex(COLUMN_FREQUENCY));
            String expiration = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRATION));
            String refillDate = cursor.getString(cursor.getColumnIndex(COLUMN_REFILL_DATE));

            Medicine medicine = new Medicine(name, dosage, frequency, expiration, refillDate);
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
        values.put(COLUMN_FREQUENCY, medicine.getFrequency());
        values.put(COLUMN_EXPIRATION, medicine.getExpiration());
        values.put(COLUMN_REFILL_DATE, medicine.getRefillDate());

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(TABLE_MEDICINE, values, selection, selectionArgs);
        db.close();

        return count;
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

}
