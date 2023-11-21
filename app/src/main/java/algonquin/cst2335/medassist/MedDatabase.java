package algonquin.cst2335.medassist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

//TODO
/**
 * Add table for Doctor + information (Phone number)
 */
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

    /**
     * Doctor
     */
    private static final String TABLE_DOCTOR = "doctor";
    private static final String COLUMN_DOC_ID = "doc_id";
    private static final String COLUMN_DOCNAME = "doc_name";
    private static final String COLUMN_DOCNUM = "doc_phone";

    /**
     * Notification
     */
    private static final String TABLE_NOTIFICATION = "notification";
    private static final String COLUMN_NOTIFICATION_ID = "noti_id";
    private static final String COLUMN_NOTIFICATION_NAME = "noti_name";
    private static final String COLUMN_NOTIFICATION_DATE = "noti_date";
    private static final String COLUMN_NOTIFICATION_TIME = "noti_time";
    private static final String COLUMN_NOTIFICATION_REPEAT_DATE = "noti_repeat_date";
    private static final String COLUMN_NOTIFICATION_REPEAT_AMOUNT = "noti_repeat_amount";
    private static final String COLUMN_NOTIFICATION_TIME_BEFORE = "noti_time_before";
    private static final String COLUMN_MEDICINE_ID = "medicine_id";




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

        String createDoctorTableSQL = "CREATE TABLE " + TABLE_DOCTOR + " (" +
                COLUMN_DOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DOCNAME + " TEXT," +
                COLUMN_DOCNUM + " TEXT" +
                ")";
        db.execSQL(createDoctorTableSQL);

        String createNotificationTableSQL = "CREATE TABLE " + TABLE_NOTIFICATION + " (" +
                COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_MEDICINE_ID + " INTEGER," +
                COLUMN_NOTIFICATION_NAME + " TEXT," +
                COLUMN_NOTIFICATION_DATE + " TEXT," +
                COLUMN_NOTIFICATION_TIME + " TEXT," +
                COLUMN_NOTIFICATION_REPEAT_DATE + " TEXT," +
                COLUMN_NOTIFICATION_REPEAT_AMOUNT + " TEXT," +
                COLUMN_NOTIFICATION_TIME_BEFORE + " TEXT," +
                "FOREIGN KEY (" + COLUMN_MEDICINE_ID + ") REFERENCES " + TABLE_MEDICINE + "(" + COLUMN_MEDICINE_ID + ")" +
                ")";
        db.execSQL(createNotificationTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades, if needed
    }

    /**
     * Inserts medicine information into database
     * @param medicine
     * @return ID - ID of medicine information
     */
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

    /**
     * Inserts user login information into database
     * @param username - username of user
     * @param password - password of user
     * @param passcode - numerical passcode to recover account information
     * @return ID - User login information
     */
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

    /**
     * Insert Doctor's information (name and number)
     * @param doctor - The doctors name and phone number
     * @return The ID of the doctors information
     */
    public long insertDoc(Doctor doctor){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DOCNAME, doctor.getDocName());
        values.put(COLUMN_DOCNUM, doctor.getDocNumber());

        long newRowId = db.insert(TABLE_DOCTOR, null, values);
        db.close();

        return newRowId;
    }

    /**
     * Insert Notifications's information (date,time,repeatdate,repeatamount,beforetime)
     * @param notification - all notification details
     * @return The ID of the Notifications information
     */
    public long insertNotification(Notification notification){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICINE_ID, notification.getMedicineId());
        values.put(COLUMN_NOTIFICATION_NAME, notification.getNotiName());
        values.put(COLUMN_NOTIFICATION_DATE, notification.getNotiDate());
        values.put(COLUMN_NOTIFICATION_TIME, notification.getNotiTime());
        values.put(COLUMN_NOTIFICATION_REPEAT_DATE, notification.getNotiRepeatDate());
        values.put(COLUMN_NOTIFICATION_REPEAT_AMOUNT, notification.getNotiRepeatAmount());
        values.put(COLUMN_NOTIFICATION_TIME_BEFORE, notification.getNotiTimeBefore());

        long newRowId = db.insert(TABLE_NOTIFICATION, null, values);
        db.close();

        return newRowId;
    }

    /**
     * Obtains the list of doctors
     * @return The list of doctors
     */
    public List<Doctor> getAllDoctor(){
        List<Doctor> doctorList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_DOC_ID, COLUMN_DOCNAME, COLUMN_DOCNUM
        };

        Cursor cursor = db.query(TABLE_DOCTOR, projection, null, null, null, null, null);

        while(cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(COLUMN_DOC_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_DOCNAME));
            String docNumber = cursor.getString(cursor.getColumnIndex(COLUMN_DOCNUM));

            Doctor doctor = new Doctor(name, docNumber);
            doctor.setDocID(id);
            doctorList.add(doctor);
        }
        cursor.close();
        db.close();

        return doctorList;
    }
    /**
     * Retrieves all medicine information in database
     * @return
     */
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
    /**
     * Obtains the list of notifications
     * @return The list of notifications
     */
    public List<Notification> getAllNotifications(){
        List<Notification> notificationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_NOTIFICATION_ID, COLUMN_MEDICINE_ID, COLUMN_NOTIFICATION_NAME, COLUMN_NOTIFICATION_DATE, COLUMN_NOTIFICATION_TIME,
                COLUMN_NOTIFICATION_REPEAT_DATE, COLUMN_NOTIFICATION_REPEAT_AMOUNT, COLUMN_NOTIFICATION_TIME_BEFORE,
        };

        Cursor cursor = db.query(TABLE_NOTIFICATION, projection, null, null, null, null, null);

        while(cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(COLUMN_NOTIFICATION_ID));
            long medicineName = cursor.getLong(cursor.getColumnIndex(COLUMN_MEDICINE_ID));
            String notiName = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_NAME));
            String notiDate = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_DATE));
            String notiTime = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_TIME));
            String notiRepeatDate = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_REPEAT_DATE));
            String notiRepeatAmount = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_REPEAT_AMOUNT));
            String notiTimeBefore = cursor.getString(cursor.getColumnIndex(COLUMN_NOTIFICATION_TIME_BEFORE));

            Notification notification = new Notification(notiName, notiDate, notiTime, notiRepeatDate, notiRepeatAmount,
                    notiTimeBefore, medicineName);
            notification.setNotiID(id);
            notificationList.add(notification);
        }
        cursor.close();
        db.close();

        return notificationList;
    }

    public long getNotificationByMedicineId(long medicineId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_NOTIFICATION_ID, COLUMN_MEDICINE_ID
        };

        String selection = COLUMN_MEDICINE_ID + " = ?";
        String[] selectionArgs = { String.valueOf(medicineId) };

        Cursor cursor = db.query(TABLE_NOTIFICATION, projection, null, null, null, null, null);

        long notificationId = -1;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_NOTIFICATION_ID);
            if (columnIndex != -1) {
                notificationId = cursor.getLong(columnIndex);
            }
        }

        cursor.close();
        db.close();

        return notificationId;

    }
    // Method to update a MedicineDTO object in the database
    /**
     * Edits the medicine information
     * @param id - The ID of the medicine being updated
     * @param medicine - The name of the medicine being updated
     * @return - The udpated medicine information in database
     */
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

    /**
     * Edits the notification information
     * @param id - The ID of the notification being updated
     * @param notification - The updated notification information
     * @return - The updated notification information in the database
     */
    public int updateNotification(long id, Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_NAME, notification.getNotiName());
        values.put(COLUMN_NOTIFICATION_DATE, notification.getNotiDate());
        values.put(COLUMN_NOTIFICATION_TIME, notification.getNotiTime());
        values.put(COLUMN_NOTIFICATION_REPEAT_DATE, notification.getNotiRepeatDate());
        values.put(COLUMN_NOTIFICATION_REPEAT_AMOUNT, notification.getNotiRepeatAmount());
        values.put(COLUMN_NOTIFICATION_TIME_BEFORE, notification.getNotiTimeBefore());

        String selection = COLUMN_NOTIFICATION_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(TABLE_NOTIFICATION, values, selection, selectionArgs);
        db.close();

        return count;
    }

    /**
     * Deletes the medicine based on ID in database
     */
    public void deleteMedicine(long medicineId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(medicineId)};

        db.delete(TABLE_MEDICINE, selection, selectionArgs);
        db.close();
    }

    /**
     * Deletes the notification based on ID in database
     */
    public int deleteNotification(Long notificationId) {
        if (notificationId == null) {
            // Handle the case where notificationId is null (not found)
            return -1;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = COLUMN_NOTIFICATION_ID + " = ?";
        String[] selectionArgs = { String.valueOf(notificationId) };

        int deletedRows = db.delete(TABLE_NOTIFICATION, selection, selectionArgs);
        db.close();

        return deletedRows;
    }

    /**
     * Method to check if user exists in the database before being granted access
     * @param username - username entered from the user
     * @param password - password entered from the user
     * @return - true if the user info matches a database record. False if either user or pass does
     *           not match a record in the database.
     */
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
