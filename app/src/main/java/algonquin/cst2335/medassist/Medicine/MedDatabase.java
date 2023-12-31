package algonquin.cst2335.medassist.Medicine;

import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Range;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import algonquin.cst2335.medassist.Main.NotificationMed;

@SuppressWarnings("Range")
public class MedDatabase extends SQLiteOpenHelper {
    private String currentUser;
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
     *  Constructs a new instance of the MedDatabase class.
     * @param context - The context used to create or open the database.
     *                  Typically, this is the application context.
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

    /**
     * Creates and initiates the database for Medicine, Doctor, and Users
     * @param db The database.
     */
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
     * Inserts Doctor with respect to medicine ID
     * @param medicine - The medicine information
     * @param doctor - The doctors information
     * @return ID - ID of medicine information
     */
    public long insertMedicine(Medicine medicine, Doctor doctor) {
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

        if (newRowId != -1 && doctor != null) {
            ContentValues doctorValues = new ContentValues();
            doctorValues.put(COLUMN_DOC_ID, newRowId); // Use medicine ID as the foreign key
            doctorValues.put(COLUMN_DOCNAME, doctor.getDocName());
            doctorValues.put(COLUMN_DOCNUM, doctor.getDocNumber());
            db.insert(TABLE_DOCTOR, null, doctorValues);
        }

        db.close();

        return newRowId;
    }

