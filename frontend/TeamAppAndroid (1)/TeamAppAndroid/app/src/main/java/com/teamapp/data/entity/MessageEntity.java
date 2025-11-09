// com/teamapp/data/entity/MessageEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "messages",
        indices = {@Index(value = {"conversationId","createdAt"})}
)
public class MessageEntity {
    @PrimaryKey @NonNull public UUID id;
    public UUID conversationId;
    public UUID senderId;
    public String senderName; // cache để render nhanh
    public String body;
    public Date createdAt;
}
