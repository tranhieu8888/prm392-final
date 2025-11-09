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
import com.teamapp.data.entity.ConversationEntity;
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
public final class ConversationDao_Impl implements ConversationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ConversationEntity> __insertionAdapterOfConversationEntity;

  private final EntityDeletionOrUpdateAdapter<ConversationEntity> __updateAdapterOfConversationEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastMessageAt;

  private final SharedSQLiteStatement __preparedStmtOfClear;

  public ConversationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfConversationEntity = new EntityInsertionAdapter<ConversationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `conversations` (`id`,`title`,`type`,`lastMessageAt`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ConversationEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        if (entity.title == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.title);
        }
        if (entity.type == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.type);
        }
        final Long _tmp_1 = DateConverters.fromDate(entity.lastMessageAt);
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp_1);
        }
      }
    };
    this.__updateAdapterOfConversationEntity = new EntityDeletionOrUpdateAdapter<ConversationEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `conversations` SET `id` = ?,`title` = ?,`type` = ?,`lastMessageAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ConversationEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        if (entity.title == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.title);
        }
        if (entity.type == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.type);
        }
        final Long _tmp_1 = DateConverters.fromDate(entity.lastMessageAt);
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp_1);
        }
        final String _tmp_2 = DateConverters.fromUuid(entity.id);
        if (_tmp_2 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_2);
        }
      }
    };
    this.__preparedStmtOfUpdateLastMessageAt = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE conversations SET lastMessageAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClear = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM conversations";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final ConversationEntity c) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfConversationEntity.insert(c);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<ConversationEntity> cs) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfConversationEntity.insert(cs);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final ConversationEntity c) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfConversationEntity.handle(c);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateLastMessageAt(final UUID id, final Date at) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastMessageAt.acquire();
    int _argIndex = 1;
    final Long _tmp = DateConverters.fromDate(at);
    if (_tmp == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindLong(_argIndex, _tmp);
    }
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
      __preparedStmtOfUpdateLastMessageAt.release(_stmt);
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
  public ConversationEntity findById(final UUID id) {
    final String _sql = "SELECT * FROM conversations WHERE id = ? LIMIT 1";
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
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfLastMessageAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessageAt");
      final ConversationEntity _result;
      if (_cursor.moveToFirst()) {
        _result = new ConversationEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfId);
        }
        _result.id = DateConverters.toUuid(_tmp_1);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _result.title = null;
        } else {
          _result.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfType)) {
          _result.type = null;
        } else {
          _result.type = _cursor.getString(_cursorIndexOfType);
        }
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfLastMessageAt)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfLastMessageAt);
        }
        _result.lastMessageAt = DateConverters.toDate(_tmp_2);
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
  public List<ConversationEntity> recent() {
    final String _sql = "    SELECT * FROM conversations\n"
            + "    ORDER BY (lastMessageAt IS NULL), lastMessageAt DESC\n";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfLastMessageAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessageAt");
      final List<ConversationEntity> _result = new ArrayList<ConversationEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ConversationEntity _item;
        _item = new ConversationEntity();
        final String _tmp;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp);
        if (_cursor.isNull(_cursorIndexOfTitle)) {
          _item.title = null;
        } else {
          _item.title = _cursor.getString(_cursorIndexOfTitle);
        }
        if (_cursor.isNull(_cursorIndexOfType)) {
          _item.type = null;
        } else {
          _item.type = _cursor.getString(_cursorIndexOfType);
        }
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfLastMessageAt)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfLastMessageAt);
        }
        _item.lastMessageAt = DateConverters.toDate(_tmp_1);
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
