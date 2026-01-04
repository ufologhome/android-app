package com.example.chat;

import android.content.Context;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.webrtc.*;

import java.net.URI;
import java.util.Collections;

public class WebRTCCallManager {

    private PeerConnectionFactory factory;
    private PeerConnection peerConnection;
    private WebSocketClient ws;
    private Context context;
    private String username;

    public WebRTCCallManager(Context ctx, String username) {
        context = ctx;
        this.username = username;

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(ctx)
                .createInitializationOptions()
        );
        factory = PeerConnectionFactory.builder().createPeerConnectionFactory();

        try {
            ws = new WebSocketClient(new URI("ws://192.168.0.150:8765")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    send("{\"username\":\"" + username + "\"}");
                }
                @Override
                public void onMessage(String message) {
                    // обработка SDP Offer/Answer, ICE
                }
                @Override
                public void onClose(int code, String reason, boolean remote) {}
                @Override
                public void onError(Exception ex) {}
            };
            ws.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void call(String target) {
        // создаём PeerConnection и LocalAudioTrack
        // генерируем Offer → ws.send("{\"to\":\""+target+"\",...}")
    }
}
