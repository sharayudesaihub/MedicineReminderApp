package com.example.medicinereminder;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class Profile extends AppCompatActivity {

    TextInputEditText etFullName, etDob, etAge, etPhone, etEmail, etEmergencyPhone;
    MaterialButton btnSaveProfile;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Database Helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize Views
        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etAge = findViewById(R.id.etAge);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etEmergencyPhone = findViewById(R.id.etEmergencyPhone);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Load existing profile if any
        loadProfile();

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = etFullName.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String emergencyPhone = etEmergencyPhone.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(Profile.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.isEmpty()) {
            Toast.makeText(Profile.this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr);

        long result = databaseHelper.insertOrUpdateProfile(name, dob, age, phone, email, emergencyPhone);

        if (result != -1) {
            Toast.makeText(Profile.this, "Profile Saved Successfully ✅", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Profile.this, "Error saving profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfile() {
        Cursor cursor = databaseHelper.getProfile();
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("full_name");
            int dobIndex = cursor.getColumnIndex("dob");
            int ageIndex = cursor.getColumnIndex("age");
            int phoneIndex = cursor.getColumnIndex("phone");
            int emailIndex = cursor.getColumnIndex("email");
            int emergencyIndex = cursor.getColumnIndex("emergency_phone");

            if (nameIndex != -1) etFullName.setText(cursor.getString(nameIndex));
            if (dobIndex != -1) etDob.setText(cursor.getString(dobIndex));
            if (ageIndex != -1) etAge.setText(String.valueOf(cursor.getInt(ageIndex)));
            if (phoneIndex != -1) etPhone.setText(cursor.getString(phoneIndex));
            if (emailIndex != -1) etEmail.setText(cursor.getString(emailIndex));
            if (emergencyIndex != -1) etEmergencyPhone.setText(cursor.getString(emergencyIndex));

            cursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}