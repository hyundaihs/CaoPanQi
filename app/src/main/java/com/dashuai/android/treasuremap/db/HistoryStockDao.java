package com.dashuai.android.treasuremap.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dashuai.android.treasuremap.entity.Stock;

import java.util.ArrayList;
import java.util.List;


public class HistoryStockDao extends StockDao {

    public HistoryStockDao(SQLiteDatabase db) {
        super(db, Stock.HISTORY_TAB_NAME);
    }

    public List<Stock> querySortById() {
        Cursor cursor = db.query(TAB_NAME, null, null, null, null, null, "id"
                + " DESC");
        List<Stock> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(query(cursor, new Stock()));
        }
        cursor.close();
        return list;
    }

}
