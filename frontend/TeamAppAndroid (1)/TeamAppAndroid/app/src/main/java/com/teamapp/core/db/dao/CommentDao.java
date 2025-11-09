package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.teamapp.data.entity.CommentEntity;

import java.util.List;
import java.util.UUID;

@Dao
public interface CommentDao {

    @Insert(onConflict = REPLACE)
    void upsert(CommentEntity c);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<CommentEntity> cs);

    @Query("SELECT * FROM comments WHERE taskId = :taskId ORDER BY createdAt ASC")
    List<CommentEntity> byTask(UUID taskId);

    @Query("DELETE FROM comments WHERE taskId = :taskId")
    void clearByTask(UUID taskId);

    @Query("DELETE FROM comments")
    void clearAll();
}
