// com/teamapp/data/entity/ConversationEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "conversations",
        indices = {@Index("lastMessageAt")}
)
public class ConversationEntity {
    @PrimaryKey @NonNull public UUID id;
    public String title;
    public String type; // "dm" | "group"
    public Date lastMessageAt;
}
