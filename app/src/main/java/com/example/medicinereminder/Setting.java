package com.example.medicinereminder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class Setting extends Activity {

    private static final int REQUEST_CODE_RINGTONE = 1;

    private Switch switchNotifications, switchPopup;
    private Button btnSelectSound, btnSaveSettings;
    private Uri notificationSoundUri;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize
        switchNotifications = findViewById(R.id.switch_notifications);
        switchPopup = findViewById(R.id.switch_popup);
        btnSelectSound = findViewById(R.id.btn_select_sound);
        btnSaveSettings = findViewById(R.id.btn_save_settings);
        preferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);

        // Load saved settings
        switchNotifications.setChecked(preferences.getBoolean("enabled", true));
        switchPopup.setChecked(preferences.getBoolean("popup", true));
        String soundUriStr = preferences.getString("sound", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString());
        notificationSoundUri = Uri.parse(soundUriStr);

        // Sound picker
        btnSelectSound.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Tone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, notificationSoundUri);
            startActivityForResult(intent, REQUEST_CODE_RINGTONE);
        });

        // Save settings
        btnSaveSettings.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("enabled", switchNotifications.isChecked());
            editor.putBoolean("popup", switchPopup.isChecked());
            editor.putString("sound", notificationSoundUri.toString());
            editor.apply();
            Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RINGTONE && resultCode == RESULT_OK) {
            notificationSoundUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (notificationSoundUri != null) {
                String ringtoneTitle = RingtoneManager.getRingtone(this, notificationSoundUri).getTitle(this);
                Toast.makeText(this, "Selected: " + ringtoneTitle, Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
