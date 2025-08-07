package com.example.medicinereminder;
import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.util.*;

public class AppointmentDetails extends AppCompatActivity {

    EditText etDoctorName, etHospitalName, etNextAppointment;
    Button btnAddLastVisit, btnSetReminder;
    LinearLayout lastVisitList;
    Calendar appointmentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

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
    }

    private void showDynamicDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = day + "/" + (month + 1) + "/" + year;
            TextView dateView = new TextView(this);
            dateView.setText("• " + date);
            dateView.setPadding(8, 8, 8, 8);
            dateView.setTextSize(16);
            dateView.setOnClickListener(v -> lastVisitList.removeView(dateView)); // optional: remove on click
            lastVisitList.addView(dateView);
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

    private void setAppointmentReminder() {
        String doctor = etDoctorName.getText().toString();
        String hospital = etHospitalName.getText().toString();

        Intent intent = new Intent(this, AppointmentReminderReceiver.class);
        intent.putExtra("doctorName", doctor);
        intent.putExtra("hospitalName", hospital);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    appointmentCalendar.getTimeInMillis(),
                    pendingIntent
            );
            Toast.makeText(this, "Reminder Set for Appointment!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AppointmentChannel";
            String description = "Doctor Appointment Reminder Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("doctorAppointment", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
