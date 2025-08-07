package com.example.medicinereminder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DiseaseInfoActivity extends AppCompatActivity {

    RadioGroup diseaseRadioGroup;
    EditText addDiseaseEditText;
    Button addDiseaseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_info);

        diseaseRadioGroup = findViewById(R.id.diseaseRadioGroup);
        addDiseaseEditText = findViewById(R.id.addDiseaseEditText);
        addDiseaseBtn = findViewById(R.id.addDiseaseBtn);

        // Show info when a disease is selected
        diseaseRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            if (selectedRadioButton != null) {
                String selectedDisease = selectedRadioButton.getText().toString();
                String diseaseInfo = getDiseaseInfo(selectedDisease);
                Toast.makeText(this, diseaseInfo, Toast.LENGTH_LONG).show();
            }
        });

        // Handle Add Disease Button
        addDiseaseBtn.setOnClickListener(v -> {
            if (addDiseaseEditText.getVisibility() == View.GONE) {
                addDiseaseEditText.setVisibility(View.VISIBLE);
                addDiseaseBtn.setText("Add Disease");
            } else {
                String newDisease = addDiseaseEditText.getText().toString().trim();

                if (!newDisease.isEmpty()) {
                    // Create a new RadioButton dynamically
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(newDisease);
                    radioButton.setId(View.generateViewId());
                    diseaseRadioGroup.addView(radioButton);

                    // Reset input
                    addDiseaseEditText.setText("");
                    addDiseaseEditText.setVisibility(View.GONE);
                    addDiseaseBtn.setText("Add Custom Disease");

                    Toast.makeText(this, "Disease Added: " + newDisease, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please enter a disease name.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getDiseaseInfo(String disease) {
        switch (disease.toLowerCase()) {
            case "diabetes":
                return "Diabetes is a condition that occurs when your blood sugar (glucose) is too high.";
            case "hypertension":
                return "Hypertension is another term for high blood pressure.";
            case "asthma":
                return "Asthma is a condition that causes your airways to swell and narrow.";
            case "cancer":
                return "Cancer is a group of diseases involving abnormal cell growth.";
            case "covid-19":
                return "COVID-19 is an infectious disease caused by the SARS-CoV-2 virus.";
            case "flu":
                return "The flu is a contagious respiratory illness caused by influenza viruses.";
            default:
                return "Sorry, no information available for this disease.";
        }
    }
}
