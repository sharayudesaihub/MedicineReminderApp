package com.example.medicinereminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AppointmentReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String doctor = intent.getStringExtra("doctorName");
        String hospital = intent.getStringExtra("hospitalName");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "doctorAppointment")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Doctor Appointment Reminder")
                .setContentText("Appointment with Dr. " + doctor + " at " + hospital)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        // ✅ Use 'context' instead of 'this'
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted; don't show notification
            return;
        }

        manager.notify(101, builder.build());
    }
}
