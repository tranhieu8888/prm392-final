package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.UUID;

public class ProjectDtos {

    public static class CreateProjectRequest {
        @SerializedName("name") public String name;
        @SerializedName("description") public String description;
        @SerializedName("isPublic") public boolean isPublic;
        public CreateProjectRequest(String name, String description, boolean isPublic) {
            this.name = name; this.description = description; this.isPublic = isPublic;
        }
    }

    public static class ProjectDto {
        @SerializedName("id") public UUID id;
        @SerializedName("name") public String name;
        @SerializedName("description") public String description;
        @SerializedName("isPublic") public boolean isPublic;
        @SerializedName("createdAt") public Date createdAt;
    }

    // --- Thêm lớp mới cho phản hồi Yêu cầu Tham gia ---
    public static class JoinRequestResponse {
        @SerializedName("status") public String status; // Ví dụ: "PENDING", "SENT", "ALREADY_MEMBER"
        @SerializedName("message") public String message; // Thông báo chi tiết

        public JoinRequestResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
    // --- Kết thúc phần thêm mới ---
}