// com/teamapp/data/repo/TaskRepository.java
package com.teamapp.data.repo;
import com.teamapp.data.dto.TaskDtos;

import androidx.annotation.Nullable;

import com.teamapp.core.db.AppDb;
import com.teamapp.data.api.TaskApi;
import com.teamapp.data.entity.TaskEntity;
import com.teamapp.data.mapper.Mapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import retrofit2.Response;
import retrofit2.Retrofit;

public class TaskRepository {

    private final TaskApi api;
    private final AppDb db;

    public TaskRepository(Retrofit retrofit, AppDb db) {
        this.api = retrofit.create(TaskApi.class);
        this.db  = db;
    }

    public List<TaskEntity> fetchByProject(UUID projectId) throws Exception {
        Response<List<TaskDtos.TaskDto>> res = api.byProject(projectId).execute();
        if (!res.isSuccessful() || res.body() == null) throw new IOException("Không tải được task");
        List<TaskEntity> entities = Mapper.toTaskEntities(res.body());
        db.taskDao().upsertAll(entities);
        return entities;
    }

    public List<TaskDtos.TaskDto> fetchMy(@Nullable String status, int page, int pageSize) throws Exception {
        Response<List<TaskDtos.TaskDto>> res = api.my(status, page, pageSize).execute();
        if (!res.isSuccessful() || res.body() == null) throw new IOException("Không tải được danh sách task của tôi");
        return res.body();
    }

    public TaskEntity create(UUID projectId, String title, @Nullable String desc) throws Exception {
        TaskDtos.CreateTaskRequest req = new TaskDtos.CreateTaskRequest(title, desc, null, null);
        Response<TaskDtos.TaskDto> res = api.create(projectId, req).execute();
        if (!res.isSuccessful() || res.body() == null) throw new IOException("Không tạo được task");
        TaskEntity e = Mapper.toTaskEntity(res.body());
        db.taskDao().upsert(e);
        return e;
    }

    public TaskEntity create(UUID projectId,
                             String title,
                             @Nullable String desc,
                             @Nullable Date   dueDate,
                             @Nullable List<UUID> assigneeIds) throws Exception {
        TaskDtos.CreateTaskRequest req =
                new TaskDtos.CreateTaskRequest(title, desc, dueDate,
                        (assigneeIds == null || assigneeIds.isEmpty()) ? null : assigneeIds);
        Response<TaskDtos.TaskDto> res = api.create(projectId, req).execute();
        if (!res.isSuccessful() || res.body() == null) throw new IOException("Không tạo được task");
        TaskEntity e = Mapper.toTaskEntity(res.body());
        db.taskDao().upsert(e);
        return e;
    }

    public TaskEntity createWithAssignee(UUID projectId,
                                         String title,
                                         @Nullable String desc,
                                         @Nullable UUID assigneeId) throws Exception {
        List<UUID> ids = assigneeId == null ? null : Collections.singletonList(assigneeId);
        return create(projectId, title, desc, null, ids);
    }

    // ====================================================================
    // ===== BẮT ĐẦU PHẦN SỬA LỖI (GỘP CÁC HÀM UPDATE) =====
    // ====================================================================

    /**
     * Hàm "master" để cập nhật cả status và position.
     * Đây là hàm duy nhất gọi API và DAO.
     */
    public void updateStatusAndPosition(UUID taskId, String newStatus, Double newPosition) throws Exception {
        // Null-safe
        double positionToSend = (newPosition == null) ? 0.0 : newPosition;
        String statusToSend = (newStatus == null) ? "ToDo" : newStatus;

        // ✅ Sửa tên class ở đây
        TaskDtos.UpdateTaskStatusRequest req = new TaskDtos.UpdateTaskStatusRequest();
        req.setStatus(statusToSend);
        req.setPosition(positionToSend);

        // Gọi API
        Response<Void> res = api.updateStatus(taskId, req).execute();
        if (!res.isSuccessful()) {
            throw new IOException("Lỗi khi gọi API cập nhật status (ID: " + taskId + ")");
        }

        // Cập nhật DB
        db.taskDao().updateStatusAndPosition(taskId, statusToSend, positionToSend, new Date());
    }


    /**
     * Hàm này được gọi từ TaskDetailActivity (chỉ cập nhật status, giữ nguyên position).
     * Đây là hàm GÂY LỖI trước đây.
     */
    public void updateStatus(UUID taskId, String status) throws Exception {
        // 1. Tải task cũ từ DB để LẤY position hiện tại
        TaskEntity task = db.taskDao().findById(taskId);

        // 2. Lấy position hiện tại (có thể bị null)
        Double currentPosition = (task != null) ? task.position : null;

        // 3. Gọi hàm "master" với status mới và position CŨ
        updateStatusAndPosition(taskId, status, currentPosition);
    }

    /**
     * Hàm này dùng để cập nhật position (giữ nguyên status)
     */
    public void updatePosition(UUID taskId, Double newPosition) throws Exception {
        // 1. Tải task cũ từ DB để LẤY status hiện tại
        TaskEntity cur = db.taskDao().findById(taskId);

        // 2. Lấy status hiện tại (nếu null thì mặc định là "ToDo")
        String currentStatus = (cur != null && cur.status != null) ? cur.status : "ToDo";

        // 3. Gọi hàm "master" với position mới và status CŨ
        updateStatusAndPosition(taskId, currentStatus, newPosition);
    }

    // ====================================================================
    // ===== KẾT THÚC PHẦN SỬA LỖI =====
    // ====================================================================
}