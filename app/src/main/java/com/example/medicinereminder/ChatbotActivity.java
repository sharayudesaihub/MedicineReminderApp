package com.example.medicinereminder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ChatbotActivity extends AppCompatActivity {

    private EditText userInput;
    private Button sendButton;
    private ListView chatListView;
    private ArrayList<String> chatList;
    private ArrayAdapter<String> chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);
        chatListView = findViewById(R.id.chatListView);

        chatList = new ArrayList<>();
        chatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatList);
        chatListView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = userInput.getText().toString().trim();
                if (!message.isEmpty()) {
                    chatList.add("👤 You: " + message);
                    userInput.setText("");
                    chatList.add("🤖 Bot: " + getBotReply(message));
                    chatAdapter.notifyDataSetChanged();
                    chatListView.setSelection(chatList.size() - 1);
                }
            }
        });
    }

    private String getBotReply(String msg) {
        msg = msg.toLowerCase();

        if (msg.contains("hello") || msg.contains("hi")) {
            return "Hello! How can I help you today?";
        } else if (msg.contains("medicine")) {
            return "Please take your medicine as prescribed. Do you want me to remind you?";
        } else if (msg.contains("miss") && msg.contains("dose")) {
            return "If you miss a dose, take it as soon as you remember. But don’t double the next dose!";
        } else if (msg.contains("fever")) {
            return "For fever, stay hydrated and take paracetamol as prescribed.";
        } else if (msg.contains("pain")) {
            return "If the pain persists, please consult your doctor.";
        } else if (msg.contains("bye")) {
            return "Goodbye! Take care 😊";
        } else {
            return "I'm not sure about that. Please consult your doctor for detailed advice.";
        }
    }
}
