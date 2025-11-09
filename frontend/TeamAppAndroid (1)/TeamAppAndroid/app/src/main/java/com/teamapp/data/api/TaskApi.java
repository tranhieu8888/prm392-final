package com.teamapp.data.api;



import com.teamapp.data.dto.TaskDtos;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// TaskApi: thêm tuỳ chọn "my" nếu dùng
public interface TaskApi {
    @GET("api/projects/{projectId}/tasks")
    Call<List<TaskDtos.TaskDto>> byProject(@Path("projectId") UUID projectId);

    @POST("api/projects/{projectId}/tasks")
    Call<TaskDtos.TaskDto> create(@Path("projectId") UUID projectId, @Body TaskDtos.CreateTaskRequest req);

    @PATCH("api/tasks/{taskId}/status")
    Call<Void> updateStatus(@Path("taskId") UUID taskId, @Body TaskDtos.UpdateTaskStatusRequest req);

    // mới: khớp /api/tasks/my trên backend
    @GET("api/tasks/my")
    Call<List<TaskDtos.TaskDto>> my(
            @Query("status") String status,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
}

