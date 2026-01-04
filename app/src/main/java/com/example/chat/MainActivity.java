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

    private final String username = "Julyet"; // UFO / Julyet

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

        client = new ChatClient(ip, port, key, username,
                new ChatClient.OnMessageListener() {

            @Override
            public void onMessage(String message) {
                // НЕ показываем сообщение,
                // которое сервер прислал от нас самих
                if (message.startsWith(username + ":")) {
                    return;
                }

                chatTextView.append(message + "\n");
                scrollToBottom();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(
                        MainActivity.this,
                        error,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (msg.isEmpty()) return;

            // 1️⃣ СРАЗУ показываем своё сообщение
            chatTextView.append("Me: " + msg + "\n");
            scrollToBottom();

            // 2️⃣ Отправляем на сервер
            client.send(msg);

            messageInput.setText("");
        });
    }

    private void scrollToBottom() {
        scrollView.post(() ->
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.close();
        }
    }
}
