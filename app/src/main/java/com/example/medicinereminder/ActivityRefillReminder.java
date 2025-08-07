package com.example.medicinereminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class  ActivityRefillReminder extends AppCompatActivity {

    EditText medicineName, dosageLeft;
    Button pickDateBtn, setRefillReminderBtn;
    TextView selectedDateText;
    Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refill_reminder);

        medicineName = findViewById(R.id.refillMedicineName);
        dosageLeft = findViewById(R.id.dosageLeft);
        pickDateBtn = findViewById(R.id.pickDateBtn);
        setRefillReminderBtn = findViewById(R.id.setRefillReminderBtn);
        selectedDateText = findViewById(R.id.selectedDateText);

        selectedDate = Calendar.getInstance();

        pickDateBtn.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth, 9, 0); // 9 AM
                        selectedDateText.setText("Selected Date: " + dayOfMonth + "/" + (month + 1) + "/" + year);
                    },
                    today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        setRefillReminderBtn.setOnClickListener(v -> {
            String name = medicineName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter medicine name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedDate.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
                // Check if exact alarms can be scheduled
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        // Request permission from the user
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(intent);
                        return;
                    }
                }

                Intent intent = new Intent(this, ReminderReceiver.class);
                intent.putExtra("medicine_name", name);
                intent.putExtra("meal_time", "Refill Reminder");

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this, (int) System.currentTimeMillis(), intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, selectedDate.getTimeInMillis(), pendingIntent);

                Toast.makeText(this, "Refill reminder set!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Select a valid future date", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
