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

    Button setReminderBtn, appointmentDetailsBtn, diseaseInfoBtn, casepaperBtn, refillReminderBtn, sosButton;

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
        refillReminderBtn = findViewById(R.id.refillReminderBtn);
        sosButton = findViewById(R.id.sosButton); // ✅ only once

        // Navigate to reminder activity
        setReminderBtn.setOnClickListener(v -> startActivity(new Intent(this, ActivityReminder.class)));

        // Navigate to appointment details activity
        appointmentDetailsBtn.setOnClickListener(v -> startActivity(new Intent(this, AppointmentDetails.class)));

        // Navigate to disease info activity
        diseaseInfoBtn.setOnClickListener(v -> startActivity(new Intent(this, DiseaseInfoActivity.class)));

        // Navigate to casepapers upload activity
        casepaperBtn.setOnClickListener(v -> startActivity(new Intent(this, CasepapersActivity.class)));

        // Navigate to refill reminder activity
        refillReminderBtn.setOnClickListener(v -> startActivity(new Intent(this, ActivityRefillReminder.class)));

        // Navigate to chatbot activity
        Button btnChatbot = findViewById(R.id.btnChatbot);
        btnChatbot.setOnClickListener(v -> startActivity(new Intent(this, ChatbotActivity.class)));

        // ✅ Navigate to SOS activity
        sosButton.setOnClickListener(v -> startActivity(new Intent(this, SOSActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profileMenu) {
            Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Profile.class));
            return true;
        } else if (id == R.id.settingsMenu) {
            Toast.makeText(this, "Opening Settings...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Setting.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
