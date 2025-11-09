package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.teamapp.data.entity.ConversationMemberEntity;

import java.util.List;
import java.util.UUID;

@Dao
public interface ConversationMemberDao {

    @Insert(onConflict = REPLACE)
    void upsert(ConversationMemberEntity m);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<ConversationMemberEntity> ms);

    @Query("SELECT * FROM conversation_members WHERE conversationId = :cid")
    List<ConversationMemberEntity> byConversation(UUID cid);

    @Query("DELETE FROM conversation_members WHERE conversationId = :cid")
    void clearForConversation(UUID cid);

    @Query("DELETE FROM conversation_members")
    void clearAll();
}
