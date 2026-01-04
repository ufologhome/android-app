package com.example.chat;

import java.io.*;
import java.net.Socket;
import android.os.Handler;
import android.os.Looper;

public class ChatClient {
    public interface OnMessageListener {
        void onMessage(String message);
        void onError(String error);
    }

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private OnMessageListener listener;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public ChatClient(String ip, int port, String key, String username, OnMessageListener listener) {
        this.listener = listener;

        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                // Handshake
                if (!reader.readLine().equals("KEY:")) throw new IOException("Handshake failed");
                writer.write(key + "\n");
                writer.flush();

                if (!reader.readLine().equals("USERNAME:")) throw new IOException("Handshake failed");
                writer.write(username + "\n");
                writer.flush();

                String response = reader.readLine();
                if (!response.equals("OK")) throw new IOException("Handshake failed: " + response);

                // Start reading messages
                String line;
                while ((line = reader.readLine()) != null) {
                    String finalLine = line;
                    mainHandler.post(() -> listener.onMessage(finalLine));
                }
            } catch (Exception e) {
                mainHandler.post(() -> listener.onError(e.getMessage()));
            }
        }).start();
    }

    public void send(String msg) {
        new Thread(() -> {
            try {
                writer.write(msg + "\n");
                writer.flush();
            } catch (IOException e) {
                mainHandler.post(() -> listener.onError("Failed to send"));
            }
        }).start();
    }

    public void close() {
        try { socket.close(); } catch (Exception ignored) {}
    }
}
