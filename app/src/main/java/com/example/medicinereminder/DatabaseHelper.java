package com.example.medicinereminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "healthcare_app.db";
    private static final int DATABASE_VERSION = 2; // Increased version

    // Table names
    private static final String TABLE_REMINDERS = "reminders";
    private static final String TABLE_PROFILE = "patient_profile";
    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String TABLE_CASE_PAPERS = "case_papers";

    // Common columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_CREATED_AT = "created_at";

    // Reminders table columns
    private static final String COLUMN_MEDICINE_NAME = "medicine_name";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_MEAL_TIME = "meal_time";
    private static final String COLUMN_REMINDER_FREQUENCY = "reminder_frequency";
    private static final String COLUMN_IS_ACTIVE = "is_active";

    // Profile table columns
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_EMERGENCY_PHONE = "emergency_phone";
    private static final String COLUMN_BLOOD_GROUP = "blood_group";
    private static final String COLUMN_ALLERGIES = "allergies";

    // Appointments table columns
    private static final String COLUMN_DOCTOR_NAME = "doctor_name";
    private static final String COLUMN_HOSPITAL_NAME = "hospital_name";
    private static final String COLUMN_APPOINTMENT_TIME = "appointment_time";
    private static final String COLUMN_DOCTOR_PHONE = "doctor_phone";
    private static final String COLUMN_NOTES = "notes";

    // Case papers table columns
    private static final String COLUMN_FILE_NAME = "file_name";
    private static final String COLUMN_FILE_URI = "file_uri";
    private static final String COLUMN_EXTRACTED_TEXT = "extracted_text";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Reminders Table
        String createRemindersTable = "CREATE TABLE " + TABLE_REMINDERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MEDICINE_NAME + " TEXT NOT NULL, " +
                COLUMN_TIME + " TEXT NOT NULL, " +
                COLUMN_MEAL_TIME + " TEXT, " +
                COLUMN_REMINDER_FREQUENCY + " TEXT DEFAULT 'Once', " +
                COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1, " +
                COLUMN_CREATED_AT + " INTEGER DEFAULT 0, " +
                COLUMN_USER_ID + " TEXT)";
        db.execSQL(createRemindersTable);

        // Create Profile Table
        String createProfileTable = "CREATE TABLE " + TABLE_PROFILE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FULL_NAME + " TEXT, " +
                COLUMN_DOB + " TEXT, " +
                COLUMN_AGE + " INTEGER, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_EMERGENCY_PHONE + " TEXT, " +
                COLUMN_BLOOD_GROUP + " TEXT, " +
                COLUMN_ALLERGIES + " TEXT, " +
                COLUMN_CREATED_AT + " INTEGER DEFAULT 0, " +
                COLUMN_USER_ID + " TEXT)";
        db.execSQL(createProfileTable);

        // Create Appointments Table
        String createAppointmentsTable = "CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DOCTOR_NAME + " TEXT NOT NULL, " +
                COLUMN_HOSPITAL_NAME + " TEXT, " +
                COLUMN_APPOINTMENT_TIME + " INTEGER, " +
                COLUMN_DOCTOR_PHONE + " TEXT, " +
                COLUMN_NOTES + " TEXT, " +
                COLUMN_CREATED_AT + " INTEGER DEFAULT 0, " +
                COLUMN_USER_ID + " TEXT)";
        db.execSQL(createAppointmentsTable);

        // Create Case Papers Table
        String createCasePapersTable = "CREATE TABLE " + TABLE_CASE_PAPERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FILE_NAME + " TEXT, " +
                COLUMN_FILE_URI + " TEXT, " +
                COLUMN_EXTRACTED_TEXT + " TEXT, " +
                COLUMN_CREATED_AT + " INTEGER DEFAULT 0, " +
                COLUMN_USER_ID + " TEXT)";
        db.execSQL(createCasePapersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CASE_PAPERS);
        onCreate(db);
    }

    // ==================== REMINDER METHODS ====================

    public long insertReminder(String medicineName, String time, String mealTime, String reminderFrequency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICINE_NAME, medicineName);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_MEAL_TIME, mealTime);
        values.put(COLUMN_REMINDER_FREQUENCY, reminderFrequency);
        values.put(COLUMN_IS_ACTIVE, 1);
        values.put(COLUMN_CREATED_AT, System.currentTimeMillis());

        long id = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        return id;
    }

    public Cursor getAllReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_REMINDERS, null, COLUMN_IS_ACTIVE + " = 1", null, null, null, COLUMN_CREATED_AT + " DESC");
    }

    public int updateReminder(int id, String medicineName, String time, String mealTime, String reminderFrequency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICINE_NAME, medicineName);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_MEAL_TIME, mealTime);
        values.put(COLUMN_REMINDER_FREQUENCY, reminderFrequency);

        return db.update(TABLE_REMINDERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deactivateReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_ACTIVE, 0);
        db.update(TABLE_REMINDERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ==================== PROFILE METHODS ====================

    public long insertOrUpdateProfile(String fullName, String dob, int age, String phone, String email, String emergencyPhone) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if profile exists
        Cursor cursor = db.query(TABLE_PROFILE, null, null, null, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_EMERGENCY_PHONE, emergencyPhone);

        long id;
        if (exists) {
            id = db.update(TABLE_PROFILE, values, null, null);
        } else {
            values.put(COLUMN_CREATED_AT, System.currentTimeMillis());
            id = db.insert(TABLE_PROFILE, null, values);
        }

        db.close();
        return id;
    }

    public Cursor getProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PROFILE, null, null, null, null, null, null);
    }

    // ==================== APPOINTMENT METHODS ====================

    public long insertAppointment(String doctorName, String hospitalName, long appointmentTime, String doctorPhone, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOCTOR_NAME, doctorName);
        values.put(COLUMN_HOSPITAL_NAME, hospitalName);
        values.put(COLUMN_APPOINTMENT_TIME, appointmentTime);
        values.put(COLUMN_DOCTOR_PHONE, doctorPhone);
        values.put(COLUMN_NOTES, notes);
        values.put(COLUMN_CREATED_AT, System.currentTimeMillis());

        long id = db.insert(TABLE_APPOINTMENTS, null, values);
        db.close();
        return id;
    }

    public Cursor getUpcomingAppointments() {
        SQLiteDatabase db = this.getReadableDatabase();
        long currentTime = System.currentTimeMillis();
        return db.query(TABLE_APPOINTMENTS, null,
                COLUMN_APPOINTMENT_TIME + " >= ?",
                new String[]{String.valueOf(currentTime)},
                null, null, COLUMN_APPOINTMENT_TIME + " ASC");
    }

    public Cursor getAllAppointments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_APPOINTMENTS, null, null, null, null, null, COLUMN_APPOINTMENT_TIME + " DESC");
    }

    public void deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APPOINTMENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ==================== CASE PAPER METHODS ====================

    public long insertCasePaper(String fileName, String fileUri, String extractedText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_NAME, fileName);
        values.put(COLUMN_FILE_URI, fileUri);
        values.put(COLUMN_EXTRACTED_TEXT, extractedText);
        values.put(COLUMN_CREATED_AT, System.currentTimeMillis());

        long id = db.insert(TABLE_CASE_PAPERS, null, values);
        db.close();
        return id;
    }

    public Cursor getAllCasePapers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CASE_PAPERS, null, null, null, null, null, COLUMN_CREATED_AT + " DESC");
    }

    public Cursor getLatestCasePaper() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CASE_PAPERS, null, null, null, null, null, COLUMN_CREATED_AT + " DESC", "1");
    }

    public void deleteCasePaper(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CASE_PAPERS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ==================== UTILITY METHODS ====================

    public void deleteAllReminders() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, null, null);
        db.close();
    }

    public int getReminderCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REMINDERS + " WHERE " + COLUMN_IS_ACTIVE + " = 1", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}