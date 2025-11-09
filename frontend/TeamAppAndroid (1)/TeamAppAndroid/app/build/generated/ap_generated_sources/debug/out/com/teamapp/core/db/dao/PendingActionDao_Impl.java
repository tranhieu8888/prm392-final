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
import com.teamapp.data.entity.PendingActionEntity;
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
public final class PendingActionDao_Impl implements PendingActionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PendingActionEntity> __insertionAdapterOfPendingActionEntity;

  private final EntityDeletionOrUpdateAdapter<PendingActionEntity> __updateAdapterOfPendingActionEntity;

  private final SharedSQLiteStatement __preparedStmtOfBumpRetry;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public PendingActionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPendingActionEntity = new EntityInsertionAdapter<PendingActionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pending_actions` (`id`,`actionType`,`payloadJson`,`retryCount`,`createdAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final PendingActionEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        if (entity.actionType == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.actionType);
        }
        if (entity.payloadJson == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.payloadJson);
        }
        statement.bindLong(4, entity.retryCount);
        final Long _tmp_1 = DateConverters.fromDate(entity.createdAt);
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_1);
        }
      }
    };
    this.__updateAdapterOfPendingActionEntity = new EntityDeletionOrUpdateAdapter<PendingActionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `pending_actions` SET `id` = ?,`actionType` = ?,`payloadJson` = ?,`retryCount` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final PendingActionEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        if (entity.actionType == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.actionType);
        }
        if (entity.payloadJson == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.payloadJson);
        }
        statement.bindLong(4, entity.retryCount);
        final Long _tmp_1 = DateConverters.fromDate(entity.createdAt);
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp_1);
        }
        final String _tmp_2 = DateConverters.fromUuid(entity.id);
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_2);
        }
      }
    };
    this.__preparedStmtOfBumpRetry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE pending_actions SET retryCount = retryCount + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pending_actions WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pending_actions";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final PendingActionEntity pa) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPendingActionEntity.insert(pa);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<PendingActionEntity> pas) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPendingActionEntity.insert(pas);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final PendingActionEntity pa) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfPendingActionEntity.handle(pa);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void bumpRetry(final UUID id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfBumpRetry.acquire();
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(id);
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
      __preparedStmtOfBumpRetry.release(_stmt);
    }
  }

  @Override
  public void delete(final UUID id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(id);
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
      __preparedStmtOfDelete.release(_stmt);
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
  public List<PendingActionEntity> queued() {
    final String _sql = "SELECT * FROM pending_actions ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfActionType = CursorUtil.getColumnIndexOrThrow(_cursor, "actionType");
      final int _cursorIndexOfPayloadJson = CursorUtil.getColumnIndexOrThrow(_cursor, "payloadJson");
      final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final List<PendingActionEntity> _result = new ArrayList<PendingActionEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final PendingActionEntity _item;
        _item = new PendingActionEntity();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        if (_cursor.isNull(_cursorIndexOfActionType)) {
          _item.actionType = null;
        } else {
          _item.actionType = _cursor.getString(_cursorIndexOfActionType);
        }
        if (_cursor.isNull(_cursorIndexOfPayloadJson)) {
          _item.payloadJson = null;
        } else {
          _item.payloadJson = _cursor.getString(_cursorIndexOfPayloadJson);
        }
        _item.retryCount = _cursor.getInt(_cursorIndexOfRetryCount);
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _item.createdAt = DateConverters.toDate(_tmp_1);
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
