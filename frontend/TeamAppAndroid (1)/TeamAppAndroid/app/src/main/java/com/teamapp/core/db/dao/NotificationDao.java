// app/src/main/java/com/teamapp/core/db/dao/NotificationDao.java
package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.teamapp.data.entity.NotificationEntity;

import java.util.List;
import java.util.UUID;

@Dao
public interface NotificationDao {

    @Insert(onConflict = REPLACE)
    void upsert(NotificationEntity n);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<NotificationEntity> ns);

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    List<NotificationEntity> recent();

    @Query("SELECT * FROM notifications WHERE id = :id LIMIT 1")
    NotificationEntity findById(UUID id);

    @Query("UPDATE notifications SET isRead = :isRead WHERE id = :id")
    void mark(UUID id, boolean isRead);

    @Query("UPDATE notifications SET isRead = 1")
    void markAllReadLocal();

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    int countUnread();

    @Query("DELETE FROM notifications")
    void clear();

    @Update
    void update(NotificationEntity n);
}
