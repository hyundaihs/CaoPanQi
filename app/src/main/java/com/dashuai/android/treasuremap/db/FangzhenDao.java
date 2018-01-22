package com.dashuai.android.treasuremap.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dashuai.android.treasuremap.entity.Fangzhen;
import com.dashuai.android.treasuremap.entity.FangzhenBig;
import com.dashuai.android.treasuremap.entity.Stock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 17/4/15.
 */
public class FangzhenDao extends SQliteDao<Fangzhen> {

    public FangzhenDao(SQLiteDatabase db) {
        super(db, Fangzhen.TAB_NAME);
    }

    public void deleteByStockIds(List<Fangzhen> stocks) {
        for (int i = 0; i < stocks.size(); i++) {
            deleteByStockId(stocks.get(i).getStockId());
        }
    }


    public void deleteByStockId(int stockId) {
        db.delete(TAB_NAME, " stockId = ? ", new String[]{String.valueOf(stockId)});
    }

    public boolean isColleted(int stockId, long date) {
        Cursor cursor = db.query(TAB_NAME, null, "stockId =? and date =?",
                new String[]{String.valueOf(stockId), String.valueOf(date)}, null, null, null);
        boolean flag = cursor.moveToFirst();
        cursor.close();
        return flag;
    }

    public List<Fangzhen> querySortByID() {
        Cursor cursor = db.query(TAB_NAME, null, null, null, null, null, "id"
                + " DESC");
        List<Fangzhen> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(query(cursor, new Fangzhen()));
        }
        cursor.close();
        return list;
    }
}
