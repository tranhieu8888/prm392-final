package com.teamapp.core.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.teamapp.core.db.DateConverters;
import com.teamapp.data.entity.UserEntity;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserEntity> __insertionAdapterOfUserEntity;

  private final EntityDeletionOrUpdateAdapter<UserEntity> __updateAdapterOfUserEntity;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserEntity = new EntityInsertionAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `users` (`id`,`email`,`fullName`,`avatarUrl`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final UserEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        if (entity.email == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.email);
        }
        if (entity.fullName == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.fullName);
        }
        if (entity.avatarUrl == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.avatarUrl);
        }
      }
    };
    this.__updateAdapterOfUserEntity = new EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `users` SET `id` = ?,`email` = ?,`fullName` = ?,`avatarUrl` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final UserEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        if (entity.email == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.email);
        }
        if (entity.fullName == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.fullName);
        }
        if (entity.avatarUrl == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.avatarUrl);
        }
        final String _tmp_1 = DateConverters.fromUuid(entity.id);
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM users";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final UserEntity user) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfUserEntity.insert(user);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<UserEntity> users) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfUserEntity.insert(users);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final UserEntity user) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfUserEntity.handle(user);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clear() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClear.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClear.release(_stmt);
    }
  }

  @Override
  public UserEntity findById(final UUID id) {
    final String _sql = "SELECT * FROM users WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(id);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
      final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
      final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
      final UserEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new UserEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfId);
        }
        _result.id = DateConverters.toUuid(_tmp_1);
        if (_cursor.isNull(_cursorIndexOfEmail)) {
          _result.email = null;
        } else {
          _result.email = _cursor.getString(_cursorIndexOfEmail);
        }
        if (_cursor.isNull(_cursorIndexOfFullName)) {
          _result.fullName = null;
        } else {
          _result.fullName = _cursor.getString(_cursorIndexOfFullName);
        }
        if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
          _result.avatarUrl = null;
        } else {
          _result.avatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
        }
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<UserEntity> getAll() {
    final String _sql = "SELECT * FROM users ORDER BY fullName COLLATE NOCASE";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
      final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
      final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
      final List<UserEntity> _result = new ArrayList<UserEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final UserEntity _item;
        _item = new UserEntity();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        if (_cursor.isNull(_cursorIndexOfEmail)) {
          _item.email = null;
        } else {
          _item.email = _cursor.getString(_cursorIndexOfEmail);
        }
        if (_cursor.isNull(_cursorIndexOfFullName)) {
          _item.fullName = null;
        } else {
          _item.fullName = _cursor.getString(_cursorIndexOfFullName);
        }
        if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
          _item.avatarUrl = null;
        } else {
          _item.avatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