    /**
     * Retrieves all medicine information in database
     * @return - List of medicine
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
     * Retrieve frequency based on medicine ID
     * @param medicineId - The ID of the medicine
     * @return - The frequency of the medicine with the given ID, or null if not found
     */
    public String getFrequencyById(long medicineId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_FREQUENCY
        };

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(medicineId)};

        Cursor cursor = db.query(TABLE_MEDICINE, projection, selection, selectionArgs, null, null, null);

        String frequency = null;

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_FREQUENCY);
            if (columnIndex != -1) {
                frequency = cursor.getString(columnIndex);
            }
        }

        cursor.close();
        db.close();

        return frequency;
    }

    /**
     * Obtains the list of notifications
     * @return The list of notifications
     */
    public List<NotificationMed> getAllNotifications(){
        List<NotificationMed> notificationList = new ArrayList<>();
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

            NotificationMed notification = new NotificationMed(notiName, notiDate, notiTime, notiRepeatDate, notiRepeatAmount,
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
    public int updateMedicine(long id, Medicine medicine, Doctor doctor) {
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

        if (doctor != null) {
            ContentValues doctorValues = new ContentValues();
            doctorValues.put(COLUMN_DOCNAME, doctor.getDocName());
            doctorValues.put(COLUMN_DOCNUM, doctor.getDocNumber());

            String doctorSelection = COLUMN_DOC_ID + " = ?";
            String[] doctorSelectionArgs = {String.valueOf(id)};

            int doctorUpdateCount = db.update(TABLE_DOCTOR, doctorValues, doctorSelection, doctorSelectionArgs);

            // If no rows were updated for the doctor, insert a new record
            if (doctorUpdateCount == 0) {
                doctorValues.put(COLUMN_DOC_ID, id); // Use medicine ID as the foreign key
                db.insert(TABLE_DOCTOR, null, doctorValues);
            }
        }


        db.close();

        return count;
    }

    /**
     * Retrieves a list of medicine that are currently taken. Expiration or Duration date is not
     * past Current Date
     * @return List of current medicine
     */
    public List<Medicine> getCurrentMedicines(){
        List<Medicine> currentMedicineList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        LocalDate currentDate = LocalDate.now();

        String[] projection = {
                COLUMN_ID, COLUMN_NAME, COLUMN_DOSAGE, COLUMN_QUANTITY, COLUMN_FREQUENCY,
                COLUMN_REFILL, COLUMN_DURATION, COLUMN_EXPIRATION, COLUMN_INSTRUCTIONS
        };

        Cursor cursor = db.query(
                TABLE_MEDICINE, projection, null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            String expirationDateString = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRATION));
            String durationDateString = cursor.getString(cursor.getColumnIndex(COLUMN_DURATION));

            LocalDate expirationDate = LocalDate.parse(expirationDateString, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            LocalDate durationDate = LocalDate.parse(durationDateString, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            if (currentDate.isBefore(expirationDate) || currentDate.isBefore(durationDate)) {
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

                currentMedicineList.add(medicine);
            }
        }

        cursor.close();
        db.close();

        return currentMedicineList;
    }

    /**
     * Retrieves all medicines that are expired or in the past
     * @return List of past medicines
     */
    public List<Medicine> getPastMedicines() {
        List<Medicine> pastMedicineList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        LocalDate currentDate = LocalDate.now();

        String[] projection = {
                COLUMN_ID, COLUMN_NAME, COLUMN_DOSAGE, COLUMN_QUANTITY, COLUMN_FREQUENCY,
                COLUMN_REFILL, COLUMN_DURATION, COLUMN_EXPIRATION, COLUMN_INSTRUCTIONS
        };

        Cursor cursor = db.query(
                TABLE_MEDICINE, projection, null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            String expirationDateString = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRATION));
            String durationDateString = cursor.getString(cursor.getColumnIndex(COLUMN_DURATION));

            LocalDate expirationDate = LocalDate.parse(expirationDateString, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            LocalDate durationDate = LocalDate.parse(durationDateString, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            if (currentDate.isAfter(expirationDate) || currentDate.isAfter(durationDate)) {
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

                pastMedicineList.add(medicine);
            }
        }

        cursor.close();
        db.close();

        return pastMedicineList;
    }

    /**
     * Deletes the medicine along with the doctor's information that prescribed it.
     * @param medicine - The medicine information
     * @param doctor - The doctors information
     */
    public void deleteMedicine(Medicine medicine, Doctor doctor) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(medicine.getId())};

        db.delete(TABLE_MEDICINE, selection, selectionArgs);

        deleteDoctor(doctor);
        db.close();
    }

    /**
     * Deletes the value associated with doctor
     * @param doctor The information of Doctor to be deleted
     */
    private void deleteDoctor(Doctor doctor) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = COLUMN_DOC_ID + " = ?";
        String[] selectionArgs = {String.valueOf(doctor.getDocID())};

        db.delete(TABLE_DOCTOR, selection, selectionArgs);

        db.close();
    }

    /**
     * Retrieves the doctor that is linked with the specific medicine.
     * @param medId - The id of the medicine that is linked with the doctor.
     * @return - The doctors information that is linked with the medicine.
     */
    public Doctor getDoctorForMedicine(long medId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_DOC_ID, COLUMN_DOCNAME, COLUMN_DOCNUM
        };

        String selection = COLUMN_DOC_ID + " = ?";
        String[] selectionArgs = {String.valueOf(medId)};

        Cursor cursor = db.query(TABLE_DOCTOR, projection, selection, selectionArgs, null, null, null);

        Doctor doctor = null;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndex(COLUMN_DOC_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_DOCNAME));
            String docNumber = cursor.getString(cursor.getColumnIndex(COLUMN_DOCNUM));

            doctor = new Doctor(name, docNumber);
            doctor.setDocID(id);
        }

        cursor.close();
        db.close();

        return doctor;
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

    /**
     * Retrieves username and password based on the provided passcode.
     * @param passcode - The passcode entered by user
     * @return - Array containing username and password if a match is found, or null if no match is found.
     */
    public String[] getUsernamePasswordByPasscode(String passcode) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {COLUMN_USERNAME, COLUMN_PASSWORD};
        String selection = COLUMN_PASSCODE + " = ?";
        String[] selectionArgs = {passcode};

        Cursor cursor = db.query(TABLE_USERS, projection, selection, selectionArgs, null, null, null);

        String[] result = null;
        if (cursor.moveToFirst()) {
            result = new String[]{
                    cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
            };
        }

        cursor.close();
        db.close();

        return result;
    }

    /**
     * Deletes the user with the specified username from the database.
     * @param username - The username of the user to be deleted.
     * @return - The number of rows affected. Returns 0 if no user with the given username is found.
     */
    public int deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Define the WHERE clause to find the user with the given username
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        // Delete the user based on the WHERE clause
        int rowsDeleted = db.delete(TABLE_USERS, selection, selectionArgs);

        // Close the database
        db.close();

        return rowsDeleted;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getCurrentUser(){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the columns to be retrieved
        String[] projection = {COLUMN_USERNAME};

        // Query to retrieve the current user
        Cursor cursor = db.query(TABLE_USERS, projection, null, null, null, null, null);

        String currentUser = null;

        // Check if the cursor has data and move to the first row
        if (cursor.moveToFirst()) {
            // Retrieve the username from the cursor
            currentUser = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
        }

        // Close the cursor and database
        cursor.close();
        db.close();

        return currentUser;
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
     * Edits the notification information
     * @param id - The ID of the notification being updated
     * @param notification - The updated notification information
     * @return - The updated notification information in the database
     */
    public int updateNotification(long id, NotificationMed notification) {
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
     * Insert Notifications's information (date,time,repeatdate,repeatamount,beforetime)
     * @param notification - all notification details
     * @return The ID of the Notifications information
     */
    public long insertNotification(NotificationMed notification){
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

}
