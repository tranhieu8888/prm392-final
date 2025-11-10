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
import com.teamapp.data.entity.TaskEntity;
import java.lang.Class;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TaskDao_Impl implements TaskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TaskEntity> __insertionAdapterOfTaskEntity;

  private final EntityDeletionOrUpdateAdapter<TaskEntity> __updateAdapterOfTaskEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatusAndPosition;

  private final SharedSQLiteStatement __preparedStmtOfUpdateContent;

  private final SharedSQLiteStatement __preparedStmtOfClearByProject;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public TaskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTaskEntity = new EntityInsertionAdapter<TaskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tasks` (`id`,`projectId`,`title`,`description`,`status`,`position`,`dueDate`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final TaskEntity entity) {
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
        if (entity.title == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.title);
        }
        if (entity.description == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.description);
        }
        if (entity.status == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.status);
        }
        if (entity.position == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.position);
        }
        final Long _tmp_2 = DateConverters.fromDate(entity.dueDate);
        if (_tmp_2 == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp_2);
        }
        final Long _tmp_3 = DateConverters.fromDate(entity.updatedAt);
        if (_tmp_3 == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, _tmp_3);
        }
      }
    };
    this.__updateAdapterOfTaskEntity = new EntityDeletionOrUpdateAdapter<TaskEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `tasks` SET `id` = ?,`projectId` = ?,`title` = ?,`description` = ?,`status` = ?,`position` = ?,`dueDate` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final TaskEntity entity) {
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
        if (entity.title == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.title);
        }
        if (entity.description == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.description);
        }
        if (entity.status == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.status);
        }
        if (entity.position == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.position);
        }
        final Long _tmp_2 = DateConverters.fromDate(entity.dueDate);
        if (_tmp_2 == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp_2);
        }
        final Long _tmp_3 = DateConverters.fromDate(entity.updatedAt);
        if (_tmp_3 == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, _tmp_3);
        }
        final String _tmp_4 = DateConverters.fromUuid(entity.id);
        if (_tmp_4 == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, _tmp_4);
        }
      }
    };
    this.__preparedStmtOfUpdateStatusAndPosition = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tasks SET status = ?, position = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateContent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tasks SET title=?, description=?, dueDate=? WHERE id=?";
        return _query;
      }
    };
    this.__preparedStmtOfClearByProject = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tasks WHERE projectId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tasks";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final TaskEntity task) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTaskEntity.insert(task);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<TaskEntity> tasks) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfTaskEntity.insert(tasks);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final TaskEntity task) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfTaskEntity.handle(task);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateStatusAndPosition(final UUID taskId, final String status, final double position,
      final Date updatedAt) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatusAndPosition.acquire();
    int _argIndex = 1;
    if (status == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, status);
    }
    _argIndex = 2;
    _stmt.bindDouble(_argIndex, position);
    _argIndex = 3;
    final Long _tmp = DateConverters.fromDate(updatedAt);
    if (_tmp == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindLong(_argIndex, _tmp);
    }
    _argIndex = 4;
    final String _tmp_1 = DateConverters.fromUuid(taskId);
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
      __preparedStmtOfUpdateStatusAndPosition.release(_stmt);
    }
  }

  @Override
  public void updateContent(final UUID taskId, final String title, final String desc,
      final Date due) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateContent.acquire();
    int _argIndex = 1;
    if (title == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, title);
    }
    _argIndex = 2;
    if (desc == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, desc);
    }
    _argIndex = 3;
    final Long _tmp = DateConverters.fromDate(due);
    if (_tmp == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindLong(_argIndex, _tmp);
    }
    _argIndex = 4;
    final String _tmp_1 = DateConverters.fromUuid(taskId);
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
      __preparedStmtOfUpdateContent.release(_stmt);
    }
  }

  @Override
  public void clearByProject(final UUID projectId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearByProject.acquire();
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
      __preparedStmtOfClearByProject.release(_stmt);
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
  public List<TaskEntity> byDayRange(final long startMs, final long endMs) {
    final String _sql = "SELECT * FROM tasks WHERE dueDate >= ? AND dueDate < ? ORDER BY dueDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startMs);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endMs);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TaskEntity _item;
        _item = new TaskEntity();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfProjectId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfProjectId);
        }
        _item.projectId = DateConverters.toUuid(_tmp_1);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfPosition)) {
          _item.position = null;
        } else {
          _item.position = _cursor.getDouble(_cursorIndexOfPosition);
        }
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfDueDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfDueDate);
        }
        _item.dueDate = DateConverters.toDate(_tmp_2);
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _item.updatedAt = DateConverters.toDate(_tmp_3);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<TaskEntity> searchByText(final String like) {
    final String _sql = "SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?) ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (like == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, like);
    }
    _argIndex = 2;
    if (like == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, like);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TaskEntity _item;
        _item = new TaskEntity();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfProjectId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfProjectId);
        }
        _item.projectId = DateConverters.toUuid(_tmp_1);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfPosition)) {
          _item.position = null;
        } else {
          _item.position = _cursor.getDouble(_cursorIndexOfPosition);
        }
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfDueDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfDueDate);
        }
        _item.dueDate = DateConverters.toDate(_tmp_2);
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _item.updatedAt = DateConverters.toDate(_tmp_3);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public TaskEntity findById(final UUID id) {
    final String _sql = "SELECT * FROM tasks WHERE id = ? LIMIT 1";
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
      final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final TaskEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new TaskEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfId);
        }
        _result.id = DateConverters.toUuid(_tmp_1);
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfProjectId)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfProjectId);
        }
        _result.projectId = DateConverters.toUuid(_tmp_2);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _result.title = null;
        } else {
          _result.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _result.description = null;
        } else {
          _result.description = _cursor.getString(_cursorIndexOfDescription);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _result.status = null;
        } else {
          _result.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfPosition)) {
          _result.position = null;
        } else {
          _result.position = _cursor.getDouble(_cursorIndexOfPosition);
        }
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfDueDate)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfDueDate);
        }
        _result.dueDate = DateConverters.toDate(_tmp_3);
        final Long _tmp_4;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_4 = null;
        } else {
          _tmp_4 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _result.updatedAt = DateConverters.toDate(_tmp_4);
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
  public List<TaskEntity> byProject(final UUID projectId) {
    final String _sql = "SELECT * FROM tasks WHERE projectId = ? ORDER BY position ASC, updatedAt DESC";
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
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TaskEntity _item;
        _item = new TaskEntity();
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
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfPosition)) {
          _item.position = null;
        } else {
          _item.position = _cursor.getDouble(_cursorIndexOfPosition);
        }
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfDueDate)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfDueDate);
        }
        _item.dueDate = DateConverters.toDate(_tmp_3);
        final Long _tmp_4;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_4 = null;
        } else {
          _tmp_4 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _item.updatedAt = DateConverters.toDate(_tmp_4);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<TaskEntity> byStatus(final String status) {
    final String _sql = "SELECT * FROM tasks WHERE status = ? ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TaskEntity _item;
        _item = new TaskEntity();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfProjectId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfProjectId);
        }
        _item.projectId = DateConverters.toUuid(_tmp_1);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfPosition)) {
          _item.position = null;
        } else {
          _item.position = _cursor.getDouble(_cursorIndexOfPosition);
        }
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfDueDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfDueDate);
        }
        _item.dueDate = DateConverters.toDate(_tmp_2);
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _item.updatedAt = DateConverters.toDate(_tmp_3);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<TaskEntity> search(final String q) {
    final String _sql = "SELECT * FROM tasks WHERE title LIKE '%' || ? || '%' OR description LIKE '%' || ? || '%' ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (q == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, q);
    }
    _argIndex = 2;
    if (q == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, q);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TaskEntity _item;
        _item = new TaskEntity();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfProjectId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfProjectId);
        }
        _item.projectId = DateConverters.toUuid(_tmp_1);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfPosition)) {
          _item.position = null;
        } else {
          _item.position = _cursor.getDouble(_cursorIndexOfPosition);
        }
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfDueDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfDueDate);
        }
        _item.dueDate = DateConverters.toDate(_tmp_2);
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _item.updatedAt = DateConverters.toDate(_tmp_3);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<TaskEntity> getAll() {
    final String _sql = "SELECT * FROM tasks ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TaskEntity _item;
        _item = new TaskEntity();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfProjectId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfProjectId);
        }
        _item.projectId = DateConverters.toUuid(_tmp_1);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfPosition)) {
          _item.position = null;
        } else {
          _item.position = _cursor.getDouble(_cursorIndexOfPosition);
        }
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfDueDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfDueDate);
        }
        _item.dueDate = DateConverters.toDate(_tmp_2);
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _item.updatedAt = DateConverters.toDate(_tmp_3);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<TaskDao.TaskWithProjectName> searchWithProjectName(final String q,
      final String status) {
    final String _sql = "SELECT t.*, p.name AS projectName\n"
            + "FROM tasks t\n"
            + "JOIN projects p ON p.id = t.projectId\n"
            + "WHERE (? IS NULL OR t.status = ?)\n"
            + "  AND (t.title LIKE '%' || ? || '%' OR t.description LIKE '%' || ? || '%')\n"
            + "ORDER BY t.updatedAt DESC\n";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    _argIndex = 2;
    if (status == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, status);
    }
    _argIndex = 3;
    if (q == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, q);
    }
    _argIndex = 4;
    if (q == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, q);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final int _cursorIndexOfProjectName = CursorUtil.getColumnIndexOrThrow(_cursor, "projectName");
      final List<TaskDao.TaskWithProjectName> _result = new ArrayList<TaskDao.TaskWithProjectName>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TaskDao.TaskWithProjectName _item;
        _item = new TaskDao.TaskWithProjectName();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfProjectId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfProjectId);
        }
        _item.projectId = DateConverters.toUuid(_tmp_1);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfPosition)) {
          _item.position = null;
        } else {
          _item.position = _cursor.getDouble(_cursorIndexOfPosition);
        }
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfDueDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfDueDate);
        }
        _item.dueDate = DateConverters.toDate(_tmp_2);
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _item.updatedAt = DateConverters.toDate(_tmp_3);
        if (_cursor.isNull(_cursorIndexOfProjectName)) {
          _item.projectName = null;
        } else {
          _item.projectName = _cursor.getString(_cursorIndexOfProjectName);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<TaskEntity> byDayString(final String ymd) {
    final String _sql = "SELECT * FROM tasks WHERE strftime('%Y-%m-%d', dueDate/1000, 'unixepoch') = ? ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (ymd == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, ymd);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfPosition = CursorUtil.getColumnIndexOrThrow(_cursor, "position");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
      final List<TaskEntity> _result = new ArrayList<TaskEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TaskEntity _item;
        _item = new TaskEntity();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfProjectId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfProjectId);
        }
        _item.projectId = DateConverters.toUuid(_tmp_1);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfDescription)) {
          _item.description = null;
        } else {
          _item.description = _cursor.getString(_cursorIndexOfDescription);
        }
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        if (_cursor.isNull(_cursorIndexOfPosition)) {
          _item.position = null;
        } else {
          _item.position = _cursor.getDouble(_cursorIndexOfPosition);
        }
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfDueDate)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfDueDate);
        }
        _item.dueDate = DateConverters.toDate(_tmp_2);
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _item.updatedAt = DateConverters.toDate(_tmp_3);
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
