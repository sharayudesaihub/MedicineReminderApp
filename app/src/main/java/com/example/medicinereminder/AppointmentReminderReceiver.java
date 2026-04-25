package com.example.medicinereminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AppointmentReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "doctorAppointment";
    private static final String CHANNEL_NAME = "Doctor Appointments";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get data from intent
        String doctor = intent.getStringExtra("doctorName");
        String hospital = intent.getStringExtra("hospitalName");
        int appointmentId = intent.getIntExtra("appointmentId", 0);
        long appointmentTime = intent.getLongExtra("appointmentTime", 0);

        // Fallback values if not provided
        if (doctor == null) doctor = "Your Doctor";
        if (hospital == null) hospital = "Hospital";

        // Create notification channel for Android O and above
        createNotificationChannel(context);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🩺 Doctor Appointment Reminder")
                .setContentText("Appointment with Dr. " + doctor + " at " + hospital)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Don't forget your appointment with Dr. " + doctor + " at " + hospital))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 250, 500})
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted - can't show notification
                // You might want to log this
                return;
            }
        }

        // ✅ Use unique notification ID based on appointment ID
        // If no appointment ID, use current time
        int notificationId = appointmentId != 0 ? appointmentId : (int) System.currentTimeMillis();

        // Show the notification
        manager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(Context context) {
        // Create notification channel for Android O and above (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders for doctor appointments");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 250, 500});
            channel.enableLights(true);
            channel.setLightColor(android.graphics.Color.RED);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}