package com.example.chat;

import java.io.*;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    private OnMessageListener listener;

    public interface OnMessageListener {
        void onMessage(String message);
        void onError(String error);
    }

    public ChatClient(String ip, int port, String key, String username, OnMessageListener listener) {
        this.listener = listener;
        this.username = username;

        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                // handshake
                writer.write(key + "\n");
                writer.flush();

                writer.write(username + "\n");
                writer.flush();

                String response = reader.readLine();
                if (!"OK".equals(response)) {
                    listener.onError("Handshake failed");
                    return;
                }

                // read loop
                String line;
                while ((line = reader.readLine()) != null) {
                    listener.onMessage(line);
                }

            } catch (IOException e) {
                listener.onError("Connection error: " + e.getMessage());
            }
        }).start();
    }

    public void send(String msg) {
        new Thread(() -> {
            try {
                writer.write(msg + "\n");
                writer.flush();
            } catch (IOException e) {
                listener.onError("Failed to send");
            }
        }).start();
    }

    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
