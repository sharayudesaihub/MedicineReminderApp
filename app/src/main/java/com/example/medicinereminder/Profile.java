package com.example.medicinereminder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    EditText etFullName, etDob, etAge, etGender, etPhone, etEmail, etCity, etState;
    Button btnSaveProfile;
    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Make sure the layout file matches

        // Initialize Views
        imgProfile = findViewById(R.id.imgProfile);
        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etAge = findViewById(R.id.etAge);
        etGender = findViewById(R.id.etGender);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Save logic
        btnSaveProfile.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(Profile.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Profile.this, "Profile saved!", Toast.LENGTH_SHORT).show();
                // You can add database save logic here later
            }
        });
    }
}
