package com.muyeedahmed.exlexp.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
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
import com.muyeedahmed.exlexp.data.local.entity.FutureExpenseEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class FutureExpenseDao_Impl implements FutureExpenseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FutureExpenseEntity> __insertionAdapterOfFutureExpenseEntity;

  private final EntityDeletionOrUpdateAdapter<FutureExpenseEntity> __updateAdapterOfFutureExpenseEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteFutureExpense;

  public FutureExpenseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFutureExpenseEntity = new EntityInsertionAdapter<FutureExpenseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `future_expenses` (`id`,`description`,`amount`,`dueDate`,`username`,`isSyncDirty`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FutureExpenseEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDescription());
        statement.bindDouble(3, entity.getAmount());
        if (entity.getDueDate() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDueDate());
        }
        statement.bindString(5, entity.getUsername());
        final int _tmp = entity.isSyncDirty() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__updateAdapterOfFutureExpenseEntity = new EntityDeletionOrUpdateAdapter<FutureExpenseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `future_expenses` SET `id` = ?,`description` = ?,`amount` = ?,`dueDate` = ?,`username` = ?,`isSyncDirty` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FutureExpenseEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDescription());
        statement.bindDouble(3, entity.getAmount());
        if (entity.getDueDate() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDueDate());
        }
        statement.bindString(5, entity.getUsername());
        final int _tmp = entity.isSyncDirty() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindString(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteFutureExpense = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM future_expenses WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertFutureExpense(final FutureExpenseEntity bill,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFutureExpenseEntity.insert(bill);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertFutureExpenses(final List<FutureExpenseEntity> bills,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFutureExpenseEntity.insert(bills);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateFutureExpense(final FutureExpenseEntity bill,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFutureExpenseEntity.handle(bill);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFutureExpense(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteFutureExpense.acquire();
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
          __preparedStmtOfDeleteFutureExpense.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FutureExpenseEntity>> getAllFutureExpensesFlow(final String username) {
    final String _sql = "SELECT * FROM future_expenses WHERE username = ? ORDER BY dueDate ASC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, username);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"future_expenses"}, new Callable<List<FutureExpenseEntity>>() {
      @Override
      @NonNull
      public List<FutureExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final List<FutureExpenseEntity> _result = new ArrayList<FutureExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDueDate;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmpDueDate = null;
            } else {
              _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            }
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp != 0;
            _item = new FutureExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpDueDate,_tmpUsername,_tmpIsSyncDirty);
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
  public Object getAllFutureExpenses(final String username,
      final Continuation<? super List<FutureExpenseEntity>> $completion) {
    final String _sql = "SELECT * FROM future_expenses WHERE username = ? ORDER BY dueDate ASC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, username);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FutureExpenseEntity>>() {
      @Override
      @NonNull
      public List<FutureExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final List<FutureExpenseEntity> _result = new ArrayList<FutureExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDueDate;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmpDueDate = null;
            } else {
              _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            }
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp != 0;
            _item = new FutureExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpDueDate,_tmpUsername,_tmpIsSyncDirty);
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
  public Object getDirtyFutureExpenses(
      final Continuation<? super List<FutureExpenseEntity>> $completion) {
    final String _sql = "SELECT * FROM future_expenses WHERE isSyncDirty = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FutureExpenseEntity>>() {
      @Override
      @NonNull
      public List<FutureExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "dueDate");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final List<FutureExpenseEntity> _result = new ArrayList<FutureExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDueDate;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmpDueDate = null;
            } else {
              _tmpDueDate = _cursor.getString(_cursorIndexOfDueDate);
            }
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp != 0;
            _item = new FutureExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpDueDate,_tmpUsername,_tmpIsSyncDirty);
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
        _stringBuilder.append("UPDATE future_expenses SET isSyncDirty = 0 WHERE id IN (");
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
