// com/teamapp/data/entity/NotificationEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "notifications",
        indices = {@Index("isRead"), @Index("createdAt")}
)
public class NotificationEntity {
    @PrimaryKey @NonNull public UUID id;
    public String type;
    public String dataJson;
    public boolean isRead;
    public Date createdAt;
}
