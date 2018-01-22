package com.dashuai.android.treasuremap.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private static DBManager dbManager;
	private SQLiteDatabase db;
	private DatabaseHelper databaseHelper;

	private DBManager(Context context) {
		databaseHelper = new DatabaseHelper(context);
	}

	private DBManager(Context context, int version) {
		databaseHelper = new DatabaseHelper(context, version);
	}

	public static DBManager getInstance(Context context) {
		if (null == dbManager) {
			dbManager = new DBManager(context);
		}
		return dbManager;
	}

	public static DBManager getInstance(Context context, int version) {
		dbManager = new DBManager(context, version);
		return dbManager;
	}

	public DBManager openDatabase() {
		if (null == db) {
			db = databaseHelper.getWritableDatabase();
		}
		return dbManager;
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	public void closeDatabase() {
		if (null != db && db.isOpen()) {
			db.close();
		}
	}

//	public void cleanTables() {
//		if (null != databaseHelper && null != db) {
//			databaseHelper.deleteAllTables(db);
//		}
//	}

}
