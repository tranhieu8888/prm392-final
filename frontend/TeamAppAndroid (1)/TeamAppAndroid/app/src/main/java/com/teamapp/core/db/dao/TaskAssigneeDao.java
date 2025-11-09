package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.teamapp.data.entity.TaskAssigneeEntity;

import java.util.List;
import java.util.UUID;

@Dao
public interface TaskAssigneeDao {

    @Insert(onConflict = REPLACE)
    void upsert(TaskAssigneeEntity entity);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<TaskAssigneeEntity> entities);

    @Query("SELECT * FROM task_assignees WHERE taskId = :taskId")
    List<TaskAssigneeEntity> byTask(UUID taskId);

    @Query("SELECT * FROM task_assignees WHERE userId = :userId")
    List<TaskAssigneeEntity> byUser(UUID userId);

    @Query("DELETE FROM task_assignees WHERE taskId = :taskId")
    void clearForTask(UUID taskId);

    @Query("DELETE FROM task_assignees")
    void clearAll();
}
