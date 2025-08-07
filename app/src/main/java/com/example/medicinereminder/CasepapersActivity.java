package com.example.medicinereminder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CasepapersActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private LinearLayout layoutUploadedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casepapers);

        Button btnUpload = findViewById(R.id.btn_upload_casepaper);
        layoutUploadedFiles = findViewById(R.id.layout_uploaded_files);

        btnUpload.setOnClickListener(view -> openFileChooser());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow any file type
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Casepaper"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                String fileName = getFileName(fileUri);
                addFileToList(fileName, fileUri);
            }
        }
    }

    private void addFileToList(String fileName, Uri fileUri) {
        TextView textView = new TextView(this);
        textView.setText(fileName);
        textView.setTextSize(16);
        textView.setPadding(8, 8, 8, 8);
        textView.setOnClickListener(v -> openFile(fileUri));
        layoutUploadedFiles.addView(textView);
    }

    private void openFile(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, getContentResolver().getType(uri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No app found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        result = cursor.getString(nameIndex);
                    }
                } finally {
                    cursor.close();
                }
            }
        }

        // Fallback in case we couldn't get the name
        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }
}
