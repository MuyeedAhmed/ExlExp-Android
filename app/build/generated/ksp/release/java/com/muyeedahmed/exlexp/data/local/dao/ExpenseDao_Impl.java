package com.muyeedahmed.exlexp.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingSource;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.paging.LimitOffsetPagingSource;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.muyeedahmed.exlexp.data.local.entity.ExpenseEntity;
import java.lang.Class;
import java.lang.Double;
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
public final class ExpenseDao_Impl implements ExpenseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExpenseEntity> __insertionAdapterOfExpenseEntity;

  private final EntityDeletionOrUpdateAdapter<ExpenseEntity> __updateAdapterOfExpenseEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteExpenseById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByTransferLinkId;

  public ExpenseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExpenseEntity = new EntityInsertionAdapter<ExpenseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `expenses` (`id`,`description`,`amount`,`creditCardId`,`date`,`fromTo`,`details`,`isFee`,`isReward`,`rewardType`,`rewardValue`,`isTransfer`,`transferLinkId`,`isInterest`,`category`,`username`,`isSyncDirty`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDescription());
        statement.bindDouble(3, entity.getAmount());
        statement.bindString(4, entity.getCreditCardId());
        statement.bindString(5, entity.getDate());
        if (entity.getFromTo() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFromTo());
        }
        if (entity.getDetails() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDetails());
        }
        final int _tmp = entity.isFee() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isReward() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        if (entity.getRewardType() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getRewardType());
        }
        if (entity.getRewardValue() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getRewardValue());
        }
        final int _tmp_2 = entity.isTransfer() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        if (entity.getTransferLinkId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getTransferLinkId());
        }
        final int _tmp_3 = entity.isInterest() ? 1 : 0;
        statement.bindLong(14, _tmp_3);
        statement.bindString(15, entity.getCategory());
        statement.bindString(16, entity.getUsername());
        final int _tmp_4 = entity.isSyncDirty() ? 1 : 0;
        statement.bindLong(17, _tmp_4);
        statement.bindLong(18, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfExpenseEntity = new EntityDeletionOrUpdateAdapter<ExpenseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `expenses` SET `id` = ?,`description` = ?,`amount` = ?,`creditCardId` = ?,`date` = ?,`fromTo` = ?,`details` = ?,`isFee` = ?,`isReward` = ?,`rewardType` = ?,`rewardValue` = ?,`isTransfer` = ?,`transferLinkId` = ?,`isInterest` = ?,`category` = ?,`username` = ?,`isSyncDirty` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDescription());
        statement.bindDouble(3, entity.getAmount());
        statement.bindString(4, entity.getCreditCardId());
        statement.bindString(5, entity.getDate());
        if (entity.getFromTo() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFromTo());
        }
        if (entity.getDetails() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDetails());
        }
        final int _tmp = entity.isFee() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.isReward() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        if (entity.getRewardType() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getRewardType());
        }
        if (entity.getRewardValue() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getRewardValue());
        }
        final int _tmp_2 = entity.isTransfer() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        if (entity.getTransferLinkId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getTransferLinkId());
        }
        final int _tmp_3 = entity.isInterest() ? 1 : 0;
        statement.bindLong(14, _tmp_3);
        statement.bindString(15, entity.getCategory());
        statement.bindString(16, entity.getUsername());
        final int _tmp_4 = entity.isSyncDirty() ? 1 : 0;
        statement.bindLong(17, _tmp_4);
        statement.bindLong(18, entity.getUpdatedAt());
        statement.bindString(19, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteExpenseById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM expenses WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByTransferLinkId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM expenses WHERE transferLinkId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertExpense(final ExpenseEntity expense,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfExpenseEntity.insert(expense);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertExpenses(final List<ExpenseEntity> expenses,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfExpenseEntity.insert(expenses);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateExpense(final ExpenseEntity expense,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfExpenseEntity.handle(expense);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteExpenseById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteExpenseById.acquire();
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
          __preparedStmtOfDeleteExpenseById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByTransferLinkId(final String transferLinkId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByTransferLinkId.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, transferLinkId);
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
          __preparedStmtOfDeleteByTransferLinkId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ExpenseEntity>> getAllExpensesFlow(final String username) {
    final String _sql = "SELECT * FROM expenses WHERE username = ? ORDER BY date DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, username);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"expenses"}, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreditCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "creditCardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfFromTo = CursorUtil.getColumnIndexOrThrow(_cursor, "fromTo");
          final int _cursorIndexOfDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "details");
          final int _cursorIndexOfIsFee = CursorUtil.getColumnIndexOrThrow(_cursor, "isFee");
          final int _cursorIndexOfIsReward = CursorUtil.getColumnIndexOrThrow(_cursor, "isReward");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfIsTransfer = CursorUtil.getColumnIndexOrThrow(_cursor, "isTransfer");
          final int _cursorIndexOfTransferLinkId = CursorUtil.getColumnIndexOrThrow(_cursor, "transferLinkId");
          final int _cursorIndexOfIsInterest = CursorUtil.getColumnIndexOrThrow(_cursor, "isInterest");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCreditCardId;
            _tmpCreditCardId = _cursor.getString(_cursorIndexOfCreditCardId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpFromTo;
            if (_cursor.isNull(_cursorIndexOfFromTo)) {
              _tmpFromTo = null;
            } else {
              _tmpFromTo = _cursor.getString(_cursorIndexOfFromTo);
            }
            final String _tmpDetails;
            if (_cursor.isNull(_cursorIndexOfDetails)) {
              _tmpDetails = null;
            } else {
              _tmpDetails = _cursor.getString(_cursorIndexOfDetails);
            }
            final boolean _tmpIsFee;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFee);
            _tmpIsFee = _tmp != 0;
            final boolean _tmpIsReward;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsReward);
            _tmpIsReward = _tmp_1 != 0;
            final String _tmpRewardType;
            if (_cursor.isNull(_cursorIndexOfRewardType)) {
              _tmpRewardType = null;
            } else {
              _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            }
            final Double _tmpRewardValue;
            if (_cursor.isNull(_cursorIndexOfRewardValue)) {
              _tmpRewardValue = null;
            } else {
              _tmpRewardValue = _cursor.getDouble(_cursorIndexOfRewardValue);
            }
            final boolean _tmpIsTransfer;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTransfer);
            _tmpIsTransfer = _tmp_2 != 0;
            final String _tmpTransferLinkId;
            if (_cursor.isNull(_cursorIndexOfTransferLinkId)) {
              _tmpTransferLinkId = null;
            } else {
              _tmpTransferLinkId = _cursor.getString(_cursorIndexOfTransferLinkId);
            }
            final boolean _tmpIsInterest;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsInterest);
            _tmpIsInterest = _tmp_3 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpCreditCardId,_tmpDate,_tmpFromTo,_tmpDetails,_tmpIsFee,_tmpIsReward,_tmpRewardType,_tmpRewardValue,_tmpIsTransfer,_tmpTransferLinkId,_tmpIsInterest,_tmpCategory,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
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
  public Object getAllExpenses(final String username,
      final Continuation<? super List<ExpenseEntity>> $completion) {
    final String _sql = "SELECT * FROM expenses WHERE username = ? ORDER BY date DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, username);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreditCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "creditCardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfFromTo = CursorUtil.getColumnIndexOrThrow(_cursor, "fromTo");
          final int _cursorIndexOfDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "details");
          final int _cursorIndexOfIsFee = CursorUtil.getColumnIndexOrThrow(_cursor, "isFee");
          final int _cursorIndexOfIsReward = CursorUtil.getColumnIndexOrThrow(_cursor, "isReward");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfIsTransfer = CursorUtil.getColumnIndexOrThrow(_cursor, "isTransfer");
          final int _cursorIndexOfTransferLinkId = CursorUtil.getColumnIndexOrThrow(_cursor, "transferLinkId");
          final int _cursorIndexOfIsInterest = CursorUtil.getColumnIndexOrThrow(_cursor, "isInterest");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCreditCardId;
            _tmpCreditCardId = _cursor.getString(_cursorIndexOfCreditCardId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpFromTo;
            if (_cursor.isNull(_cursorIndexOfFromTo)) {
              _tmpFromTo = null;
            } else {
              _tmpFromTo = _cursor.getString(_cursorIndexOfFromTo);
            }
            final String _tmpDetails;
            if (_cursor.isNull(_cursorIndexOfDetails)) {
              _tmpDetails = null;
            } else {
              _tmpDetails = _cursor.getString(_cursorIndexOfDetails);
            }
            final boolean _tmpIsFee;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFee);
            _tmpIsFee = _tmp != 0;
            final boolean _tmpIsReward;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsReward);
            _tmpIsReward = _tmp_1 != 0;
            final String _tmpRewardType;
            if (_cursor.isNull(_cursorIndexOfRewardType)) {
              _tmpRewardType = null;
            } else {
              _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            }
            final Double _tmpRewardValue;
            if (_cursor.isNull(_cursorIndexOfRewardValue)) {
              _tmpRewardValue = null;
            } else {
              _tmpRewardValue = _cursor.getDouble(_cursorIndexOfRewardValue);
            }
            final boolean _tmpIsTransfer;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTransfer);
            _tmpIsTransfer = _tmp_2 != 0;
            final String _tmpTransferLinkId;
            if (_cursor.isNull(_cursorIndexOfTransferLinkId)) {
              _tmpTransferLinkId = null;
            } else {
              _tmpTransferLinkId = _cursor.getString(_cursorIndexOfTransferLinkId);
            }
            final boolean _tmpIsInterest;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsInterest);
            _tmpIsInterest = _tmp_3 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpCreditCardId,_tmpDate,_tmpFromTo,_tmpDetails,_tmpIsFee,_tmpIsReward,_tmpRewardType,_tmpRewardValue,_tmpIsTransfer,_tmpTransferLinkId,_tmpIsInterest,_tmpCategory,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
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
  public Flow<List<ExpenseEntity>> getExpensesForCardFlow(final String cardId,
      final String username) {
    final String _sql = "SELECT * FROM expenses WHERE creditCardId = ? AND username = ? ORDER BY date DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, cardId);
    _argIndex = 2;
    _statement.bindString(_argIndex, username);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"expenses"}, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreditCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "creditCardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfFromTo = CursorUtil.getColumnIndexOrThrow(_cursor, "fromTo");
          final int _cursorIndexOfDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "details");
          final int _cursorIndexOfIsFee = CursorUtil.getColumnIndexOrThrow(_cursor, "isFee");
          final int _cursorIndexOfIsReward = CursorUtil.getColumnIndexOrThrow(_cursor, "isReward");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfIsTransfer = CursorUtil.getColumnIndexOrThrow(_cursor, "isTransfer");
          final int _cursorIndexOfTransferLinkId = CursorUtil.getColumnIndexOrThrow(_cursor, "transferLinkId");
          final int _cursorIndexOfIsInterest = CursorUtil.getColumnIndexOrThrow(_cursor, "isInterest");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCreditCardId;
            _tmpCreditCardId = _cursor.getString(_cursorIndexOfCreditCardId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpFromTo;
            if (_cursor.isNull(_cursorIndexOfFromTo)) {
              _tmpFromTo = null;
            } else {
              _tmpFromTo = _cursor.getString(_cursorIndexOfFromTo);
            }
            final String _tmpDetails;
            if (_cursor.isNull(_cursorIndexOfDetails)) {
              _tmpDetails = null;
            } else {
              _tmpDetails = _cursor.getString(_cursorIndexOfDetails);
            }
            final boolean _tmpIsFee;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFee);
            _tmpIsFee = _tmp != 0;
            final boolean _tmpIsReward;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsReward);
            _tmpIsReward = _tmp_1 != 0;
            final String _tmpRewardType;
            if (_cursor.isNull(_cursorIndexOfRewardType)) {
              _tmpRewardType = null;
            } else {
              _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            }
            final Double _tmpRewardValue;
            if (_cursor.isNull(_cursorIndexOfRewardValue)) {
              _tmpRewardValue = null;
            } else {
              _tmpRewardValue = _cursor.getDouble(_cursorIndexOfRewardValue);
            }
            final boolean _tmpIsTransfer;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTransfer);
            _tmpIsTransfer = _tmp_2 != 0;
            final String _tmpTransferLinkId;
            if (_cursor.isNull(_cursorIndexOfTransferLinkId)) {
              _tmpTransferLinkId = null;
            } else {
              _tmpTransferLinkId = _cursor.getString(_cursorIndexOfTransferLinkId);
            }
            final boolean _tmpIsInterest;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsInterest);
            _tmpIsInterest = _tmp_3 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpCreditCardId,_tmpDate,_tmpFromTo,_tmpDetails,_tmpIsFee,_tmpIsReward,_tmpRewardType,_tmpRewardValue,_tmpIsTransfer,_tmpTransferLinkId,_tmpIsInterest,_tmpCategory,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
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
  public Object getExpensesForCard(final String cardId, final String username,
      final Continuation<? super List<ExpenseEntity>> $completion) {
    final String _sql = "SELECT * FROM expenses WHERE creditCardId = ? AND username = ? ORDER BY date DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, cardId);
    _argIndex = 2;
    _statement.bindString(_argIndex, username);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreditCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "creditCardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfFromTo = CursorUtil.getColumnIndexOrThrow(_cursor, "fromTo");
          final int _cursorIndexOfDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "details");
          final int _cursorIndexOfIsFee = CursorUtil.getColumnIndexOrThrow(_cursor, "isFee");
          final int _cursorIndexOfIsReward = CursorUtil.getColumnIndexOrThrow(_cursor, "isReward");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfIsTransfer = CursorUtil.getColumnIndexOrThrow(_cursor, "isTransfer");
          final int _cursorIndexOfTransferLinkId = CursorUtil.getColumnIndexOrThrow(_cursor, "transferLinkId");
          final int _cursorIndexOfIsInterest = CursorUtil.getColumnIndexOrThrow(_cursor, "isInterest");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCreditCardId;
            _tmpCreditCardId = _cursor.getString(_cursorIndexOfCreditCardId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpFromTo;
            if (_cursor.isNull(_cursorIndexOfFromTo)) {
              _tmpFromTo = null;
            } else {
              _tmpFromTo = _cursor.getString(_cursorIndexOfFromTo);
            }
            final String _tmpDetails;
            if (_cursor.isNull(_cursorIndexOfDetails)) {
              _tmpDetails = null;
            } else {
              _tmpDetails = _cursor.getString(_cursorIndexOfDetails);
            }
            final boolean _tmpIsFee;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFee);
            _tmpIsFee = _tmp != 0;
            final boolean _tmpIsReward;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsReward);
            _tmpIsReward = _tmp_1 != 0;
            final String _tmpRewardType;
            if (_cursor.isNull(_cursorIndexOfRewardType)) {
              _tmpRewardType = null;
            } else {
              _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            }
            final Double _tmpRewardValue;
            if (_cursor.isNull(_cursorIndexOfRewardValue)) {
              _tmpRewardValue = null;
            } else {
              _tmpRewardValue = _cursor.getDouble(_cursorIndexOfRewardValue);
            }
            final boolean _tmpIsTransfer;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTransfer);
            _tmpIsTransfer = _tmp_2 != 0;
            final String _tmpTransferLinkId;
            if (_cursor.isNull(_cursorIndexOfTransferLinkId)) {
              _tmpTransferLinkId = null;
            } else {
              _tmpTransferLinkId = _cursor.getString(_cursorIndexOfTransferLinkId);
            }
            final boolean _tmpIsInterest;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsInterest);
            _tmpIsInterest = _tmp_3 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpCreditCardId,_tmpDate,_tmpFromTo,_tmpDetails,_tmpIsFee,_tmpIsReward,_tmpRewardType,_tmpRewardValue,_tmpIsTransfer,_tmpTransferLinkId,_tmpIsInterest,_tmpCategory,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
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
  public PagingSource<Integer, ExpenseEntity> getPagedExpenses(final String username) {
    final String _sql = "SELECT * FROM expenses WHERE username = ? ORDER BY date DESC, id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, username);
    return new LimitOffsetPagingSource<ExpenseEntity>(_statement, __db, "expenses") {
      @Override
      @NonNull
      protected List<ExpenseEntity> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(cursor, "description");
        final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(cursor, "amount");
        final int _cursorIndexOfCreditCardId = CursorUtil.getColumnIndexOrThrow(cursor, "creditCardId");
        final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(cursor, "date");
        final int _cursorIndexOfFromTo = CursorUtil.getColumnIndexOrThrow(cursor, "fromTo");
        final int _cursorIndexOfDetails = CursorUtil.getColumnIndexOrThrow(cursor, "details");
        final int _cursorIndexOfIsFee = CursorUtil.getColumnIndexOrThrow(cursor, "isFee");
        final int _cursorIndexOfIsReward = CursorUtil.getColumnIndexOrThrow(cursor, "isReward");
        final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(cursor, "rewardType");
        final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(cursor, "rewardValue");
        final int _cursorIndexOfIsTransfer = CursorUtil.getColumnIndexOrThrow(cursor, "isTransfer");
        final int _cursorIndexOfTransferLinkId = CursorUtil.getColumnIndexOrThrow(cursor, "transferLinkId");
        final int _cursorIndexOfIsInterest = CursorUtil.getColumnIndexOrThrow(cursor, "isInterest");
        final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(cursor, "category");
        final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(cursor, "username");
        final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(cursor, "isSyncDirty");
        final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "updatedAt");
        final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(cursor.getCount());
        while (cursor.moveToNext()) {
          final ExpenseEntity _item;
          final String _tmpId;
          _tmpId = cursor.getString(_cursorIndexOfId);
          final String _tmpDescription;
          _tmpDescription = cursor.getString(_cursorIndexOfDescription);
          final double _tmpAmount;
          _tmpAmount = cursor.getDouble(_cursorIndexOfAmount);
          final String _tmpCreditCardId;
          _tmpCreditCardId = cursor.getString(_cursorIndexOfCreditCardId);
          final String _tmpDate;
          _tmpDate = cursor.getString(_cursorIndexOfDate);
          final String _tmpFromTo;
          if (cursor.isNull(_cursorIndexOfFromTo)) {
            _tmpFromTo = null;
          } else {
            _tmpFromTo = cursor.getString(_cursorIndexOfFromTo);
          }
          final String _tmpDetails;
          if (cursor.isNull(_cursorIndexOfDetails)) {
            _tmpDetails = null;
          } else {
            _tmpDetails = cursor.getString(_cursorIndexOfDetails);
          }
          final boolean _tmpIsFee;
          final int _tmp;
          _tmp = cursor.getInt(_cursorIndexOfIsFee);
          _tmpIsFee = _tmp != 0;
          final boolean _tmpIsReward;
          final int _tmp_1;
          _tmp_1 = cursor.getInt(_cursorIndexOfIsReward);
          _tmpIsReward = _tmp_1 != 0;
          final String _tmpRewardType;
          if (cursor.isNull(_cursorIndexOfRewardType)) {
            _tmpRewardType = null;
          } else {
            _tmpRewardType = cursor.getString(_cursorIndexOfRewardType);
          }
          final Double _tmpRewardValue;
          if (cursor.isNull(_cursorIndexOfRewardValue)) {
            _tmpRewardValue = null;
          } else {
            _tmpRewardValue = cursor.getDouble(_cursorIndexOfRewardValue);
          }
          final boolean _tmpIsTransfer;
          final int _tmp_2;
          _tmp_2 = cursor.getInt(_cursorIndexOfIsTransfer);
          _tmpIsTransfer = _tmp_2 != 0;
          final String _tmpTransferLinkId;
          if (cursor.isNull(_cursorIndexOfTransferLinkId)) {
            _tmpTransferLinkId = null;
          } else {
            _tmpTransferLinkId = cursor.getString(_cursorIndexOfTransferLinkId);
          }
          final boolean _tmpIsInterest;
          final int _tmp_3;
          _tmp_3 = cursor.getInt(_cursorIndexOfIsInterest);
          _tmpIsInterest = _tmp_3 != 0;
          final String _tmpCategory;
          _tmpCategory = cursor.getString(_cursorIndexOfCategory);
          final String _tmpUsername;
          _tmpUsername = cursor.getString(_cursorIndexOfUsername);
          final boolean _tmpIsSyncDirty;
          final int _tmp_4;
          _tmp_4 = cursor.getInt(_cursorIndexOfIsSyncDirty);
          _tmpIsSyncDirty = _tmp_4 != 0;
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = cursor.getLong(_cursorIndexOfUpdatedAt);
          _item = new ExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpCreditCardId,_tmpDate,_tmpFromTo,_tmpDetails,_tmpIsFee,_tmpIsReward,_tmpRewardType,_tmpRewardValue,_tmpIsTransfer,_tmpTransferLinkId,_tmpIsInterest,_tmpCategory,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public PagingSource<Integer, ExpenseEntity> searchExpensesPaged(final String username,
      final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM expenses \n"
            + "        WHERE username = ? \n"
            + "        AND (\n"
            + "            description LIKE '%' || ? || '%' \n"
            + "            OR category LIKE '%' || ? || '%' \n"
            + "            OR details LIKE '%' || ? || '%'\n"
            + "            OR fromTo LIKE '%' || ? || '%'\n"
            + "            OR date LIKE '%' || ? || '%'\n"
            + "        )\n"
            + "        ORDER BY date DESC, id DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 6);
    int _argIndex = 1;
    _statement.bindString(_argIndex, username);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    _argIndex = 4;
    _statement.bindString(_argIndex, query);
    _argIndex = 5;
    _statement.bindString(_argIndex, query);
    _argIndex = 6;
    _statement.bindString(_argIndex, query);
    return new LimitOffsetPagingSource<ExpenseEntity>(_statement, __db, "expenses") {
      @Override
      @NonNull
      protected List<ExpenseEntity> convertRows(@NonNull final Cursor cursor) {
        final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(cursor, "id");
        final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(cursor, "description");
        final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(cursor, "amount");
        final int _cursorIndexOfCreditCardId = CursorUtil.getColumnIndexOrThrow(cursor, "creditCardId");
        final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(cursor, "date");
        final int _cursorIndexOfFromTo = CursorUtil.getColumnIndexOrThrow(cursor, "fromTo");
        final int _cursorIndexOfDetails = CursorUtil.getColumnIndexOrThrow(cursor, "details");
        final int _cursorIndexOfIsFee = CursorUtil.getColumnIndexOrThrow(cursor, "isFee");
        final int _cursorIndexOfIsReward = CursorUtil.getColumnIndexOrThrow(cursor, "isReward");
        final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(cursor, "rewardType");
        final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(cursor, "rewardValue");
        final int _cursorIndexOfIsTransfer = CursorUtil.getColumnIndexOrThrow(cursor, "isTransfer");
        final int _cursorIndexOfTransferLinkId = CursorUtil.getColumnIndexOrThrow(cursor, "transferLinkId");
        final int _cursorIndexOfIsInterest = CursorUtil.getColumnIndexOrThrow(cursor, "isInterest");
        final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(cursor, "category");
        final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(cursor, "username");
        final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(cursor, "isSyncDirty");
        final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(cursor, "updatedAt");
        final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(cursor.getCount());
        while (cursor.moveToNext()) {
          final ExpenseEntity _item;
          final String _tmpId;
          _tmpId = cursor.getString(_cursorIndexOfId);
          final String _tmpDescription;
          _tmpDescription = cursor.getString(_cursorIndexOfDescription);
          final double _tmpAmount;
          _tmpAmount = cursor.getDouble(_cursorIndexOfAmount);
          final String _tmpCreditCardId;
          _tmpCreditCardId = cursor.getString(_cursorIndexOfCreditCardId);
          final String _tmpDate;
          _tmpDate = cursor.getString(_cursorIndexOfDate);
          final String _tmpFromTo;
          if (cursor.isNull(_cursorIndexOfFromTo)) {
            _tmpFromTo = null;
          } else {
            _tmpFromTo = cursor.getString(_cursorIndexOfFromTo);
          }
          final String _tmpDetails;
          if (cursor.isNull(_cursorIndexOfDetails)) {
            _tmpDetails = null;
          } else {
            _tmpDetails = cursor.getString(_cursorIndexOfDetails);
          }
          final boolean _tmpIsFee;
          final int _tmp;
          _tmp = cursor.getInt(_cursorIndexOfIsFee);
          _tmpIsFee = _tmp != 0;
          final boolean _tmpIsReward;
          final int _tmp_1;
          _tmp_1 = cursor.getInt(_cursorIndexOfIsReward);
          _tmpIsReward = _tmp_1 != 0;
          final String _tmpRewardType;
          if (cursor.isNull(_cursorIndexOfRewardType)) {
            _tmpRewardType = null;
          } else {
            _tmpRewardType = cursor.getString(_cursorIndexOfRewardType);
          }
          final Double _tmpRewardValue;
          if (cursor.isNull(_cursorIndexOfRewardValue)) {
            _tmpRewardValue = null;
          } else {
            _tmpRewardValue = cursor.getDouble(_cursorIndexOfRewardValue);
          }
          final boolean _tmpIsTransfer;
          final int _tmp_2;
          _tmp_2 = cursor.getInt(_cursorIndexOfIsTransfer);
          _tmpIsTransfer = _tmp_2 != 0;
          final String _tmpTransferLinkId;
          if (cursor.isNull(_cursorIndexOfTransferLinkId)) {
            _tmpTransferLinkId = null;
          } else {
            _tmpTransferLinkId = cursor.getString(_cursorIndexOfTransferLinkId);
          }
          final boolean _tmpIsInterest;
          final int _tmp_3;
          _tmp_3 = cursor.getInt(_cursorIndexOfIsInterest);
          _tmpIsInterest = _tmp_3 != 0;
          final String _tmpCategory;
          _tmpCategory = cursor.getString(_cursorIndexOfCategory);
          final String _tmpUsername;
          _tmpUsername = cursor.getString(_cursorIndexOfUsername);
          final boolean _tmpIsSyncDirty;
          final int _tmp_4;
          _tmp_4 = cursor.getInt(_cursorIndexOfIsSyncDirty);
          _tmpIsSyncDirty = _tmp_4 != 0;
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = cursor.getLong(_cursorIndexOfUpdatedAt);
          _item = new ExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpCreditCardId,_tmpDate,_tmpFromTo,_tmpDetails,_tmpIsFee,_tmpIsReward,_tmpRewardType,_tmpRewardValue,_tmpIsTransfer,_tmpTransferLinkId,_tmpIsInterest,_tmpCategory,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
          _result.add(_item);
        }
        return _result;
      }
    };
  }

  @Override
  public Object getExpenseById(final String id,
      final Continuation<? super ExpenseEntity> $completion) {
    final String _sql = "SELECT * FROM expenses WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ExpenseEntity>() {
      @Override
      @Nullable
      public ExpenseEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreditCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "creditCardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfFromTo = CursorUtil.getColumnIndexOrThrow(_cursor, "fromTo");
          final int _cursorIndexOfDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "details");
          final int _cursorIndexOfIsFee = CursorUtil.getColumnIndexOrThrow(_cursor, "isFee");
          final int _cursorIndexOfIsReward = CursorUtil.getColumnIndexOrThrow(_cursor, "isReward");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfIsTransfer = CursorUtil.getColumnIndexOrThrow(_cursor, "isTransfer");
          final int _cursorIndexOfTransferLinkId = CursorUtil.getColumnIndexOrThrow(_cursor, "transferLinkId");
          final int _cursorIndexOfIsInterest = CursorUtil.getColumnIndexOrThrow(_cursor, "isInterest");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final ExpenseEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCreditCardId;
            _tmpCreditCardId = _cursor.getString(_cursorIndexOfCreditCardId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpFromTo;
            if (_cursor.isNull(_cursorIndexOfFromTo)) {
              _tmpFromTo = null;
            } else {
              _tmpFromTo = _cursor.getString(_cursorIndexOfFromTo);
            }
            final String _tmpDetails;
            if (_cursor.isNull(_cursorIndexOfDetails)) {
              _tmpDetails = null;
            } else {
              _tmpDetails = _cursor.getString(_cursorIndexOfDetails);
            }
            final boolean _tmpIsFee;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFee);
            _tmpIsFee = _tmp != 0;
            final boolean _tmpIsReward;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsReward);
            _tmpIsReward = _tmp_1 != 0;
            final String _tmpRewardType;
            if (_cursor.isNull(_cursorIndexOfRewardType)) {
              _tmpRewardType = null;
            } else {
              _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            }
            final Double _tmpRewardValue;
            if (_cursor.isNull(_cursorIndexOfRewardValue)) {
              _tmpRewardValue = null;
            } else {
              _tmpRewardValue = _cursor.getDouble(_cursorIndexOfRewardValue);
            }
            final boolean _tmpIsTransfer;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTransfer);
            _tmpIsTransfer = _tmp_2 != 0;
            final String _tmpTransferLinkId;
            if (_cursor.isNull(_cursorIndexOfTransferLinkId)) {
              _tmpTransferLinkId = null;
            } else {
              _tmpTransferLinkId = _cursor.getString(_cursorIndexOfTransferLinkId);
            }
            final boolean _tmpIsInterest;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsInterest);
            _tmpIsInterest = _tmp_3 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new ExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpCreditCardId,_tmpDate,_tmpFromTo,_tmpDetails,_tmpIsFee,_tmpIsReward,_tmpRewardType,_tmpRewardValue,_tmpIsTransfer,_tmpTransferLinkId,_tmpIsInterest,_tmpCategory,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
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
  public Object getExpensesByTransferLinkId(final String transferLinkId,
      final Continuation<? super List<ExpenseEntity>> $completion) {
    final String _sql = "SELECT * FROM expenses WHERE transferLinkId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, transferLinkId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreditCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "creditCardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfFromTo = CursorUtil.getColumnIndexOrThrow(_cursor, "fromTo");
          final int _cursorIndexOfDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "details");
          final int _cursorIndexOfIsFee = CursorUtil.getColumnIndexOrThrow(_cursor, "isFee");
          final int _cursorIndexOfIsReward = CursorUtil.getColumnIndexOrThrow(_cursor, "isReward");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfIsTransfer = CursorUtil.getColumnIndexOrThrow(_cursor, "isTransfer");
          final int _cursorIndexOfTransferLinkId = CursorUtil.getColumnIndexOrThrow(_cursor, "transferLinkId");
          final int _cursorIndexOfIsInterest = CursorUtil.getColumnIndexOrThrow(_cursor, "isInterest");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCreditCardId;
            _tmpCreditCardId = _cursor.getString(_cursorIndexOfCreditCardId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpFromTo;
            if (_cursor.isNull(_cursorIndexOfFromTo)) {
              _tmpFromTo = null;
            } else {
              _tmpFromTo = _cursor.getString(_cursorIndexOfFromTo);
            }
            final String _tmpDetails;
            if (_cursor.isNull(_cursorIndexOfDetails)) {
              _tmpDetails = null;
            } else {
              _tmpDetails = _cursor.getString(_cursorIndexOfDetails);
            }
            final boolean _tmpIsFee;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFee);
            _tmpIsFee = _tmp != 0;
            final boolean _tmpIsReward;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsReward);
            _tmpIsReward = _tmp_1 != 0;
            final String _tmpRewardType;
            if (_cursor.isNull(_cursorIndexOfRewardType)) {
              _tmpRewardType = null;
            } else {
              _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            }
            final Double _tmpRewardValue;
            if (_cursor.isNull(_cursorIndexOfRewardValue)) {
              _tmpRewardValue = null;
            } else {
              _tmpRewardValue = _cursor.getDouble(_cursorIndexOfRewardValue);
            }
            final boolean _tmpIsTransfer;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTransfer);
            _tmpIsTransfer = _tmp_2 != 0;
            final String _tmpTransferLinkId;
            if (_cursor.isNull(_cursorIndexOfTransferLinkId)) {
              _tmpTransferLinkId = null;
            } else {
              _tmpTransferLinkId = _cursor.getString(_cursorIndexOfTransferLinkId);
            }
            final boolean _tmpIsInterest;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsInterest);
            _tmpIsInterest = _tmp_3 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpCreditCardId,_tmpDate,_tmpFromTo,_tmpDetails,_tmpIsFee,_tmpIsReward,_tmpRewardType,_tmpRewardValue,_tmpIsTransfer,_tmpTransferLinkId,_tmpIsInterest,_tmpCategory,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
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
  public Object getDirtyExpenses(final Continuation<? super List<ExpenseEntity>> $completion) {
    final String _sql = "SELECT * FROM expenses WHERE isSyncDirty = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCreditCardId = CursorUtil.getColumnIndexOrThrow(_cursor, "creditCardId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfFromTo = CursorUtil.getColumnIndexOrThrow(_cursor, "fromTo");
          final int _cursorIndexOfDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "details");
          final int _cursorIndexOfIsFee = CursorUtil.getColumnIndexOrThrow(_cursor, "isFee");
          final int _cursorIndexOfIsReward = CursorUtil.getColumnIndexOrThrow(_cursor, "isReward");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfIsTransfer = CursorUtil.getColumnIndexOrThrow(_cursor, "isTransfer");
          final int _cursorIndexOfTransferLinkId = CursorUtil.getColumnIndexOrThrow(_cursor, "transferLinkId");
          final int _cursorIndexOfIsInterest = CursorUtil.getColumnIndexOrThrow(_cursor, "isInterest");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfIsSyncDirty = CursorUtil.getColumnIndexOrThrow(_cursor, "isSyncDirty");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCreditCardId;
            _tmpCreditCardId = _cursor.getString(_cursorIndexOfCreditCardId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpFromTo;
            if (_cursor.isNull(_cursorIndexOfFromTo)) {
              _tmpFromTo = null;
            } else {
              _tmpFromTo = _cursor.getString(_cursorIndexOfFromTo);
            }
            final String _tmpDetails;
            if (_cursor.isNull(_cursorIndexOfDetails)) {
              _tmpDetails = null;
            } else {
              _tmpDetails = _cursor.getString(_cursorIndexOfDetails);
            }
            final boolean _tmpIsFee;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFee);
            _tmpIsFee = _tmp != 0;
            final boolean _tmpIsReward;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsReward);
            _tmpIsReward = _tmp_1 != 0;
            final String _tmpRewardType;
            if (_cursor.isNull(_cursorIndexOfRewardType)) {
              _tmpRewardType = null;
            } else {
              _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            }
            final Double _tmpRewardValue;
            if (_cursor.isNull(_cursorIndexOfRewardValue)) {
              _tmpRewardValue = null;
            } else {
              _tmpRewardValue = _cursor.getDouble(_cursorIndexOfRewardValue);
            }
            final boolean _tmpIsTransfer;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsTransfer);
            _tmpIsTransfer = _tmp_2 != 0;
            final String _tmpTransferLinkId;
            if (_cursor.isNull(_cursorIndexOfTransferLinkId)) {
              _tmpTransferLinkId = null;
            } else {
              _tmpTransferLinkId = _cursor.getString(_cursorIndexOfTransferLinkId);
            }
            final boolean _tmpIsInterest;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsInterest);
            _tmpIsInterest = _tmp_3 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final boolean _tmpIsSyncDirty;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfIsSyncDirty);
            _tmpIsSyncDirty = _tmp_4 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpDescription,_tmpAmount,_tmpCreditCardId,_tmpDate,_tmpFromTo,_tmpDetails,_tmpIsFee,_tmpIsReward,_tmpRewardType,_tmpRewardValue,_tmpIsTransfer,_tmpTransferLinkId,_tmpIsInterest,_tmpCategory,_tmpUsername,_tmpIsSyncDirty,_tmpUpdatedAt);
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
        _stringBuilder.append("UPDATE expenses SET isSyncDirty = 0 WHERE id IN (");
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
