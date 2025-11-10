// com/teamapp/data/entity/ProjectEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "projects",
        indices = {@Index("createdAt")}
)
public class ProjectEntity {
    @PrimaryKey @NonNull public UUID id;
    public String name;
    public String description;
    public boolean isPublic;
    public String status;   // "TODO","DOING","REVIEW","DONE"
    public Date createdAt;
}
