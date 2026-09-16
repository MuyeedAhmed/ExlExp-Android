package com.muyeedahmed.exlexp.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.muyeedahmed.exlexp.data.local.entity.CreditCardEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CardDao_Impl implements CardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CreditCardEntity> __insertionAdapterOfCreditCardEntity;

  private final EntityDeletionOrUpdateAdapter<CreditCardEntity> __updateAdapterOfCreditCardEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteCard;

  public CardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCreditCardEntity = new EntityInsertionAdapter<CreditCardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cards` (`id`,`name`,`isChecking`,`isSaving`,`isBrokerage`,`isHidden`,`priority`,`openDate`,`username`,`isSyncDirty`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCardEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        final int _tmp = entity.isChecking() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final int _tmp_1 = entity.isSaving() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        final int _tmp_2 = entity.isBrokerage() ? 1 : 0;
        statement.bindLong(5, _tmp_2);
        final int _tmp_3 = entity.isHidden() ? 1 : 0;
        statement.bindLong(6, _tmp_3);
        statement.bindLong(7, entity.getPriority());
        statement.bindString(8, entity.getOpenDate());
        statement.bindString(9, entity.getUsername());
        final int _tmp_4 = entity.isSyncDirty() ? 1 : 0;
        statement.bindLong(10, _tmp_4);
        statement.bindLong(11, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfCreditCardEntity = new EntityDeletionOrUpdateAdapter<CreditCardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `cards` SET `id` = ?,`name` = ?,`isChecking` = ?,`isSaving` = ?,`isBrokerage` = ?,`isHidden` = ?,`priority` = ?,`openDate` = ?,`username` = ?,`isSyncDirty` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CreditCardEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        final int _tmp = entity.isChecking() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final int _tmp_1 = entity.isSaving() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        final int _tmp_2 = entity.isBrokerage() ? 1 : 0;
        statement.bindLong(5, _tmp_2);
        final int _tmp_3 = entity.isHidden() ? 1 : 0;
        statement.bindLong(6, _tmp_3);
        statement.bindLong(7, entity.getPriority());
        statement.bindString(8, entity.getOpenDate());
        statement.bindString(9, entity.getUsername());
        final int _tmp_4 = entity.isSyncDirty() ? 1 : 0;
        statement.bindLong(10, _tmp_4);
        statement.bindLong(11, entity.getUpdatedAt());
        statement.bindString(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteCard = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM cards WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertCard(final CreditCardEntity card,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCreditCardEntity.insert(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCards(final List<CreditCardEntity> cards,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCreditCardEntity.insert(cards);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCard(final CreditCardEntity card,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCreditCardEntity.handle(card);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCard(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteCard.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteCard.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CreditCardEntity>> getAllCardsFlow(final String username) {
    final String _sql = "SELECT * FROM cards WHERE username = ? ORDER BY priority ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, username);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cards"}, new Callable<List<CreditCardEntity>>() {
      @Override
      @NonNull
      public List<CreditCardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsChecking = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecking");
          final int _cursorIndexOfIsSaving = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaving");
          final int _cursorIndexOfIsBrokerage = CursorUtil.getColumnIndexOrThrow(_cursor, "isBrokerage");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfOpenDate = CursorUtil.getColumnIndexOrThrow(_cursor, "openDate");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<CreditCardEntity> _result = new ArrayList<CreditCardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CreditCardEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final boolean _tmpIsChecking;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsChecking);
            _tmpIsChecking = _tmp != 0;
            final boolean _tmpIsSaving;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsSaving);
            _tmpIsSaving = _tmp_1 != 0;
            final boolean _tmpIsBrokerage;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBrokerage);
            _tmpIsBrokerage = _tmp_2 != 0;
            final boolean _tmpIsHidden;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_3 != 0;
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpOpenDate;
            _tmpOpenDate = _cursor.getString(_cursorIndexOfOpenDate);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new CreditCardEntity(_tmpId,_tmpName,_tmpIsChecking,_tmpIsSaving,_tmpIsBrokerage,_tmpIsHidden,_tmpPriority,_tmpOpenDate,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllCards(final String username,
      final Continuation<? super List<CreditCardEntity>> $completion) {
    final String _sql = "SELECT * FROM cards WHERE username = ? ORDER BY priority ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, username);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CreditCardEntity>>() {
      @Override
      @NonNull
      public List<CreditCardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsChecking = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecking");
          final int _cursorIndexOfIsSaving = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaving");
          final int _cursorIndexOfIsBrokerage = CursorUtil.getColumnIndexOrThrow(_cursor, "isBrokerage");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfOpenDate = CursorUtil.getColumnIndexOrThrow(_cursor, "openDate");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<CreditCardEntity> _result = new ArrayList<CreditCardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CreditCardEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final boolean _tmpIsChecking;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsChecking);
            _tmpIsChecking = _tmp != 0;
            final boolean _tmpIsSaving;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsSaving);
            _tmpIsSaving = _tmp_1 != 0;
            final boolean _tmpIsBrokerage;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBrokerage);
            _tmpIsBrokerage = _tmp_2 != 0;
            final boolean _tmpIsHidden;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_3 != 0;
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpOpenDate;
            _tmpOpenDate = _cursor.getString(_cursorIndexOfOpenDate);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new CreditCardEntity(_tmpId,_tmpName,_tmpIsChecking,_tmpIsSaving,_tmpIsBrokerage,_tmpIsHidden,_tmpPriority,_tmpOpenDate,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CreditCardEntity>> getNonHiddenCardsFlow(final String username) {
    final String _sql = "SELECT * FROM cards WHERE username = ? AND isHidden = 0 ORDER BY priority ASC, name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, username);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cards"}, new Callable<List<CreditCardEntity>>() {
      @Override
      @NonNull
      public List<CreditCardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsChecking = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecking");
          final int _cursorIndexOfIsSaving = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaving");
          final int _cursorIndexOfIsBrokerage = CursorUtil.getColumnIndexOrThrow(_cursor, "isBrokerage");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfOpenDate = CursorUtil.getColumnIndexOrThrow(_cursor, "openDate");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<CreditCardEntity> _result = new ArrayList<CreditCardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CreditCardEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final boolean _tmpIsChecking;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsChecking);
            _tmpIsChecking = _tmp != 0;
            final boolean _tmpIsSaving;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsSaving);
            _tmpIsSaving = _tmp_1 != 0;
            final boolean _tmpIsBrokerage;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBrokerage);
            _tmpIsBrokerage = _tmp_2 != 0;
            final boolean _tmpIsHidden;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_3 != 0;
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpOpenDate;
            _tmpOpenDate = _cursor.getString(_cursorIndexOfOpenDate);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new CreditCardEntity(_tmpId,_tmpName,_tmpIsChecking,_tmpIsSaving,_tmpIsBrokerage,_tmpIsHidden,_tmpPriority,_tmpOpenDate,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getCardById(final String id,
      final Continuation<? super CreditCardEntity> $completion) {
    final String _sql = "SELECT * FROM cards WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CreditCardEntity>() {
      @Override
      @Nullable
      public CreditCardEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsChecking = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecking");
          final int _cursorIndexOfIsSaving = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaving");
          final int _cursorIndexOfIsBrokerage = CursorUtil.getColumnIndexOrThrow(_cursor, "isBrokerage");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfOpenDate = CursorUtil.getColumnIndexOrThrow(_cursor, "openDate");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final CreditCardEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final boolean _tmpIsChecking;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsChecking);
            _tmpIsChecking = _tmp != 0;
            final boolean _tmpIsSaving;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsSaving);
            _tmpIsSaving = _tmp_1 != 0;
            final boolean _tmpIsBrokerage;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBrokerage);
            _tmpIsBrokerage = _tmp_2 != 0;
            final boolean _tmpIsHidden;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_3 != 0;
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpOpenDate;
            _tmpOpenDate = _cursor.getString(_cursorIndexOfOpenDate);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new CreditCardEntity(_tmpId,_tmpName,_tmpIsChecking,_tmpIsSaving,_tmpIsBrokerage,_tmpIsHidden,_tmpPriority,_tmpOpenDate,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCardCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM cards";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDirtyCards(final Continuation<? super List<CreditCardEntity>> $completion) {
    final String _sql = "SELECT * FROM cards WHERE isSyncDirty = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CreditCardEntity>>() {
      @Override
      @NonNull
      public List<CreditCardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsChecking = CursorUtil.getColumnIndexOrThrow(_cursor, "isChecking");
          final int _cursorIndexOfIsSaving = CursorUtil.getColumnIndexOrThrow(_cursor, "isSaving");
          final int _cursorIndexOfIsBrokerage = CursorUtil.getColumnIndexOrThrow(_cursor, "isBrokerage");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "isHidden");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfOpenDate = CursorUtil.getColumnIndexOrThrow(_cursor, "openDate");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<CreditCardEntity> _result = new ArrayList<CreditCardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CreditCardEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final boolean _tmpIsChecking;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsChecking);
            _tmpIsChecking = _tmp != 0;
            final boolean _tmpIsSaving;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsSaving);
            _tmpIsSaving = _tmp_1 != 0;
            final boolean _tmpIsBrokerage;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBrokerage);
            _tmpIsBrokerage = _tmp_2 != 0;
            final boolean _tmpIsHidden;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_3 != 0;
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final String _tmpOpenDate;
            _tmpOpenDate = _cursor.getString(_cursorIndexOfOpenDate);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new CreditCardEntity(_tmpId,_tmpName,_tmpIsChecking,_tmpIsSaving,_tmpIsBrokerage,_tmpIsHidden,_tmpPriority,_tmpOpenDate,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object markClean(final List<String> ids, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE cards SET isSyncDirty = 0 WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (String _item : ids) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
