package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    private TextView chatTextView;
    private EditText messageInput;
    private Button sendButton;
    private ScrollView scrollView;

    private ChatClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatTextView = findViewById(R.id.chatTextView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        scrollView = findViewById(R.id.scrollView);

        String ip = "192.168.0.150";
        int port = 9009;
        String key = "12345";
        String username = "UFO"; // or "Julyet"

        client = new ChatClient(ip, port, key, username, new ChatClient.OnMessageListener() {
            @Override
            public void onMessage(String message) {
                chatTextView.append(message + "\n");
                scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                client.send(msg);
                messageInput.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.close();
    }
}
