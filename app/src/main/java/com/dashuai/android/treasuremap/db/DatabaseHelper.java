package com.dashuai.android.treasuremap.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.entity.BZListStatus;
import com.dashuai.android.treasuremap.entity.Fangzhen;
import com.dashuai.android.treasuremap.entity.Record;
import com.dashuai.android.treasuremap.entity.Score;
import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.entity.StockLog;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String name = "caopanqi.db";// 数据库名称
    private static final int version = 24;// 数据库版本

    public DatabaseHelper(Context context) {
        super(context, name, null, version);
    }

    public DatabaseHelper(Context context, int version) {
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createAllTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateAllTables(db);
        if (oldVersion <= 9) {
            new HistoryStockDao(db).createTable(new Stock());
        }
        if (oldVersion <= 10 && oldVersion > 9) {
            new StockLogDao(db).deleteTable();
        }
        if (oldVersion <= 12 && oldVersion > 10) {
            new RecordDao(db).createTable(new Record());
        }
        if (oldVersion <= 14) {
            new ScoreDao(db).createTable(new Score());
        }
        if (oldVersion <= 16) {
            new FangzhenDao(db).createTable(new Fangzhen());
        }
        if (oldVersion <= 17) {
            new BZListStatusDao(db).createTable(new BZListStatus());
        }
        if (oldVersion <= 20) {
            new ScoreDao(db).cleanTable();
            new RecordDao(db).cleanTable();
        }
        if (oldVersion <= 22) {
            new FangzhenDao(db).cleanTable();
        }
    }

    public void createAllTables(SQLiteDatabase db) {
        new StockDao(db).createTable(new Stock());
        new HistoryStockDao(db).createTable(new Stock());
        new StockLogDao(db).createTable(new StockLog());
        new RecordDao(db).createTable(new Record());
        new ScoreDao(db).createTable(new Score());
        new FangzhenDao(db).createTable(new Fangzhen());
        new BZListStatusDao(db).createTable(new BZListStatus());
    }

    public void updateAllTables(SQLiteDatabase db) {
        new StockDao(db).updateTable(new Stock());
        new HistoryStockDao(db).updateTable(new Stock());
        new StockLogDao(db).updateTable(new StockLog());
        new RecordDao(db).updateTable(new Record());
        new ScoreDao(db).updateTable(new Score());
        new FangzhenDao(db).updateTable(new Fangzhen());
        new BZListStatusDao(db).updateTable(new BZListStatus());
    }


}
