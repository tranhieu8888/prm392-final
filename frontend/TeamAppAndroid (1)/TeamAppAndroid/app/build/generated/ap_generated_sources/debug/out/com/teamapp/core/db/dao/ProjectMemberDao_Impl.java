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
import com.teamapp.data.entity.ProjectMemberEntity;
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
public final class ProjectMemberDao_Impl implements ProjectMemberDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProjectMemberEntity> __insertionAdapterOfProjectMemberEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearProjectMembers;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public ProjectMemberDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProjectMemberEntity = new EntityInsertionAdapter<ProjectMemberEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `project_members` (`projectId`,`userId`,`role`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ProjectMemberEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.projectId);
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
        if (entity.role == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.role);
        }
      }
    };
    this.__preparedStmtOfClearProjectMembers = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM project_members WHERE projectId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM project_members";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final ProjectMemberEntity member) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfProjectMemberEntity.insert(member);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<ProjectMemberEntity> members) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfProjectMemberEntity.insert(members);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearProjectMembers(final UUID projectId) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearProjectMembers.acquire();
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
      __preparedStmtOfClearProjectMembers.release(_stmt);
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
  public List<ProjectMemberEntity> listMembers(final UUID projectId) {
    final String _sql = "SELECT * FROM project_members WHERE projectId = ?";
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
      final int _cursorIndexOfProjectId = CursorUtil.getColumnIndexOrThrow(_cursor, "projectId");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
      final List<ProjectMemberEntity> _result = new ArrayList<ProjectMemberEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ProjectMemberEntity _item;
        _item = new ProjectMemberEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfProjectId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfProjectId);
        }
        _item.projectId = DateConverters.toUuid(_tmp_1);
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfUserId)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfUserId);
        }
        _item.userId = DateConverters.toUuid(_tmp_2);
        if (_cursor.isNull(_cursorIndexOfRole)) {
          _item.role = null;
        } else {
          _item.role = _cursor.getString(_cursorIndexOfRole);
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
