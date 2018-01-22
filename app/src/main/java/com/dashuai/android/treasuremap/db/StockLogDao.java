package com.dashuai.android.treasuremap.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dashuai.android.treasuremap.entity.Stock;
import com.dashuai.android.treasuremap.entity.StockLog;

import java.util.ArrayList;
import java.util.List;

public class StockLogDao extends SQliteDao<StockLog> {

    public StockLogDao(SQLiteDatabase db) {
       super(db, StockLog.TAB_NAME);
    }

    public long addByCodeName(StockLog stockLog){
        Cursor cursor = db.query(TAB_NAME, null, "code" + " = ? and " + "name"
                        + " = ?  ",
                new String[]{stockLog.getCode(), stockLog.getName()}, null,
                null, null);
        while (cursor.moveToNext()) {
            String str = cursor.getString(cursor.getColumnIndex("message"));
            if (str.contains("进入关注阶段")) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                db.delete(TAB_NAME, "id" + " = ? ",
                        new String[]{String.valueOf(id)});
                delete(stockLog);
            }
        }
        cursor.close();
        return add(stockLog);
    }

    List<StockLog> querySortById() {
        Cursor cursor = db.query(TAB_NAME, null, null, null, null, null, "id"
                + " DESC");
        List<StockLog> list = new ArrayList<StockLog>();
        while (cursor.moveToNext()) {
            list.add(query(cursor, new StockLog()));
        }
        cursor.close();
        return list;
    }

    public List<StockLog> queryTen(boolean isHideHongbao,
                                   boolean isHidePingcang, boolean isHideJiancang, boolean isHideFangzhen) {
        return queryAll(10, isHideHongbao, isHidePingcang, isHideJiancang, isHideFangzhen);
    }

    public List<StockLog> queryAll(int line, boolean isHideHongbao,
                                   boolean isHidePingcang, boolean isHideJiancang, boolean isHideFangzhen) {
        Cursor cursor = null;
        cursor = db
                .query(TAB_NAME, null, null, null, null, null, "id" + " DESC");
        List<StockLog> list = new ArrayList<StockLog>();
        while (cursor.moveToNext() && line > 0) {
            StockLog stockLog = query(cursor, new StockLog());
            line--;
            if (isHideHongbao && stockLog.getMessage().contains("关注")) {
                continue;
            }
            if (isHidePingcang && stockLog.getMessage().contains("平仓")) {
                continue;
            }
            if (isHideJiancang && stockLog.getMessage().contains("建仓")) {
                continue;
            }
            if (isHideFangzhen && stockLog.getMessage().contains("仿真")) {
                continue;
            }
            list.add(stockLog);
        }
        cursor.close();
        return list;
    }

}
