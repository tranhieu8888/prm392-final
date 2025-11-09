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
import com.teamapp.data.entity.TaskAssigneeEntity;
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
public final class TaskAssigneeDao_Impl implements TaskAssigneeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TaskAssigneeEntity> __insertionAdapterOfTaskAssigneeEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearForTask;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public TaskAssigneeDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTaskAssigneeEntity = new EntityInsertionAdapter<TaskAssigneeEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `task_assignees` (`taskId`,`userId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final TaskAssigneeEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.taskId);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        final String _tmp_1 = DateConverters.fromUuid(entity.userId);
        if (_tmp_1 == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp_1);
        }
      }
    };
    this.__preparedStmtOfClearForTask = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM task_assignees WHERE taskId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM task_assignees";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final TaskAssigneeEntity entity) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTaskAssigneeEntity.insert(entity);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<TaskAssigneeEntity> entities) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTaskAssigneeEntity.insert(entities);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearForTask(final UUID taskId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearForTask.acquire();
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(taskId);
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
      __preparedStmtOfClearForTask.release(_stmt);
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
  public List<TaskAssigneeEntity> byTask(final UUID taskId) {
    final String _sql = "SELECT * FROM task_assignees WHERE taskId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(taskId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final List<TaskAssigneeEntity> _result = new ArrayList<TaskAssigneeEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TaskAssigneeEntity _item;
        _item = new TaskAssigneeEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfTaskId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfTaskId);
        }
        _item.taskId = DateConverters.toUuid(_tmp_1);
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfUserId)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfUserId);
        }
        _item.userId = DateConverters.toUuid(_tmp_2);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<TaskAssigneeEntity> byUser(final UUID userId) {
    final String _sql = "SELECT * FROM task_assignees WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(userId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final List<TaskAssigneeEntity> _result = new ArrayList<TaskAssigneeEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TaskAssigneeEntity _item;
        _item = new TaskAssigneeEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfTaskId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfTaskId);
        }
        _item.taskId = DateConverters.toUuid(_tmp_1);
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfUserId)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfUserId);
        }
        _item.userId = DateConverters.toUuid(_tmp_2);
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
