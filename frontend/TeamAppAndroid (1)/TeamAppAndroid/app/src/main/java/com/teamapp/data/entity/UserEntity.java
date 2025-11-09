// com/teamapp/data/entity/UserEntity.java
package com.teamapp.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey @NonNull public UUID id;
    public String email;
    public String fullName;
    public String avatarUrl;
}
