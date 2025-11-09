package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.teamapp.data.entity.ProjectMemberEntity;

import java.util.List;
import java.util.UUID;

@Dao
public interface ProjectMemberDao {

    @Insert(onConflict = REPLACE)
    void upsert(ProjectMemberEntity member);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<ProjectMemberEntity> members);

    @Query("SELECT * FROM project_members WHERE projectId = :projectId")
    List<ProjectMemberEntity> listMembers(UUID projectId);

    @Query("DELETE FROM project_members WHERE projectId = :projectId")
    void clearProjectMembers(UUID projectId);

    @Query("DELETE FROM project_members")
    void clearAll();
}
