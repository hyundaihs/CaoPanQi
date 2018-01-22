package com.dashuai.android.treasuremap.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dashuai.android.treasuremap.entity.Stock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StockDao extends SQliteDao<Stock> {


    public StockDao(SQLiteDatabase db) {
        super(db, Stock.TAB_NAME);
    }

    public StockDao(SQLiteDatabase db, String tabName) {
        super(db, tabName);
    }

//    public boolean isColletedSearch(Stock stock) {
//        return isColleted(stock.getCode());
//    }


    @Override
    public long add(Stock object) {
        if (isColleted(object.getCode())) {
            return 0;
        }
        return super.add(object);
    }

    public boolean isColleted(String code) {
        Cursor cursor = db.query(TAB_NAME, null, "code" + "=?",
                new String[]{code}, null, null, null);
        boolean flag = cursor.moveToFirst();
        cursor.close();
        return flag;
    }

    public void exchange(Stock dst, Stock src) {
        Stock tmp = (Stock) dst.clone();
        tmp.setId(src.getId());
        update(tmp);
        tmp = (Stock) src.clone();
        tmp.setId(dst.getId());
        update(tmp);
    }

    public void deleteByCodes(List<Stock> stocks) {
        for (int i = 0; i < stocks.size(); i++) {
            deleteByCode(stocks.get(i).getCode());
        }
    }

    public void deleteByCode(String code) {
        db.delete(TAB_NAME, "code" + "=?", new String[]{code});
    }


//    private ContentValues getCV(Stock stock) {
//        ContentValues cv = new ContentValues();
//        // 得到类对象
//        Class userCla = (Class) stock.getClass();
//        /*
//         * 得到类中的所有属性集合
//		 */
//        Field[] fs = userCla.getDeclaredFields();
//        for (int i = 0; i < fs.length; i++) {
//            Field f = fs[i];
//            f.setAccessible(true); // 设置些属性是可以访问的
//            Object val;
//            try {
//                val = f.get(stock);// 得到此属性的值
//                String name = f.getName();
//                if (name.equals("id") || name.equals("serialVersionUID") || name.equals("CREATOR")) {
//                    continue;
//                }
//                cv.put(name, String.valueOf(val));
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            }
//        }
//        return cv;
//    }

    public List<Stock> querySortById() {
        Cursor cursor = db.query(TAB_NAME, null, null, null, null, null, "id"
                + " DESC");
        List<Stock> list = new ArrayList<Stock>();
        while (cursor.moveToNext()) {
            list.add(query(cursor, new Stock()));
        }
        cursor.close();
        return list;
    }

}
