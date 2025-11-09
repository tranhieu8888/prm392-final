package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TaskDtos {
    public static class CreateTaskRequest {
        @SerializedName("title") public String title;
        @SerializedName("description") public String description;
        @SerializedName("dueDate") public Date dueDate;
        @SerializedName("assigneeIds") public List<UUID> assigneeIds;
        public CreateTaskRequest(String t, String d, Date due, List<UUID> ids) {
            title = t; description = d; dueDate = due; assigneeIds = ids;
        }
    }

    public static class TaskDto {
        @SerializedName("id") public UUID id;
        @SerializedName("projectId") public UUID projectId;
        @SerializedName("title") public String title;
        @SerializedName("description") public String description;
        @SerializedName("status") public String status;
        @SerializedName("position") public double position;
        @SerializedName("dueDate") public Date dueDate;
        @SerializedName("updatedAt") public Date updatedAt;
    }

    public static class UpdateTaskStatusRequest {
        @SerializedName("status") public String status;
        @SerializedName("position") public double position;
        public UpdateTaskStatusRequest(String s, double p) {
            status = s; position = p;
        }
    }
}
