package com.dashuai.android.treasuremap.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dashuai.android.treasuremap.entity.Record;
import com.dashuai.android.treasuremap.entity.Score;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 17/7/29.
 */
public class RecordDao extends SQliteDao<Record> {

    public RecordDao(SQLiteDatabase db) {
        super(db, Record.TAB_NAME);
    }

    public List<Record> querysByFangzhenId(String fangzhenId) {
        //"id" + " DESC"
        Cursor cursor = db.query(TAB_NAME, null, " fangzhenId = ? ", new String[]{fangzhenId}, null, null, null);
        List<Record> list = new ArrayList<Record>();
        while (cursor.moveToNext()) {
            Record record = query(cursor, new Record());
            list.add(record);
        }
        cursor.close();
        return list;
    }
}
