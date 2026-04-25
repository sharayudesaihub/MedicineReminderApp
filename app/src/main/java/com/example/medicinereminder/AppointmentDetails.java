package com.example.medicinereminder;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.*;
import android.provider.Settings;

public class AppointmentDetails extends AppCompatActivity {

    EditText etDoctorName, etHospitalName, etNextAppointment;
    Button btnAddLastVisit, btnSetReminder;
    LinearLayout lastVisitList;
    Calendar appointmentCalendar;
    private DatabaseHelper databaseHelper;  // ✅ ADDED - Database helper
    private int currentAppointmentId = -1;  // ✅ ADDED - For storing/updating appointments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        // ✅ Initialize Database Helper
        databaseHelper = new DatabaseHelper(this);

        etDoctorName = findViewById(R.id.etDoctorName);
        etHospitalName = findViewById(R.id.etHospitalName);
        etNextAppointment = findViewById(R.id.etNextAppointment);
        btnAddLastVisit = findViewById(R.id.btnAddLastVisit);
        btnSetReminder = findViewById(R.id.btnSetReminder);
        lastVisitList = findViewById(R.id.lastVisitList);
        appointmentCalendar = Calendar.getInstance();

        etNextAppointment.setOnClickListener(v -> showDateTimePicker());
        btnAddLastVisit.setOnClickListener(v -> showDynamicDatePicker());
        btnSetReminder.setOnClickListener(v -> setAppointmentReminder());

        createNotificationChannel();

        // ✅ Load existing appointments if editing (optional)
        loadExistingAppointments();
    }

    private void showDynamicDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = day + "/" + (month + 1) + "/" + year;
            TextView dateView = new TextView(this);
            dateView.setText("• " + date);
            dateView.setPadding(8, 8, 8, 8);
            dateView.setTextSize(16);
            dateView.setOnClickListener(v -> lastVisitList.removeView(dateView));
            lastVisitList.addView(dateView);

            // ✅ Save last visit date to SharedPreferences or database
            saveLastVisitDate(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showDateTimePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            appointmentCalendar.set(Calendar.YEAR, year);
            appointmentCalendar.set(Calendar.MONTH, month);
            appointmentCalendar.set(Calendar.DAY_OF_MONTH, day);

            new TimePickerDialog(this, (timePicker, hour, minute) -> {
                appointmentCalendar.set(Calendar.HOUR_OF_DAY, hour);
                appointmentCalendar.set(Calendar.MINUTE, minute);
                appointmentCalendar.set(Calendar.SECOND, 0);
                etNextAppointment.setText(day + "/" + (month + 1) + "/" + year + " " + hour + ":" + minute);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setAppointmentReminder() {
        String doctor = etDoctorName.getText().toString().trim();
        String hospital = etHospitalName.getText().toString().trim();

        // ✅ Validate input
        if (doctor.isEmpty()) {
            Toast.makeText(this, "Please enter doctor name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etNextAppointment.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select appointment date & time", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Check if appointment time is in future
        if (appointmentCalendar.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(this, "Please select a future date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Save to database first
        long id = databaseHelper.insertAppointment(
                doctor,
                hospital,
                appointmentCalendar.getTimeInMillis(),
                "",  // doctor phone - you can add a field for this
                ""   // notes - you can add a field for this
        );

        if (id == -1) {
            Toast.makeText(this, "Error saving appointment", Toast.LENGTH_SHORT).show();
            return;
        }

        currentAppointmentId = (int) id;

        // ✅ Check exact alarm permission for Android 12+
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Request permission
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(this, "Please allow exact alarm permission for reminders", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // ✅ Create unique request code using appointment ID
        int requestCode = (int) id;

        Intent intent = new Intent(this, AppointmentReminderReceiver.class);
        intent.putExtra("doctorName", doctor);
        intent.putExtra("hospitalName", hospital);
        intent.putExtra("appointmentId", requestCode);
        intent.putExtra("appointmentTime", appointmentCalendar.getTimeInMillis());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            // ✅ Use setExact for precise timing
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    appointmentCalendar.getTimeInMillis(),
                    pendingIntent
            );
            Toast.makeText(this, "Appointment Saved & Reminder Set!", Toast.LENGTH_SHORT).show();

            // ✅ Clear form or finish activity
            clearForm();
        }
    }

    // ✅ Save last visit date (you can extend this to save to database)
    private void saveLastVisitDate(String date) {
        SharedPreferences prefs = getSharedPreferences("AppointmentPrefs", MODE_PRIVATE);
        String existingVisits = prefs.getString("lastVisits", "");
        String newVisits = existingVisits + date + "\n";
        prefs.edit().putString("lastVisits", newVisits).apply();
    }

    // ✅ Load existing appointments to display (optional)
    private void loadExistingAppointments() {
        Cursor cursor = databaseHelper.getUpcomingAppointments();
        if (cursor != null && cursor.getCount() > 0) {
            // You can display existing appointments in a list if you want
            // For now, just log them
            int nameIndex = cursor.getColumnIndex("doctor_name");
            int timeIndex = cursor.getColumnIndex("appointment_time");

            if (nameIndex != -1 && timeIndex != -1) {
                StringBuilder existing = new StringBuilder("Existing Appointments:\n");
                while (cursor.moveToNext()) {
                    String docName = cursor.getString(nameIndex);
                    long time = cursor.getLong(timeIndex);
                    Date date = new Date(time);
                    existing.append("• Dr. ").append(docName).append(" at ").append(date).append("\n");
                }
                // You could show this in a TextView or Toast
                // Toast.makeText(this, existing.toString(), Toast.LENGTH_LONG).show();
            }
            cursor.close();
        }
    }

    // ✅ Clear form after saving
    private void clearForm() {
        etDoctorName.setText("");
        etHospitalName.setText("");
        etNextAppointment.setText("");
        lastVisitList.removeAllViews();
        appointmentCalendar = Calendar.getInstance();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AppointmentChannel";
            String description = "Doctor Appointment Reminder Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("doctorAppointment", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ✅ Close database to prevent memory leaks
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}