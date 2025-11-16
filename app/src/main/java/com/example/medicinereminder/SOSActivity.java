package com.example.medicinereminder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class SOSActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private String emergencyNumber = "9876543210"; // change to your emergency contact
    private String message = "I need help! My current location: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosactivity);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button btnSendSOS = findViewById(R.id.btnSendSOS);
        Button btnCallSOS = findViewById(R.id.btnCallSOS);

        btnSendSOS.setOnClickListener(v -> sendSOSMessage());
        btnCallSOS.setOnClickListener(v -> makeEmergencyCall());
    }

    private void sendSOSMessage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String locationUrl = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(emergencyNumber, null, message + locationUrl, null, null);
                        Toast.makeText(SOSActivity.this, "SOS message sent!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SOSActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void makeEmergencyCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSIONS);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + emergencyNumber));
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSOSMessage();
            } else {
                Toast.makeText(this, "Permissions denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
