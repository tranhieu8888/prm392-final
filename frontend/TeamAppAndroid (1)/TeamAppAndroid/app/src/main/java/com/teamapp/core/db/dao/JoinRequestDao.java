package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.teamapp.data.entity.JoinRequestEntity;

import java.util.List;
import java.util.UUID;

@Dao
public interface JoinRequestDao {

    @Insert(onConflict = REPLACE)
    void upsert(JoinRequestEntity jr);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<JoinRequestEntity> jrs);

    @Query("SELECT * FROM join_requests WHERE projectId = :projectId ORDER BY createdAt DESC")
    List<JoinRequestEntity> byProject(UUID projectId);

    @Query("DELETE FROM join_requests WHERE projectId = :projectId")
    void clearForProject(UUID projectId);

    @Query("DELETE FROM join_requests")
    void clearAll();
}
