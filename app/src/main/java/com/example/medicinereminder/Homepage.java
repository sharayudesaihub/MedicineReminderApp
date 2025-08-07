package com.example.medicinereminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Homepage extends AppCompatActivity {

    Button setReminderBtn, appointmentDetailsBtn, diseaseInfoBtn, casepaperBtn, refillReminderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        // Initialize buttons
        setReminderBtn = findViewById(R.id.setReminderBtn);
        appointmentDetailsBtn = findViewById(R.id.appointmentDetailsBtn);
        diseaseInfoBtn = findViewById(R.id.diseaseInfoBtn);
        casepaperBtn = findViewById(R.id.casepaperBtn);
        refillReminderBtn = findViewById(R.id.refillReminderBtn); // NEW button

        // Navigate to reminder activity
        setReminderBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, ActivityReminder.class);
            startActivity(intent);
        });

        // Navigate to appointment details activity
        appointmentDetailsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, AppointmentDetails.class);
            startActivity(intent);
        });

        // Navigate to disease info activity
        diseaseInfoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, DiseaseInfoActivity.class);
            startActivity(intent);
        });

        // Navigate to casepapers upload activity
        casepaperBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, CasepapersActivity.class);
            startActivity(intent);
        });

        // Navigate to refill reminder activity
        refillReminderBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Homepage.this, ActivityRefillReminder.class);
            startActivity(intent);
        });
    }

    // Inflate the overflow menu (3-dot menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    // Handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profileMenu) {
            Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Homepage.this, Profile.class));
            return true;
        } else if (id == R.id.settingsMenu) {
            Toast.makeText(this, "Opening Settings...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Homepage.this, Setting.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
