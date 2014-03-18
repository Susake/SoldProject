package db.dd.mockloc.model;

/**
 * Created by IntelliJ IDEA.
 * User: johnmay
 * Date: Nov 25, 2009
 * Time: 9:24:08 AM
 * To change this template use File | Settings | File Templates.
 */
import java.util.ArrayList;

import db.dd.mockloc.constaints.Constants;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataAdapter {
	private final static String LOG_TAG = "DataAdapter";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";
	public static final String KEY_TYPE = "type";
	private static final String DATABASE_NAME = "favorites";
	private static final String DATABASE_TABLE = "favorites";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE_FAVORITE = "create table "
			+ DATABASE_TABLE + " (_id integer primary key autoincrement, "
			+ KEY_NAME + " text not null, " + KEY_LAT + " text, " + KEY_LNG
			+ " text, " + KEY_TYPE + " text); ";

	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DataAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_FAVORITE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Content provider database",
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			// db.execSQL("DROP TABLE IF EXISTS titles");
			// onCreate(db);
		}
	}

	// ---opens the database---
	public DataAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		try {
			db.close();
			DBHelper.close();
		} catch (Exception e) {
			Log.e(LOG_TAG, "" + e.getMessage());
		}
	}

	// ---insert a title into the database---
	public long insertFav(DataModel fav) {
		open();
		long result = 0;
		if (!checkIfExists(fav)) {
			ContentValues initialValues = new ContentValues();

			initialValues.put(KEY_NAME, fav.name);
			initialValues.put(KEY_LAT, fav.lat);
			initialValues.put(KEY_LNG, fav.lng);
			initialValues.put(KEY_TYPE, fav.type);
			result = db.insert(DATABASE_TABLE, null, initialValues);
		} else {
			result = -1;
		}

		close();
		return result;
	}

	public boolean deleteFav(String rowId) {
		open();
		boolean result = db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId,
				null) > 0;
		close();
		return result;
	}

	public boolean deleteFavByName(DataModel dm) {
		open();
		boolean result = db.delete(DATABASE_TABLE, KEY_NAME + "='" + dm.name
				+ "' AND " + KEY_LAT + "='" + dm.lat + "' AND " + KEY_LNG
				+ "='" + dm.lng + "' AND " + KEY_TYPE + "='" + dm.type + "'",
				null) > 0;
		close();
		return result;
	}

	public boolean deleteAllFavs() {
		open();
		boolean result = db.delete(DATABASE_TABLE, KEY_ROWID + "> 0 AND "
				+ KEY_TYPE + "='" + Constants.TYPE_FAVORITE + "'", null) > 0;
		close();
		return result;
	}

	public boolean deleteAllNONFavs() {
		open();
		boolean result = db.delete(DATABASE_TABLE, KEY_ROWID + "> 0 AND "
				+ KEY_TYPE + "!='" + Constants.TYPE_FAVORITE + "'", null) > 0;
		close();
		return result;
	}

	public ArrayList<DataModel> getAllFavs() {
		ArrayList<DataModel> data = new ArrayList<DataModel>();
		open();

		try {
			Cursor c = db.query(DATABASE_TABLE, new String[] { KEY_ROWID,
					KEY_NAME, KEY_LAT, KEY_LNG, KEY_TYPE }, KEY_TYPE + "=?",
					new String[] { Constants.TYPE_FAVORITE }, null, null, null);

			if (c.moveToFirst()) {
				do {
					DataModel dm = new DataModel();
					dm.id = c.getString(0);
					dm.name = c.getString(1);
					dm.lat = c.getString(2);
					dm.lng = c.getString(3);
					dm.type = c.getString(4);
					data.add(dm);
				} while (c.moveToNext());
			}

			c.close();
		} catch (Exception e) {
		}
		close();
		return data;
	}

	public ArrayList<DataModel> getAllNONFavs() {
		ArrayList<DataModel> data = new ArrayList<DataModel>();
		open();

		try {
			Cursor c = db.query(DATABASE_TABLE, new String[] { KEY_ROWID,
					KEY_NAME, KEY_LAT, KEY_LNG, KEY_TYPE }, KEY_TYPE + "!=?",
					new String[] { Constants.TYPE_FAVORITE }, null, null, null);

			if (c.moveToFirst()) {
				do {
					DataModel dm = new DataModel();
					dm.id = c.getString(0);
					dm.name = c.getString(1);
					dm.lat = c.getString(2);
					dm.lng = c.getString(3);
					dm.type = c.getString(4);
					data.add(dm);
				} while (c.moveToNext());
			}

			c.close();
		} catch (Exception e) {
		}
		close();
		return data;
	}

	public boolean checkIfExists(DataModel dm) {
		boolean match = false;
		try {
			Cursor c = db.query(DATABASE_TABLE, null, KEY_NAME + "= ? And "
					+ KEY_LAT + "=? AND " + KEY_LNG + "=? AND " + KEY_TYPE
					+ "=? ", new String[] { dm.name, dm.lat, dm.lng, dm.type },
					null, null, null);

			if (c.moveToFirst()) {
				match = true;
			}
			c.close();
		} catch (Exception e) {
			match = false;
		}
		return match;
	}

}
