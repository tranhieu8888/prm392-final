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
import com.teamapp.data.entity.ConversationMemberEntity;
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
public final class ConversationMemberDao_Impl implements ConversationMemberDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ConversationMemberEntity> __insertionAdapterOfConversationMemberEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearForConversation;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public ConversationMemberDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfConversationMemberEntity = new EntityInsertionAdapter<ConversationMemberEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `conversation_members` (`conversationId`,`userId`,`lastReadMessageId`,`lastReadAt`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ConversationMemberEntity entity) {
        final String _tmp = DateConverters.fromUuid(entity.conversationId);
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
        final String _tmp_2 = DateConverters.fromUuid(entity.lastReadMessageId);
        if (_tmp_2 == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp_2);
        }
        final Long _tmp_3 = DateConverters.fromDate(entity.lastReadAt);
        if (_tmp_3 == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp_3);
        }
      }
    };
    this.__preparedStmtOfClearForConversation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM conversation_members WHERE conversationId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM conversation_members";
        return _query;
      }
    };
  }

  @Override
  public void upsert(final ConversationMemberEntity m) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfConversationMemberEntity.insert(m);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void upsertAll(final List<ConversationMemberEntity> ms) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfConversationMemberEntity.insert(ms);
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
  public List<ConversationMemberEntity> byConversation(final UUID cid) {
    final String _sql = "SELECT * FROM conversation_members WHERE conversationId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = DateConverters.fromUuid(cid);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
      final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
      final int _cursorIndexOfLastReadMessageId = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReadMessageId");
      final int _cursorIndexOfLastReadAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReadAt");
      final List<ConversationMemberEntity> _result = new ArrayList<ConversationMemberEntity>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ConversationMemberEntity _item;
        _item = new ConversationMemberEntity();
        final String _tmp_1;
        if (_cursor.isNull(_cursorIndexOfConversationId)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getString(_cursorIndexOfConversationId);
        }
        _item.conversationId = DateConverters.toUuid(_tmp_1);
        final String _tmp_2;
        if (_cursor.isNull(_cursorIndexOfUserId)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getString(_cursorIndexOfUserId);
        }
        _item.userId = DateConverters.toUuid(_tmp_2);
        final String _tmp_3;
        if (_cursor.isNull(_cursorIndexOfLastReadMessageId)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getString(_cursorIndexOfLastReadMessageId);
        }
        _item.lastReadMessageId = DateConverters.toUuid(_tmp_3);
        final Long _tmp_4;
        if (_cursor.isNull(_cursorIndexOfLastReadAt)) {
          _tmp_4 = null;
        } else {
          _tmp_4 = _cursor.getLong(_cursorIndexOfLastReadAt);
        }
        _item.lastReadAt = DateConverters.toDate(_tmp_4);
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
