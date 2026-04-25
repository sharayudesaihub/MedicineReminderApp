package com.example.medicinereminder;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotActivity extends AppCompatActivity {

    TextView chatOutput;
    EditText userInput;
    String casePaperText = "";
    private DatabaseHelper databaseHelper;

    // 🔑 ADD YOUR GEMINI API KEY HERE
    private final String API_KEY = "AIzaSyDh3xduhW-5I-fzhLZ-lGOc_Za6J4vxsc0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot2);

        databaseHelper = new DatabaseHelper(this);
        PDFBoxResourceLoader.init(getApplicationContext());

        chatOutput = findViewById(R.id.chatOutput);
        userInput = findViewById(R.id.userInput);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnSummarize = findViewById(R.id.btnSummarize);

        loadLatestCasePaper();

        btnSend.setOnClickListener(v -> {
            String message = userInput.getText().toString().trim();
            if (message.isEmpty()) return;

            chatOutput.append("\n\n👤 You: " + message);
            userInput.setText("");

            callAI(message);
        });

        btnSummarize.setOnClickListener(v -> showCasePaperSelectionDialog());
    }

    private void loadLatestCasePaper() {
        Cursor cursor = databaseHelper.getLatestCasePaper();
        if (cursor != null && cursor.moveToFirst()) {
            int uriIndex = cursor.getColumnIndex("file_uri");
            if (uriIndex != -1) {
                String uriString = cursor.getString(uriIndex);
                if (uriString != null && !uriString.isEmpty()) {
                    loadPdfFromUri(Uri.parse(uriString));
                }
            }
            cursor.close();
        }
    }

    private void showCasePaperSelectionDialog() {
        Cursor cursor = databaseHelper.getAllCasePapers();

        if (cursor == null || cursor.getCount() == 0) {
            chatOutput.append("\n🤖 Bot: No case papers uploaded.");
            if (cursor != null) cursor.close();
            return;
        }

        ArrayList<String> fileNames = new ArrayList<>();
        final ArrayList<String> uriList = new ArrayList<>();

        int nameIndex = cursor.getColumnIndex("file_name");
        int uriIndex = cursor.getColumnIndex("file_uri");

        if (nameIndex != -1 && uriIndex != -1) {
            while (cursor.moveToNext()) {
                fileNames.add(cursor.getString(nameIndex));
                uriList.add(cursor.getString(uriIndex));
            }
        }
        cursor.close();

        String[] items = fileNames.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Select Case Paper")
                .setItems(items, (dialog, which) -> {
                    Uri selectedUri = Uri.parse(uriList.get(which));
                    loadPdfFromUri(selectedUri);
                    callAI("Summarize this medical report in simple language.");
                })
                .show();
    }

    private void loadPdfFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            PDDocument document = PDDocument.load(inputStream);
            PDFTextStripper stripper = new PDFTextStripper();
            casePaperText = stripper.getText(document);

            document.close();
            inputStream.close();

        } catch (Exception e) {
            chatOutput.append("\n⚠ Error reading PDF");
        }
    }

    private void callAI(String userMessage) {

        if (API_KEY.isEmpty()) {
            runOnUiThread(() ->
                    chatOutput.append("\n⚠ Add your Gemini API key first")
            );
            return;
        }

        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                JSONObject json = new JSONObject();
                JSONArray contents = new JSONArray();

                JSONObject userContent = new JSONObject();
                userContent.put("role", "user");

                JSONArray parts = new JSONArray();

                // 🔥 LIMIT PDF TEXT (IMPORTANT)
                String finalPrompt = "";

                if (!casePaperText.isEmpty()) {
                    int maxLength = Math.min(casePaperText.length(), 3000);
                    String limitedText = casePaperText.substring(0, maxLength);
                    finalPrompt += "Patient report:\n" + limitedText + "\n\n";
                }

                finalPrompt += userMessage;

                JSONObject textPart = new JSONObject();
                textPart.put("text", finalPrompt);

                parts.put(textPart);
                userContent.put("parts", parts);

                contents.put(userContent);
                json.put("contents", contents);

                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url("https://generativelanguage.googleapis.com/v1/models/gemini-1.0-pro:generateContent?key=" + API_KEY)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    int code = response.code();
                    String error = response.body() != null ? response.body().string() : "";

                    runOnUiThread(() ->
                            chatOutput.append("\n⚠ Error " + code + ": " + error)
                    );
                    return;
                }

                String responseBody = response.body().string();

                JSONObject obj = new JSONObject(responseBody);

                String reply = obj.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");

                runOnUiThread(() ->
                        chatOutput.append("\n🤖 Bot: " + reply)
                );

            } catch (Exception e) {
                runOnUiThread(() ->
                        chatOutput.append("\n⚠ Error: " + e.getMessage())
                );
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}