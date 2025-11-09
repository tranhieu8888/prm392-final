package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class AuthDtos {
    public static class RegisterRequest {
        @SerializedName("fullName") public String fullName;
        @SerializedName("email") public String email;
        @SerializedName("password") public String password;
        public RegisterRequest(String fullName, String email, String password) {
            this.fullName = fullName; this.email = email; this.password = password;
        }
    }

    public static class LoginRequest {
        @SerializedName("email") public String email;
        @SerializedName("password") public String password;
        public LoginRequest(String email, String password) {
            this.email = email; this.password = password;
        }
    }

    public static class UserDto {
        @SerializedName("id") public UUID id;
        @SerializedName("fullName") public String fullName;
        @SerializedName("email") public String email;
        @SerializedName("avatarUrl") public String avatarUrl;
    }

    public static class AuthResponse {
        @SerializedName("token") public String token;
        @SerializedName("user") public UserDto user;
    }
}
