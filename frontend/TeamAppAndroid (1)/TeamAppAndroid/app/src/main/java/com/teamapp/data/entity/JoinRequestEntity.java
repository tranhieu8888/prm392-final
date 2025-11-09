// com/teamapp/data/entity/JoinRequestEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "join_requests",
        indices = {@Index("projectId"), @Index("requesterId")}
)
public class JoinRequestEntity {
    @PrimaryKey @NonNull public UUID id;
    public UUID projectId;
    public UUID requesterId;
    public String status; // "Pending","Approved","Rejected"
    public Date createdAt;
}
