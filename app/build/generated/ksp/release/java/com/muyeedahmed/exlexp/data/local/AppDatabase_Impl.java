package com.muyeedahmed.exlexp.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.muyeedahmed.exlexp.data.local.dao.CardDao;
import com.muyeedahmed.exlexp.data.local.dao.CardDao_Impl;
import com.muyeedahmed.exlexp.data.local.dao.DeletedRecordDao;
import com.muyeedahmed.exlexp.data.local.dao.DeletedRecordDao_Impl;
import com.muyeedahmed.exlexp.data.local.dao.ExpenseDao;
import com.muyeedahmed.exlexp.data.local.dao.ExpenseDao_Impl;
import com.muyeedahmed.exlexp.data.local.dao.FutureExpenseDao;
import com.muyeedahmed.exlexp.data.local.dao.FutureExpenseDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CardDao _cardDao;

  private volatile ExpenseDao _expenseDao;

  private volatile FutureExpenseDao _futureExpenseDao;

  private volatile DeletedRecordDao _deletedRecordDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cards` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `isChecking` INTEGER NOT NULL, `isSaving` INTEGER NOT NULL, `isBrokerage` INTEGER NOT NULL, `isHidden` INTEGER NOT NULL, `priority` INTEGER NOT NULL, `openDate` TEXT NOT NULL, `username` TEXT NOT NULL, `isSyncDirty` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `expenses` (`id` TEXT NOT NULL, `description` TEXT NOT NULL, `amount` REAL NOT NULL, `creditCardId` TEXT NOT NULL, `date` TEXT NOT NULL, `fromTo` TEXT, `details` TEXT, `isFee` INTEGER NOT NULL, `isReward` INTEGER NOT NULL, `rewardType` TEXT, `rewardValue` REAL, `isTransfer` INTEGER NOT NULL, `transferLinkId` TEXT, `isInterest` INTEGER NOT NULL, `category` TEXT NOT NULL, `username` TEXT NOT NULL, `isSyncDirty` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_creditCardId` ON `expenses` (`creditCardId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_date` ON `expenses` (`date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_username` ON `expenses` (`username`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_category` ON `expenses` (`category`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_transferLinkId` ON `expenses` (`transferLinkId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `future_expenses` (`id` TEXT NOT NULL, `description` TEXT NOT NULL, `amount` REAL NOT NULL, `dueDate` TEXT, `username` TEXT NOT NULL, `isSyncDirty` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `deleted_records` (`id` TEXT NOT NULL, `tableName` TEXT NOT NULL, `deletedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4f6ba1a9234efc2428a2186c50c78557')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cards`");
        db.execSQL("DROP TABLE IF EXISTS `expenses`");
        db.execSQL("DROP TABLE IF EXISTS `future_expenses`");
        db.execSQL("DROP TABLE IF EXISTS `deleted_records`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCards = new HashMap<String, TableInfo.Column>(11);
        _columnsCards.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("isChecking", new TableInfo.Column("isChecking", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("isSaving", new TableInfo.Column("isSaving", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("isBrokerage", new TableInfo.Column("isBrokerage", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("isHidden", new TableInfo.Column("isHidden", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("openDate", new TableInfo.Column("openDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("isSyncDirty", new TableInfo.Column("isSyncDirty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCards.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCards = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCards = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCards = new TableInfo("cards", _columnsCards, _foreignKeysCards, _indicesCards);
        final TableInfo _existingCards = TableInfo.read(db, "cards");
        if (!_infoCards.equals(_existingCards)) {
          return new RoomOpenHelper.ValidationResult(false, "cards(com.muyeedahmed.exlexp.data.local.entity.CreditCardEntity).\n"
                  + " Expected:\n" + _infoCards + "\n"
                  + " Found:\n" + _existingCards);
        }
        final HashMap<String, TableInfo.Column> _columnsExpenses = new HashMap<String, TableInfo.Column>(18);
        _columnsExpenses.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("creditCardId", new TableInfo.Column("creditCardId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("fromTo", new TableInfo.Column("fromTo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("details", new TableInfo.Column("details", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("isFee", new TableInfo.Column("isFee", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("isReward", new TableInfo.Column("isReward", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("rewardType", new TableInfo.Column("rewardType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("rewardValue", new TableInfo.Column("rewardValue", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("isTransfer", new TableInfo.Column("isTransfer", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("transferLinkId", new TableInfo.Column("transferLinkId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("isInterest", new TableInfo.Column("isInterest", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("isSyncDirty", new TableInfo.Column("isSyncDirty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExpenses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExpenses = new HashSet<TableInfo.Index>(5);
        _indicesExpenses.add(new TableInfo.Index("index_expenses_creditCardId", false, Arrays.asList("creditCardId"), Arrays.asList("ASC")));
        _indicesExpenses.add(new TableInfo.Index("index_expenses_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        _indicesExpenses.add(new TableInfo.Index("index_expenses_username", false, Arrays.asList("username"), Arrays.asList("ASC")));
        _indicesExpenses.add(new TableInfo.Index("index_expenses_category", false, Arrays.asList("category"), Arrays.asList("ASC")));
        _indicesExpenses.add(new TableInfo.Index("index_expenses_transferLinkId", false, Arrays.asList("transferLinkId"), Arrays.asList("ASC")));
        final TableInfo _infoExpenses = new TableInfo("expenses", _columnsExpenses, _foreignKeysExpenses, _indicesExpenses);
        final TableInfo _existingExpenses = TableInfo.read(db, "expenses");
        if (!_infoExpenses.equals(_existingExpenses)) {
          return new RoomOpenHelper.ValidationResult(false, "expenses(com.muyeedahmed.exlexp.data.local.entity.ExpenseEntity).\n"
                  + " Expected:\n" + _infoExpenses + "\n"
                  + " Found:\n" + _existingExpenses);
        }
        final HashMap<String, TableInfo.Column> _columnsFutureExpenses = new HashMap<String, TableInfo.Column>(6);
        _columnsFutureExpenses.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureExpenses.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureExpenses.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureExpenses.put("dueDate", new TableInfo.Column("dueDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureExpenses.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureExpenses.put("isSyncDirty", new TableInfo.Column("isSyncDirty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFutureExpenses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFutureExpenses = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFutureExpenses = new TableInfo("future_expenses", _columnsFutureExpenses, _foreignKeysFutureExpenses, _indicesFutureExpenses);
        final TableInfo _existingFutureExpenses = TableInfo.read(db, "future_expenses");
        if (!_infoFutureExpenses.equals(_existingFutureExpenses)) {
          return new RoomOpenHelper.ValidationResult(false, "future_expenses(com.muyeedahmed.exlexp.data.local.entity.FutureExpenseEntity).\n"
                  + " Expected:\n" + _infoFutureExpenses + "\n"
                  + " Found:\n" + _existingFutureExpenses);
        }
        final HashMap<String, TableInfo.Column> _columnsDeletedRecords = new HashMap<String, TableInfo.Column>(3);
        _columnsDeletedRecords.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDeletedRecords.put("tableName", new TableInfo.Column("tableName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDeletedRecords.put("deletedAt", new TableInfo.Column("deletedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDeletedRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDeletedRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDeletedRecords = new TableInfo("deleted_records", _columnsDeletedRecords, _foreignKeysDeletedRecords, _indicesDeletedRecords);
        final TableInfo _existingDeletedRecords = TableInfo.read(db, "deleted_records");
        if (!_infoDeletedRecords.equals(_existingDeletedRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "deleted_records(com.muyeedahmed.exlexp.data.local.entity.DeletedRecordEntity).\n"
                  + " Expected:\n" + _infoDeletedRecords + "\n"
                  + " Found:\n" + _existingDeletedRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4f6ba1a9234efc2428a2186c50c78557", "dd671df7dceaca36ed7088a9c207d251");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cards","expenses","future_expenses","deleted_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `cards`");
      _db.execSQL("DELETE FROM `expenses`");
      _db.execSQL("DELETE FROM `future_expenses`");
      _db.execSQL("DELETE FROM `deleted_records`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CardDao.class, CardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExpenseDao.class, ExpenseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FutureExpenseDao.class, FutureExpenseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DeletedRecordDao.class, DeletedRecordDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CardDao cardDao() {
    if (_cardDao != null) {
      return _cardDao;
    } else {
      synchronized(this) {
        if(_cardDao == null) {
          _cardDao = new CardDao_Impl(this);
        }
        return _cardDao;
      }
    }
  }

  @Override
  public ExpenseDao expenseDao() {
    if (_expenseDao != null) {
      return _expenseDao;
    } else {
      synchronized(this) {
        if(_expenseDao == null) {
          _expenseDao = new ExpenseDao_Impl(this);
        }
        return _expenseDao;
      }
    }
  }

  @Override
  public FutureExpenseDao futureExpenseDao() {
    if (_futureExpenseDao != null) {
      return _futureExpenseDao;
    } else {
      synchronized(this) {
        if(_futureExpenseDao == null) {
          _futureExpenseDao = new FutureExpenseDao_Impl(this);
        }
        return _futureExpenseDao;
      }
    }
  }

  @Override
  public DeletedRecordDao deletedRecordDao() {
    if (_deletedRecordDao != null) {
      return _deletedRecordDao;
    } else {
      synchronized(this) {
        if(_deletedRecordDao == null) {
          _deletedRecordDao = new DeletedRecordDao_Impl(this);
        }
        return _deletedRecordDao;
      }
    }
  }
}
