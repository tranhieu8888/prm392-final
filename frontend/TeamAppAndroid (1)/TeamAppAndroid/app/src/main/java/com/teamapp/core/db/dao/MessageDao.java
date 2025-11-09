package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.teamapp.data.entity.MessageEntity;

import java.util.List;
import java.util.UUID;

@Dao
public interface MessageDao {

    @Insert(onConflict = REPLACE)
    void upsert(MessageEntity m);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<MessageEntity> ms);

    @Query("""
           SELECT * FROM messages
           WHERE conversationId = :cid
           ORDER BY createdAt DESC
           LIMIT :limit
           """)
    List<MessageEntity> latest(UUID cid, int limit);

    @Query("""
           SELECT * FROM messages
           WHERE conversationId = :cid
             AND createdAt < :before
           ORDER BY createdAt DESC
           LIMIT :limit
           """)
    List<MessageEntity> before(UUID cid, long before, int limit);

    @Query("DELETE FROM messages WHERE conversationId = :cid")
    void clearForConversation(UUID cid);

    @Query("DELETE FROM messages")
    void clearAll();
}
