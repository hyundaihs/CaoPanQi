package com.dashuai.android.treasuremap.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dashuai.android.treasuremap.entity.BZListStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 17/4/16.
 */
public class BZListStatusDao extends SQliteDao<BZListStatus> {

    public BZListStatusDao(SQLiteDatabase db) {
        super(db, BZListStatus.TAB_NAME);
    }

    public boolean isEmpty() {
        List<BZListStatus> list = querys(new BZListStatus());
        return list.isEmpty();
    }

    public List<BZListStatus> querySortByFlag() {
        Cursor cursor = db.query(TAB_NAME, null, null, null, null, null, "flag");
        List<BZListStatus> list = new ArrayList<BZListStatus>();
        while (cursor.moveToNext()) {
            BZListStatus score = query(cursor, new BZListStatus());
            list.add(score);
        }
        cursor.close();
        return list;
    }

    public int update(BZListStatus bzListStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("flag", bzListStatus.getFlag());
        contentValues.put("isOpen", bzListStatus.isOpen());
        int i = db.update(TAB_NAME, contentValues, " id = ?", new String[]{String.valueOf(bzListStatus.getId())});
        return i;
    }

    public boolean isExits(BZListStatus bzListStatus) {
        Cursor cursor = db.query(TAB_NAME, null, "name =?", new String[]{bzListStatus.getName()}, null, null, null);
        boolean result = cursor.moveToFirst();
        return result;
    }

//    @Override
//    public void adds(List<BZListStatus> objs) {
//        for (int i = 0; i < objs.size(); i++) {
//            addIfnotExits(objs.get(i));
//        }
//        notifyChange();
//    }

    public long addIfnotExits(BZListStatus obj) {
        if (isExits(obj)) {
            return 0;
        }
        return super.add(obj);
    }

}
