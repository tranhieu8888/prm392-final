package com.teamapp.core.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.teamapp.core.db.DateConverters;
import com.teamapp.data.entity.JoinRequestEntity;
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
public final class JoinRequestDao_Impl implements JoinRequestDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<JoinRequestEntity> __insertionAdapterOfJoinRequestEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearForProject;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public JoinRequestDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfJoinRequestEntity = new EntityInsertionAdapter<JoinRequestEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `join_requests` (`id`,`projectId`,`requesterId`,`status`,`createdAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final JoinRequestEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        final String _tmp_1 = DateConverters.fromUuid(entity.projectId);
        if (_tmp_1 == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp_1);
        }
        final String _tmp_2 = DateConverters.fromUuid(entity.requesterId);
        if (_tmp_2 == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp_2);
        }
        if (entity.status == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.status);
        }
        final Long _tmp_3 = DateConverters.fromDate(entity.createdAt);
        if (_tmp_3 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_3);
        }
      }
    };
    this.__preparedStmtOfClearForProject = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM join_requests WHERE projectId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM join_requests";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final JoinRequestEntity jr) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfJoinRequestEntity.insert(jr);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<JoinRequestEntity> jrs) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfJoinRequestEntity.insert(jrs);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearForProject(final UUID projectId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearForProject.acquire();
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(projectId);
    if (_tmp == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, _tmp);
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
      __preparedStmtOfClearForProject.release(_stmt);
    }
  }

  @Override
  public void clearAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClearAll.release(_stmt);
    }
  }

  @Override
  public List<JoinRequestEntity> byProject(final UUID projectId) {
    final String _sql = "SELECT * FROM join_requests WHERE projectId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(projectId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
      final int _cursorIndexOfRequesterId = CursorUtil.getColumnIndexOrThrow(_cursor, "requesterId");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final List<JoinRequestEntity> _result = new ArrayList<JoinRequestEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final JoinRequestEntity _item;
        _item = new JoinRequestEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp_1);
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfProjectId)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfProjectId);
        }
        _item.projectId = DateConverters.toUuid(_tmp_2);
        final String _tmp_3;
        if (_cursor.isNull(_cursorIndexOfRequesterId)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getString(_cursorIndexOfRequesterId);
        }
        _item.requesterId = DateConverters.toUuid(_tmp_3);
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        final Long _tmp_4;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp_4 = null;
        } else {
          _tmp_4 = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _item.createdAt = DateConverters.toDate(_tmp_4);
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
