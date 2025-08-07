package com.example.medicinereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ActivityReminder extends AppCompatActivity {

    EditText medicineName;
    TimePicker morningTimePicker, afternoonTimePicker, nightTimePicker;
    RadioGroup morningRadioGroup, afternoonRadioGroup, nightRadioGroup, repeatRadioGroup;
    Button saveReminderButton, addMoreButton;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Initialize Views
        medicineName = findViewById(R.id.medicineName);
        morningTimePicker = findViewById(R.id.morningTimePicker);
        afternoonTimePicker = findViewById(R.id.afternoonTimePicker);
        nightTimePicker = findViewById(R.id.nightTimePicker);

        morningRadioGroup = findViewById(R.id.morningRadioGroup);
        afternoonRadioGroup = findViewById(R.id.afternoonRadioGroup);
        nightRadioGroup = findViewById(R.id.nightRadioGroup);
        repeatRadioGroup = findViewById(R.id.repeatRadioGroup);

        saveReminderButton = findViewById(R.id.saveReminderButton);
        addMoreButton = findViewById(R.id.addMoreButton);

        // Initialize Database Helper
        databaseHelper = new DatabaseHelper(this);

        saveReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = medicineName.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(ActivityReminder.this, "Enter medicine name", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get selected repeat option (Daily, Weekly, Once)
                int repeatOptionId = repeatRadioGroup.getCheckedRadioButtonId();
                String repeatOption = "Once"; // Default to once
                if (repeatOptionId == R.id.radio_daily) {
                    repeatOption = "Daily";
                } else if (repeatOptionId == R.id.radio_weekly) {
                    repeatOption = "Weekly";
                }

                Calendar now = Calendar.getInstance();

                // Morning Reminder
                Calendar morning = (Calendar) now.clone();
                morning.set(Calendar.HOUR_OF_DAY, morningTimePicker.getHour());
                morning.set(Calendar.MINUTE, morningTimePicker.getMinute());
                morning.set(Calendar.SECOND, 0);
                if (morning.after(now)) {
                    setAlarm(morning, name, "Morning", repeatOption);
                    storeReminder(name, morning.getTime().toString(), "Morning", repeatOption);
                }

                // Afternoon Reminder
                Calendar afternoon = (Calendar) now.clone();
                afternoon.set(Calendar.HOUR_OF_DAY, afternoonTimePicker.getHour());
                afternoon.set(Calendar.MINUTE, afternoonTimePicker.getMinute());
                afternoon.set(Calendar.SECOND, 0);
                if (afternoon.after(now)) {
                    setAlarm(afternoon, name, "Afternoon", repeatOption);
                    storeReminder(name, afternoon.getTime().toString(), "Afternoon", repeatOption);
                }

                // Night Reminder
                Calendar night = (Calendar) now.clone();
                night.set(Calendar.HOUR_OF_DAY, nightTimePicker.getHour());
                night.set(Calendar.MINUTE, nightTimePicker.getMinute());
                night.set(Calendar.SECOND, 0);
                if (night.after(now)) {
                    setAlarm(night, name, "Night", repeatOption);
                    storeReminder(name, night.getTime().toString(), "Night", repeatOption);
                }

                Toast.makeText(ActivityReminder.this, "Reminder set!", Toast.LENGTH_SHORT).show();
            }
        });

        addMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicineName.setText("");
                Toast.makeText(ActivityReminder.this, "Add another medicine", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ View All Saved Reminders (Log Output)
        Cursor cursor = databaseHelper.getAllReminders();
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("medicine_name");
            int timeIndex = cursor.getColumnIndex("time");
            int mealIndex = cursor.getColumnIndex("meal_time");
            int repeatIndex = cursor.getColumnIndex("repeat_option");

            do {
                if (nameIndex != -1 && timeIndex != -1 && mealIndex != -1 && repeatIndex != -1) {
                    String medName = cursor.getString(nameIndex);
                    String time = cursor.getString(timeIndex);
                    String mealTime = cursor.getString(mealIndex);
                    String repeatOption = cursor.getString(repeatIndex);
                    Log.d("DB_Reminder", "Medicine: " + medName + ", Time: " + time + ", Meal: " + mealTime + ", Repeat: " + repeatOption);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void setAlarm(Calendar calendar, String medicineName, String mealTime, String repeatOption) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("medicine_name", medicineName);
        intent.putExtra("meal_time", mealTime);
        intent.putExtra("repeat_option", repeatOption); // Pass repeat option to ReminderReceiver

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set repeating alarm based on selected repeat option
        if (repeatOption.equals("Daily")) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        } else if (repeatOption.equals("Weekly")) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void storeReminder(String medicineName, String time, String mealTime, String repeatOption) {
        databaseHelper.insertReminder(medicineName, time, mealTime, repeatOption); // Include repeat option
    }
}
