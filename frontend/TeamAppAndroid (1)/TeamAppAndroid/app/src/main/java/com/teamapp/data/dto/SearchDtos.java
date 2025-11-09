package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchDtos {
    public static class SearchResultDto {
        @SerializedName("projects") public List<ProjectDtos.ProjectDto> projects;
        @SerializedName("tasks") public List<TaskDtos.TaskDto> tasks;
        @SerializedName("users") public List<AuthDtos.UserDto> users;
    }
}
