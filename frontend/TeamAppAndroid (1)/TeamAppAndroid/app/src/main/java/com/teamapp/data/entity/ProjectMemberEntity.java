// com/teamapp/data/entity/ProjectMemberEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

import java.util.UUID;

@Entity(
        tableName = "project_members",
        primaryKeys = {"projectId","userId"},
        indices = {@Index("projectId"), @Index("userId")}
)
public class ProjectMemberEntity {
    @NonNull public UUID projectId;
    @NonNull public UUID userId;
    public String role; // Owner/Member...
}
