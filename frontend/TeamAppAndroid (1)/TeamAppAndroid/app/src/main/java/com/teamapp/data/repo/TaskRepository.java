// com/teamapp/data/repo/TaskRepository.java
package com.teamapp.data.repo;

import androidx.annotation.Nullable;

import com.teamapp.core.db.AppDb;
import com.teamapp.data.api.TaskApi;
import com.teamapp.data.dto.TaskDtos;
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
        Response<List<TaskDtos.TaskDto>> res = api.myTasks(status, page, pageSize).execute();
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

    public void updateStatus(UUID taskId, String status, @Nullable Double position) throws Exception {
        TaskDtos.UpdateTaskStatusRequest req = new TaskDtos.UpdateTaskStatusRequest(status, position);
        Response<Void> res = api.updateStatus(taskId, req).execute();
        if (!res.isSuccessful()) throw new IOException("Không cập nhật trạng thái");

        TaskEntity local = db.taskDao().findById(taskId);
        if (local != null) {
            local.status = status;
            if (position != null) local.position = position;
            db.taskDao().upsert(local);
        }
    }

    public void updateStatus(UUID taskId, String status) throws Exception {
        updateStatus(taskId, status, null);
    }

    public void updatePosition(UUID taskId, Double newPosition) throws Exception {
        TaskEntity cur = db.taskDao().findById(taskId);
        String status = (cur != null && cur.status != null) ? cur.status : "ToDo";
        updateStatus(taskId, status, newPosition);
    }
}
