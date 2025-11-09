package com.teamapp.data.dto;

import java.util.UUID;

public class ProfileDtos {
    public static class UpdateProfileRequest {
        public String fullName;
        public String avatarUrl;
        public UpdateProfileRequest(String fullName, String avatarUrl){
            this.fullName = fullName; this.avatarUrl = avatarUrl;
        }
    }
    public static class ChangePasswordRequest {
        public String currentPassword;
        public String newPassword;
        public ChangePasswordRequest(String cur, String nw){ currentPassword = cur; newPassword = nw; }
    }
    public static class UserDto {
        public UUID id;
        public String email;
        public String fullName;
        public String avatarUrl;
    }
}
