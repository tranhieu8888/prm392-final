// com/teamapp/data/entity/PendingActionEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "pending_actions")
public class PendingActionEntity {
    @PrimaryKey @NonNull public UUID id;
    public String actionType;   // "ADD_COMMENT","UPDATE_TASK",...
    public String payloadJson;  // dữ liệu chờ sync
    public int retryCount;
    public Date createdAt;
}
