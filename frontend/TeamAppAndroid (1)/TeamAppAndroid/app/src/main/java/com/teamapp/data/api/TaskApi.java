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

/**
 * TaskApi: Interface định nghĩa các endpoint liên quan đến Task.
 */
public interface TaskApi {

    /**
     * Lấy danh sách task theo ID dự án (Dùng cho Kanban Board).
     * GET /api/projects/{projectId}/tasks
     */
    @GET("api/projects/{projectId}/tasks")
    Call<List<TaskDtos.TaskDto>> byProject(@Path("projectId") UUID projectId);

    /**
     * Tạo task mới trong dự án.
     * POST /api/projects/{projectId}/tasks
     */
    @POST("api/projects/{projectId}/tasks")
    Call<TaskDtos.TaskDto> create(@Path("projectId") UUID projectId, @Body TaskDtos.CreateTaskRequest req);

    /**
     * Cập nhật trạng thái của một task (Dùng cho Drag-and-drop).
     * PATCH /api/tasks/{taskId}/status
     */
    @PATCH("api/tasks/{taskId}/status")
    Call<Void> updateStatus(@Path("taskId") UUID taskId, @Body TaskDtos.UpdateTaskStatusRequest req);

    /**
     * Lấy danh sách các task được giao cho người dùng hiện tại (Dùng cho Màn hình 9).
     * GET /api/tasks/my
     */
    @GET("api/tasks/my")
    Call<List<TaskDtos.TaskDto>> myTasks( // Đã đổi tên từ 'my' thành 'myTasks'
                                          @Query("status") String status,
                                          @Query("page") int page,
                                          @Query("pageSize") int pageSize
    );
}