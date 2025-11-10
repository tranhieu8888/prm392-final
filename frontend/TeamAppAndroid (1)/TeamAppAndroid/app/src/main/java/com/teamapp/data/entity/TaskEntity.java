// com/teamapp/data/entity/TaskEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "tasks",
        indices = {@Index("projectId"), @Index("dueDate"), @Index("updatedAt")}
)
public class TaskEntity {
    @PrimaryKey @NonNull public UUID id;
    public UUID projectId;
    public String title;
    public String description;
    public String status;   // "TODO","DOING","REVIEW","DONE"
    public Double position; // dùng cho sắp xếp trong cột
    public Date dueDate;
    public Date updatedAt;
}
