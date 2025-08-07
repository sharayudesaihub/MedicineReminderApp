package com.example.medicinereminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medicine_reminder.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "reminders";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MEDICINE_NAME = "medicine_name";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_MEAL_TIME = "meal_time";
    private static final String COLUMN_REMINDER_FREQUENCY = "reminder_frequency";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MEDICINE_NAME + " TEXT, " +
            COLUMN_TIME + " TEXT, " +
            COLUMN_MEAL_TIME + " TEXT, " +
            COLUMN_REMINDER_FREQUENCY + " TEXT" + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertReminder(String medicineName, String time, String mealTime, String reminderFrequency) {
        // Validate the reminder frequency
        if (!reminderFrequency.equals("Daily") && !reminderFrequency.equals("Weekly") && !reminderFrequency.equals("Once")) {
            throw new IllegalArgumentException("Invalid reminder frequency. Use 'Daily', 'Weekly', or 'Once'.");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICINE_NAME, medicineName);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_MEAL_TIME, mealTime);
        values.put(COLUMN_REMINDER_FREQUENCY, reminderFrequency);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Cursor getAllReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    // Update Reminder (Optional for frequency)
    public int updateReminder(int id, String medicineName, String time, String mealTime, String reminderFrequency) {
        // Validate the reminder frequency
        if (!reminderFrequency.equals("Daily") && !reminderFrequency.equals("Weekly") && !reminderFrequency.equals("Once")) {
            throw new IllegalArgumentException("Invalid reminder frequency. Use 'Daily', 'Weekly', or 'Once'.");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEDICINE_NAME, medicineName);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_MEAL_TIME, mealTime);
        values.put(COLUMN_REMINDER_FREQUENCY, reminderFrequency);

        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Delete Reminder
    public void deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Delete all Reminders (optional)
    public void deleteAllReminders() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
