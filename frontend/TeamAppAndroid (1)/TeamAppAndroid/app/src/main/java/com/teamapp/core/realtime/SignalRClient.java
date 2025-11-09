package com.teamapp.core.realtime;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Client SignalR:
 * - start(baseUrl, jwt, listener)
 * - join(conversationId), leave(conversationId), stop()
 *
 * Server gửi event: "messageReceived", payload dạng:
 * {
 *   "conversationId": "...",
 *   "message": { "id": "...", "conversationId":"...", "senderId":"...", "senderName":"...", "body":"...", "createdAt":"2025-11-06T12:34:56Z" }
 * }
 */
public class SignalRClient {

    public interface MessageListener {
        void onMessage(@NonNull MessageEnvelope envelope);
    }

    private final Gson gson = new Gson();
    private HubConnection hub;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private MessageListener listener;

    public void start(@NonNull String baseUrl, @NonNull String jwt, @NonNull MessageListener listener) {
        this.listener = listener;

        String hubUrl = baseUrl + "/hubs/chat";
        hub = HubConnectionBuilder.create(hubUrl)
                // Java client không có AccessTokenProvider như .NET; dùng header Authorization
                .withHeader("Authorization", "Bearer " + jwt)
                .build();

        // Đăng ký handler nhận message
        hub.on("messageReceived", (Object payload) -> {
            try {
                // payload có thể là Map hoặc JsonElement -> serialize lại rồi parse
                String json = gson.toJson(payload);
                MessageEnvelope env = gson.fromJson(json, MessageEnvelope.class);
                if (env != null && listener != null) listener.onMessage(env);
            } catch (Exception ignored) {}
        }, Object.class);

        // Kết nối (blockingAwait để chắc chắn đã start xong trước khi join)
        try {
            hub.start().blockingAwait();
            connected.set(true);
        } catch (Throwable t) {
            connected.set(false);
        }
    }

    public boolean isConnected() { return connected.get(); }
    // thêm vào trong class SignalRClient
    public void setOnMessageListener(MessageListener l) {
        this.listener = l;
    }

    public void join(@NonNull UUID conversationId) {
        if (hub == null || !isConnected()) return;
        try { hub.send("JoinConversation", conversationId.toString()); } catch (Throwable ignored) {}
    }

    public void leave(@NonNull UUID conversationId) {
        if (hub == null || !isConnected()) return;
        try { hub.send("LeaveConversation", conversationId.toString()); } catch (Throwable ignored) {}
    }

    public void stop() {
        if (hub == null) return;
        try { hub.stop().blockingAwait(); } catch (Throwable ignored) {}
        connected.set(false);
    }

    /* ===== Models khớp payload từ server ===== */

    public static class MessageEnvelope {
        @SerializedName("conversationId")
        public UUID conversationId;

        @SerializedName("message")
        public MessageDto message;
    }

    public static class MessageDto {
        @SerializedName("id")
        public UUID id;
        @SerializedName("conversationId")
        public UUID conversationId;
        @SerializedName("senderId")
        public UUID senderId;
        @SerializedName("senderName")
        public String senderName;
        @SerializedName("body")
        public String body;
        @SerializedName("createdAt")
        public String createdAt; // parse thành Date khi mapping sang Entity nếu cần
    }
}
