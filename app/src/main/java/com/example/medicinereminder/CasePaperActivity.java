package com.example.medicinereminder;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CasePaperActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;
    private ListView listViewPapers;
    private DatabaseHelper databaseHelper;
    private ArrayList<String> fileNames = new ArrayList<>();
    private ArrayList<String> uriList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_paper);

        databaseHelper = new DatabaseHelper(this);

        Button btnUpload = findViewById(R.id.btnUpload);
        Button btnView = findViewById(R.id.btnView);
        listViewPapers = findViewById(R.id.listViewPapers);

        btnUpload.setOnClickListener(v -> openFileChooser());
        btnView.setOnClickListener(v -> openLatestPDF());

        loadAllCasePapers();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri pdfUri = data.getData();
            if (pdfUri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(
                            pdfUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                String fileName = pdfUri.getLastPathSegment();
                long id = databaseHelper.insertCasePaper(fileName, pdfUri.toString(), "");

                if (id != -1) {
                    Toast.makeText(this, "Case Paper Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    loadAllCasePapers();
                } else {
                    Toast.makeText(this, "Error saving case paper", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void openLatestPDF() {
        Cursor cursor = databaseHelper.getLatestCasePaper();
        if (cursor != null && cursor.moveToFirst()) {
            int uriIndex = cursor.getColumnIndex("file_uri");
            if (uriIndex != -1) {
                String uriString = cursor.getString(uriIndex);
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
            cursor.close();
        } else {
            Toast.makeText(this, "No Case Paper Uploaded Yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAllCasePapers() {
        fileNames.clear();
        uriList.clear();

        Cursor cursor = databaseHelper.getAllCasePapers();
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("file_name");
            int uriIndex = cursor.getColumnIndex("file_uri");

            do {
                if (nameIndex != -1 && uriIndex != -1) {
                    fileNames.add(cursor.getString(nameIndex));
                    uriList.add(cursor.getString(uriIndex));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        listViewPapers.setAdapter(adapter);

        listViewPapers.setOnItemClickListener((parent, view, position, id) -> {
            Uri uri = Uri.parse(uriList.get(position));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });
    }
}