package com.dashuai.android.treasuremap.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dashuai.android.treasuremap.entity.Score;

import java.util.ArrayList;
import java.util.List;

public class ScoreDao extends SQliteDao<Score> {

    public ScoreDao(SQLiteDatabase db) {
        super(db, Score.TAB_NAME);
    }

    public List<Score> querySortById() {
        Cursor cursor = db.query(TAB_NAME, null, null, null, null, null, "id" + " DESC");
        List<Score> list = new ArrayList<Score>();
        while (cursor.moveToNext()) {
            Score score = query(cursor, new Score());
            list.add(score);
        }
        cursor.close();
        return list;
    }

}