package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class MemberDtos {
    public static class MemberDto {
        @SerializedName("userId") public UUID userId;
        @SerializedName("fullName") public String fullName;
        @SerializedName("email") public String email;
        @SerializedName("avatarUrl") public String avatarUrl;
        @SerializedName("role") public String role;
    }
}
