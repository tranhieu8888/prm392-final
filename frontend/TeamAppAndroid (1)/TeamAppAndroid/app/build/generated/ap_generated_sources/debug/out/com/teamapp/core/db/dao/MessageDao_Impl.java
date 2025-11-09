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
import com.teamapp.data.entity.MessageEntity;
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
public final class MessageDao_Impl implements MessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MessageEntity> __insertionAdapterOfMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearForConversation;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public MessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMessageEntity = new EntityInsertionAdapter<MessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `messages` (`id`,`conversationId`,`senderId`,`senderName`,`body`,`createdAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final MessageEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.id);
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        final String _tmp_1 = DateConverters.fromUuid(entity.conversationId);
        if (_tmp_1 == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, _tmp_1);
        }
        final String _tmp_2 = DateConverters.fromUuid(entity.senderId);
        if (_tmp_2 == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp_2);
        }
        if (entity.senderName == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.senderName);
        }
        if (entity.body == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.body);
        }
        final Long _tmp_3 = DateConverters.fromDate(entity.createdAt);
        if (_tmp_3 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_3);
        }
      }
    };
    this.__preparedStmtOfClearForConversation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM messages WHERE conversationId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM messages";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final MessageEntity m) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfMessageEntity.insert(m);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<MessageEntity> ms) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfMessageEntity.insert(ms);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearForConversation(final UUID cid) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearForConversation.acquire();
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(cid);
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
      __preparedStmtOfClearForConversation.release(_stmt);
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
  public List<MessageEntity> latest(final UUID cid, final int limit) {
    final String _sql = "SELECT * FROM messages\n"
            + "WHERE conversationId = ?\n"
            + "ORDER BY createdAt DESC\n"
            + "LIMIT ?\n";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(cid);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
      final int _cursorIndexOfSenderId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderId");
      final int _cursorIndexOfSenderName = CursorUtil.getColumnIndexOrThrow(_cursor, "senderName");
      final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final MessageEntity _item;
        _item = new MessageEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp_1);
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfConversationId)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfConversationId);
        }
        _item.conversationId = DateConverters.toUuid(_tmp_2);
        final String _tmp_3;
        if (_cursor.isNull(_cursorIndexOfSenderId)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getString(_cursorIndexOfSenderId);
        }
        _item.senderId = DateConverters.toUuid(_tmp_3);
        if (_cursor.isNull(_cursorIndexOfSenderName)) {
          _item.senderName = null;
        } else {
          _item.senderName = _cursor.getString(_cursorIndexOfSenderName);
        }
        if (_cursor.isNull(_cursorIndexOfBody)) {
          _item.body = null;
        } else {
          _item.body = _cursor.getString(_cursorIndexOfBody);
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

  @Override
  public List<MessageEntity> before(final UUID cid, final long before, final int limit) {
    final String _sql = "SELECT * FROM messages\n"
            + "WHERE conversationId = ?\n"
            + "  AND createdAt < ?\n"
            + "ORDER BY createdAt DESC\n"
            + "LIMIT ?\n";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(cid);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, before);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
      final int _cursorIndexOfSenderId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderId");
      final int _cursorIndexOfSenderName = CursorUtil.getColumnIndexOrThrow(_cursor, "senderName");
      final int _cursorIndexOfBody = CursorUtil.getColumnIndexOrThrow(_cursor, "body");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
      final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final MessageEntity _item;
        _item = new MessageEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfId);
        }
        _item.id = DateConverters.toUuid(_tmp_1);
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfConversationId)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfConversationId);
        }
        _item.conversationId = DateConverters.toUuid(_tmp_2);
        final String _tmp_3;
        if (_cursor.isNull(_cursorIndexOfSenderId)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getString(_cursorIndexOfSenderId);
        }
        _item.senderId = DateConverters.toUuid(_tmp_3);
        if (_cursor.isNull(_cursorIndexOfSenderName)) {
          _item.senderName = null;
        } else {
          _item.senderName = _cursor.getString(_cursorIndexOfSenderName);
        }
        if (_cursor.isNull(_cursorIndexOfBody)) {
          _item.body = null;
        } else {
          _item.body = _cursor.getString(_cursorIndexOfBody);
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
