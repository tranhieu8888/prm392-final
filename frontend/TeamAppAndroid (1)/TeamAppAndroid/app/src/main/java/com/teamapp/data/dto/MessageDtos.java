package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.UUID;

public class MessageDtos {

    public static class MessageDto {
        @SerializedName("id") public UUID id;
        @SerializedName("conversationId") public UUID conversationId;
        @SerializedName("senderId") public UUID senderId;
        @SerializedName("body") public String body;
        @SerializedName("createdAt") public Date createdAt;
    }

    public static class SendMessageRequest {
        @SerializedName("body") public String body;
        public SendMessageRequest(String body) { this.body = body; }
    }
}
