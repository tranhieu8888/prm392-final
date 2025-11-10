package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.teamapp.data.entity.TaskEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM tasks " +
            "WHERE dueDate >= :startMs AND dueDate < :endMs " +
            "ORDER BY dueDate ASC")
    List<TaskEntity> byDayRange(long startMs, long endMs);

    // com/teamapp/core/db/dao/TaskDao.java
    @Query("SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER(:like) OR LOWER(description) LIKE LOWER(:like) ORDER BY updatedAt DESC")
    List<TaskEntity> searchByText(String like);

    @Insert(onConflict = REPLACE)
    void upsert(TaskEntity task);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<TaskEntity> tasks);

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    TaskEntity findById(UUID id);

    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY position ASC, updatedAt DESC")
    List<TaskEntity> byProject(UUID projectId);

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY updatedAt DESC")
    List<TaskEntity> byStatus(String status);

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :q || '%' OR description LIKE '%' || :q || '%' ORDER BY updatedAt DESC")
    List<TaskEntity> search(String q);

    @Query("UPDATE tasks SET status = :status, position = :position, updatedAt = :updatedAt WHERE id = :taskId")
    void updateStatusAndPosition(UUID taskId, String status, double position, Date updatedAt);

    @Query("UPDATE tasks SET title=:title, description=:desc, dueDate=:due WHERE id=:taskId")
    void updateContent(UUID taskId, String title, String desc, Date due);

    @Query("DELETE FROM tasks WHERE projectId = :projectId")
    void clearByProject(UUID projectId);

    @Query("DELETE FROM tasks")
    void clearAll();

    @Update
    void update(TaskEntity task);

    /* ===== Projection: Task + projectName để hiển thị đẹp ===== */
    class TaskWithProjectName {
        public UUID id;
        public UUID projectId;
        public String title;
        public String description;
        public String status;
        public Double position;
        public Date dueDate;
        public Date updatedAt;
        @ColumnInfo(name = "projectName")
        public String projectName;
    }
    // <-- Cái bạn đang gọi trong GlobalSearchActivity
    @Query("SELECT * FROM tasks ORDER BY updatedAt DESC")
    List<TaskEntity> getAll();
    @Query("""
           SELECT t.*, p.name AS projectName
           FROM tasks t
           JOIN projects p ON p.id = t.projectId
           WHERE (:status IS NULL OR t.status = :status)
             AND (t.title LIKE '%' || :q || '%' OR t.description LIKE '%' || :q || '%')
           ORDER BY t.updatedAt DESC
           """)
    List<TaskWithProjectName> searchWithProjectName(String q, String status);
    @Query("SELECT * FROM tasks WHERE strftime('%Y-%m-%d', dueDate/1000, 'unixepoch') = :ymd ORDER BY updatedAt DESC")
    List<TaskEntity> byDayString(String ymd);

}
