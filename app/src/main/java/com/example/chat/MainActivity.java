package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    private TextView chatTextView;
    private EditText messageInput;
    private Button sendButton, callButton;
    private ScrollView scrollView;

    private ChatClient client;
    private WebRTCCallManager callManager;

    private final String username = "Julyet"; // UFO / Julyet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatTextView = findViewById(R.id.chatTextView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        callButton = findViewById(R.id.callButton);
        scrollView = findViewById(R.id.scrollView);

        // --- текстовый чат ---
        client = new ChatClient("192.168.0.150", 9009, "12345", username,
                new ChatClient.OnMessageListener() {
            @Override
            public void onMessage(String message) {
                if (message.startsWith(username + ":")) return;
                chatTextView.append(message + "\n");
                scrollToBottom();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (msg.isEmpty()) return;

            chatTextView.append("Me: " + msg + "\n");
            scrollToBottom();
            client.send(msg);
            messageInput.setText("");
        });

        // --- звонки ---
        callManager = new WebRTCCallManager(this, username);

        callButton.setOnClickListener(v -> {
            String target = "UFO"; // или выбранный собеседник
            callManager.call(target);
        });
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) client.close();
    }
}
