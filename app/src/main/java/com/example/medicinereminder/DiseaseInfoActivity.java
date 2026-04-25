package com.example.medicinereminder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DiseaseInfoActivity extends AppCompatActivity {

    RadioGroup diseaseRadioGroup, severityGroup;
    TextView infoCard, riskIndicator;
    EditText durationInput, customDiseaseInput;
    Button addDiseaseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_info);

        diseaseRadioGroup = findViewById(R.id.diseaseRadioGroup);
        severityGroup = findViewById(R.id.severityGroup);
        infoCard = findViewById(R.id.infoCard);
        riskIndicator = findViewById(R.id.riskIndicator);
        durationInput = findViewById(R.id.durationInput);
        customDiseaseInput = findViewById(R.id.customDiseaseInput);
        addDiseaseBtn = findViewById(R.id.addDiseaseBtn);

        diseaseRadioGroup.setOnCheckedChangeListener((group, checkedId) -> updateInfo());
        severityGroup.setOnCheckedChangeListener((group, checkedId) -> updateInfo());

        addDiseaseBtn.setOnClickListener(v -> {
            String newDisease = customDiseaseInput.getText().toString().trim();

            if (!newDisease.isEmpty()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(newDisease);
                radioButton.setLayoutParams(
                        new RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.MATCH_PARENT,
                                RadioGroup.LayoutParams.WRAP_CONTENT
                        )
                );

                diseaseRadioGroup.addView(radioButton);
                customDiseaseInput.setText("");
            }
        });
    }

    private void updateInfo() {

        int selectedDiseaseId = diseaseRadioGroup.getCheckedRadioButtonId();
        int selectedSeverityId = severityGroup.getCheckedRadioButtonId();

        if (selectedDiseaseId == -1 || selectedSeverityId == -1) return;

        RadioButton diseaseBtn = findViewById(selectedDiseaseId);
        RadioButton severityBtn = findViewById(selectedSeverityId);

        String disease = diseaseBtn.getText().toString().toLowerCase();
        String severity = severityBtn.getText().toString();

        String durationText = durationInput.getText().toString();
        int days = durationText.isEmpty() ? 0 : Integer.parseInt(durationText);

        infoCard.setText(getDiseaseInfo(disease));

        if (severity.contains("Severe") || days > 7) {
            riskIndicator.setText("⚠ HIGH RISK - Consult Doctor Immediately");
            riskIndicator.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (severity.contains("Moderate") || days > 3) {
            riskIndicator.setText("🟡 MODERATE RISK - Monitor Closely");
            riskIndicator.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            riskIndicator.setText("🟢 LOW RISK - Home Care Recommended");
            riskIndicator.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private String getDiseaseInfo(String disease) {

        if (disease.contains("fever"))
            return "🌡 Fever\n\nSymptoms:\n• High temperature\n• Chills\n\nTreatment:\n• Rest\n• Fluids";

        if (disease.contains("diabetes"))
            return "💉 Diabetes\n\nSymptoms:\n• Frequent urination\n• Fatigue\n\nTreatment:\n• Insulin\n• Diet control";

        if (disease.contains("cough"))
            return "🤧 Cough\n\nTreatment:\n• Warm fluids\n• Steam inhalation";

        if (disease.contains("hypertension"))
            return "💓 Hypertension\n\nTreatment:\n• Low salt diet\n• BP medication";

        if (disease.contains("asthma"))
            return "🫁 Asthma\n\nTreatment:\n• Inhalers\n• Avoid triggers";

        if (disease.contains("flu"))
            return "🤒 Flu\n\nTreatment:\n• Rest\n• Fluids";

        if (disease.contains("migraine"))
            return "🤕 Migraine\n\nTreatment:\n• Pain relievers\n• Dark room";

        return "No detailed information available.";
    }
}