package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.UUID;

public class NotificationDtos {

    public static class NotificationDto {
        @SerializedName("id") public UUID id;
        @SerializedName("type") public String type;
        @SerializedName("dataJson") public String dataJson;
        @SerializedName("isRead") public boolean isRead;
        @SerializedName("createdAt") public Date createdAt;
    }

    public static class MarkReadRequest {
        @SerializedName("isRead") public boolean isRead;
        public MarkReadRequest(boolean isRead) { this.isRead = isRead; }
    }
}
