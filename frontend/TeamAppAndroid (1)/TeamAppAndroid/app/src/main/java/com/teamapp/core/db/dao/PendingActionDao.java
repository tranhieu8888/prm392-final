package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.teamapp.data.entity.PendingActionEntity;

import java.util.List;
import java.util.UUID;

@Dao
public interface PendingActionDao {

    @Insert(onConflict = REPLACE)
    void upsert(PendingActionEntity pa);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<PendingActionEntity> pas);

    @Query("SELECT * FROM pending_actions ORDER BY createdAt ASC")
    List<PendingActionEntity> queued();

    @Query("UPDATE pending_actions SET retryCount = retryCount + 1 WHERE id = :id")
    void bumpRetry(UUID id);

    @Query("DELETE FROM pending_actions WHERE id = :id")
    void delete(UUID id);

    @Query("DELETE FROM pending_actions")
    void clear();

    @Update
    void update(PendingActionEntity pa);
}
