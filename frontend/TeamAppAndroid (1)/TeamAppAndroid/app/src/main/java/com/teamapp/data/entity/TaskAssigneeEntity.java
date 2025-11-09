// com/teamapp/data/entity/TaskAssigneeEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;

import java.util.UUID;

@Entity(
        tableName = "task_assignees",
        primaryKeys = {"taskId","userId"},
        indices = {@Index("taskId"), @Index("userId")}
)
public class TaskAssigneeEntity {
    @NonNull public UUID taskId;
    @NonNull public UUID userId;
}
