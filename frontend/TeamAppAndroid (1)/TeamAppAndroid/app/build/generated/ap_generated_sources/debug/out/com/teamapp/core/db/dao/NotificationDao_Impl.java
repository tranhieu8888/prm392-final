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
import com.teamapp.data.entity.NotificationEntity;
import java.lang.Class;
import java.lang.Long;
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
public final class NotificationDao_Impl implements NotificationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NotificationEntity> __insertionAdapterOfNotificationEntity;

  private final EntityDeletionOrUpdateAdapter<NotificationEntity> __updateAdapterOfNotificationEntity;

  private final SharedSQLiteStatement __preparedStmtOfMark;

  private final SharedSQLiteStatement __preparedStmtOfMarkAllReadLocal;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public NotificationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNotificationEntity = new EntityInsertionAdapter<NotificationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `notifications` (`id`,`type`,`dataJson`,`isRead`,`createdAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final NotificationEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        if (entity.type == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.type);
        }
        if (entity.dataJson == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.dataJson);
        }
        final int _tmp_1 = entity.isRead ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        final Long _tmp_2 = DateConverters.fromDate(entity.createdAt);
        if (_tmp_2 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_2);
        }
      }
    };
    this.__updateAdapterOfNotificationEntity = new EntityDeletionOrUpdateAdapter<NotificationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `notifications` SET `id` = ?,`type` = ?,`dataJson` = ?,`isRead` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final NotificationEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        if (entity.type == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.type);
        }
        if (entity.dataJson == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.dataJson);
        }
        final int _tmp_1 = entity.isRead ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        final Long _tmp_2 = DateConverters.fromDate(entity.createdAt);
        if (_tmp_2 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_2);
        }
        final String _tmp_3 = DateConverters.fromUuid(entity.id);
        if (_tmp_3 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_3);
        }
      }
    };
    this.__preparedStmtOfMark = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notifications SET isRead = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAllReadLocal = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notifications SET isRead = 1";
        return _query;
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM notifications";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final NotificationEntity n) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfNotificationEntity.insert(n);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<NotificationEntity> ns) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfNotificationEntity.insert(ns);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final NotificationEntity n) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfNotificationEntity.handle(n);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void mark(final UUID id, final boolean isRead) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfMark.acquire();
    int _argIndex = 1;
    final int _tmp = isRead ? 1 : 0;
    _stmt.bindLong(_argIndex, _tmp);
    _argIndex = 2;
    final String _tmp_1 = DateConverters.fromUuid(id);
    if (_tmp_1 == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, _tmp_1);
    }
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfMark.release(_stmt);
    }
  }

  @Override
  public void markAllReadLocal() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAllReadLocal.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfMarkAllReadLocal.release(_stmt);
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
  public List<NotificationEntity> recent() {
    final String _sql = "SELECT * FROM notifications ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "dataJson");
      final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final List<NotificationEntity> _result = new ArrayList<NotificationEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final NotificationEntity _item;
        _item = new NotificationEntity();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        if (_cursor.isNull(_cursorIndexOfType)) {
          _item.type = null;
        } else {
          _item.type = _cursor.getString(_cursorIndexOfType);
        }
        if (_cursor.isNull(_cursorIndexOfDataJson)) {
          _item.dataJson = null;
        } else {
          _item.dataJson = _cursor.getString(_cursorIndexOfDataJson);
        }
        final int _tmp_1;
        _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
        _item.isRead = _tmp_1 != 0;
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _item.createdAt = DateConverters.toDate(_tmp_2);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public NotificationEntity findById(final UUID id) {
    final String _sql = "SELECT * FROM notifications WHERE id = ? LIMIT 1";
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
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "dataJson");
      final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final NotificationEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new NotificationEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfId);
        }
        _result.id = DateConverters.toUuid(_tmp_1);
        if (_cursor.isNull(_cursorIndexOfType)) {
          _result.type = null;
        } else {
          _result.type = _cursor.getString(_cursorIndexOfType);
        }
        if (_cursor.isNull(_cursorIndexOfDataJson)) {
          _result.dataJson = null;
        } else {
          _result.dataJson = _cursor.getString(_cursorIndexOfDataJson);
        }
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfIsRead);
        _result.isRead = _tmp_2 != 0;
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _result.createdAt = DateConverters.toDate(_tmp_3);
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
  public int countUnread() {
    final String _sql = "SELECT COUNT(*) FROM notifications WHERE isRead = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
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
