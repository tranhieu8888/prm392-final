// com/teamapp/data/entity/CommentEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "comments",
        indices = {@Index("taskId"), @Index("createdAt")}
)
public class CommentEntity {
    @PrimaryKey @NonNull public UUID id;
    public UUID taskId;
    public UUID authorId;
    public String authorName;
    public String content;
    public Date createdAt;
}
