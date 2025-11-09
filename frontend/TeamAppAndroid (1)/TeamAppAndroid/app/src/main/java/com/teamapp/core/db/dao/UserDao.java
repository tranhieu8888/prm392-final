package com.teamapp.core.db.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.teamapp.data.entity.UserEntity;

import java.util.List;
import java.util.UUID;

@Dao
public interface UserDao {

    @Insert(onConflict = REPLACE)
    void upsert(UserEntity user);

    @Insert(onConflict = REPLACE)
    void upsertAll(List<UserEntity> users);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity findById(UUID id);

    @Query("SELECT * FROM users ORDER BY fullName COLLATE NOCASE")
    List<UserEntity> getAll();

    @Query("DELETE FROM users")
    void clear();

    @Update
    void update(UserEntity user);
}
