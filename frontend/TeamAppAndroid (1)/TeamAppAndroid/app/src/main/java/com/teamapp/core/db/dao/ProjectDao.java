package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.teamapp.data.entity.ProjectEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Dao
public interface ProjectDao {

    // <-- Dùng để observe ở UI (ProjectsActivity)
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    LiveData<List<ProjectEntity>> all();
    @Insert(onConflict = REPLACE)
    void upsert(ProjectEntity project);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<ProjectEntity> projects);

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    ProjectEntity findById(UUID id);

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    List<ProjectEntity> getAll();

    @Query("SELECT * FROM projects WHERE name LIKE '%' || :q || '%' ORDER BY createdAt DESC")
    List<ProjectEntity> searchByName(String q);

    @Query("UPDATE projects SET name=:name, description=:desc, isPublic=:isPublic WHERE id=:id")
    void updateBrief(UUID id, String name, String desc, boolean isPublic);

    @Query("DELETE FROM projects")
    void clear();

    @Query("DELETE FROM projects WHERE createdAt < :before")
    void deleteOlderThan(Date before);

    @Update
    void update(ProjectEntity project);
}
