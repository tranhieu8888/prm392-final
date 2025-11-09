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
}
