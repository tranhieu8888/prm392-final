// com/teamapp/data/entity/ConversationMemberEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "conversation_members",
        primaryKeys = {"conversationId","userId"},
        indices = {@Index("conversationId"), @Index("userId")}
)
public class ConversationMemberEntity {
    @NonNull public UUID conversationId;
    @NonNull public UUID userId;
    public UUID lastReadMessageId;
    public Date lastReadAt;
}
