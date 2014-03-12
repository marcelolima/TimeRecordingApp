package com.myapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.myapp.util.Task;

public class DataBaseHandler extends SQLiteOpenHelper {
	private static DataBaseHandler instance = null;
	private static final String TAG = DataBaseHandler.class.getSimpleName();

	// Database config
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "TimeRecordingDB";

	// Table names
	private static final String TABLE_TASK = "task";
	private static final String TABLE_RECORD = "record";

	// Table Task column names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_DESC = "description";
	private static final String KEY_SSID = "ssid";
	private static final String KEY_BSSID = "bssid";

	// Table Record column names
	private static final String KEY_ID_TASK = "id_task";
	private static final String KEY_CHECKIN = "checkin";
	private static final String KEY_CHECKOUT = "checkout";

	// SQL statements to create the tables
	private static final String CREATE_TABLE_TASK = "CREATE TABLE " +
			TABLE_TASK + " (" +
			KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			KEY_NAME + " TEXT NOT NULL, " +
			KEY_DESC + " TEXT, " +
			KEY_SSID + " TEXT NOT NULL, " +
			KEY_BSSID + " TEXT NOT NULL" +
			");";
	private static final String CREATE_TABLE_RECORD = "CREATE TABLE " +
			TABLE_RECORD + " (" +
			KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			KEY_CHECKIN + " INTEGER NOT NULL, " +
			KEY_CHECKOUT + " INTEGER NOT NULL, " +
			KEY_ID_TASK + " INTEGER NOT NULL, " +
			"FOREIGN KEY (" + KEY_ID_TASK + ") REFERENCES "+
			TABLE_TASK + "(" + KEY_ID+ ")" +
			");";

	public DataBaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static DataBaseHandler getInstance(Context context) {
		if (instance == null)
			instance = new DataBaseHandler(context);

		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_TASK);
		db.execSQL(CREATE_TABLE_RECORD);

		Log.d(TAG, "TABLES CREATED");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);

		this.onCreate(db);
	}

	public long addTask(Task task) throws SQLException {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TASK + " WHERE name = \"" +
				task.getName() + "\"", null);

		if (c.getCount() > 0)
			return -1;

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, task.getName());
		values.put(KEY_DESC, task.getDescription());
		values.put(KEY_SSID, task.getSsid());
		values.put(KEY_BSSID, task.getBssid());

		long id = db.insert(TABLE_TASK, null, values);
		db.close();

		Log.d(TAG, "Inserted: " + task.toString());
		return id;
	}
}
