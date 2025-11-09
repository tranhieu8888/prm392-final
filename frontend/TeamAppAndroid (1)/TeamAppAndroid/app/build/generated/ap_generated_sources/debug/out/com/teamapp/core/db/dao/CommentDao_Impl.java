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
import com.teamapp.data.entity.CommentEntity;
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
public final class CommentDao_Impl implements CommentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CommentEntity> __insertionAdapterOfCommentEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearByTask;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public CommentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCommentEntity = new EntityInsertionAdapter<CommentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `comments` (`id`,`taskId`,`authorId`,`authorName`,`content`,`createdAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CommentEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        final String _tmp_1 = DateConverters.fromUuid(entity.taskId);
        if (_tmp_1 == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp_1);
        }
        final String _tmp_2 = DateConverters.fromUuid(entity.authorId);
        if (_tmp_2 == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp_2);
        }
        if (entity.authorName == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.authorName);
        }
        if (entity.content == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.content);
        }
        final Long _tmp_3 = DateConverters.fromDate(entity.createdAt);
        if (_tmp_3 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_3);
        }
      }
    };
    this.__preparedStmtOfClearByTask = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM comments WHERE taskId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM comments";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final CommentEntity c) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCommentEntity.insert(c);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<CommentEntity> cs) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCommentEntity.insert(cs);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearByTask(final UUID taskId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearByTask.acquire();
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
      __preparedStmtOfClearByTask.release(_stmt);
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
  public List<CommentEntity> byTask(final UUID taskId) {
    final String _sql = "SELECT * FROM comments WHERE taskId = ? ORDER BY createdAt ASC";
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
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "taskId");
      final int _cursorIndexOfAuthorId = CursorUtil.getColumnIndexOrThrow(_cursor, "authorId");
      final int _cursorIndexOfAuthorName = CursorUtil.getColumnIndexOrThrow(_cursor, "authorName");
      final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final List<CommentEntity> _result = new ArrayList<CommentEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final CommentEntity _item;
        _item = new CommentEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp_1);
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfTaskId)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfTaskId);
        }
        _item.taskId = DateConverters.toUuid(_tmp_2);
        final String _tmp_3;
        if (_cursor.isNull(_cursorIndexOfAuthorId)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getString(_cursorIndexOfAuthorId);
        }
        _item.authorId = DateConverters.toUuid(_tmp_3);
        if (_cursor.isNull(_cursorIndexOfAuthorName)) {
          _item.authorName = null;
        } else {
          _item.authorName = _cursor.getString(_cursorIndexOfAuthorName);
        }
        if (_cursor.isNull(_cursorIndexOfContent)) {
          _item.content = null;
        } else {
          _item.content = _cursor.getString(_cursorIndexOfContent);
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
