package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.teamapp.data.entity.ConversationEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Dao
public interface ConversationDao {

    @Insert(onConflict = REPLACE)
    void upsert(ConversationEntity c);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<ConversationEntity> cs);

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    ConversationEntity findById(UUID id);

    @Query("""
    SELECT * FROM conversations
    ORDER BY (lastMessageAt IS NULL), lastMessageAt DESC
""")
    List<ConversationEntity> recent();


    @Query("UPDATE conversations SET lastMessageAt = :at WHERE id = :id")
    void updateLastMessageAt(UUID id, Date at);

    @Query("DELETE FROM conversations")
    void clear();

    @Update
    void update(ConversationEntity c);
}
