package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;

public class DeviceDtos {
    public static class SaveDeviceTokenRequest {
        @SerializedName("fcmToken") public String fcmToken;
        @SerializedName("platform") public String platform;
        public SaveDeviceTokenRequest(String t, String p) { fcmToken = t; platform = p; }
    }
}
