package com.myapp.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.myapp.util.Record;
import com.myapp.util.Task;
import com.myapp.util.Wifi;

public class DataBaseHandler extends SQLiteOpenHelper {
	private static DataBaseHandler instance = null;
	private static final String TAG = DataBaseHandler.class.getSimpleName();
	private static final long UNDEF = 0;
	private static final Long DELAY_BETWEEN_CHECKS = (long) 60000;

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

	public ArrayList<Task> getAllTasks() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT " + KEY_ID + ", " + KEY_NAME +
				" FROM " + TABLE_TASK + ";" , null);

		if (c.getCount() == 0)
			return null;

		int columnId = c.getColumnIndex(KEY_ID);
		int columnName = c.getColumnIndex(KEY_NAME);

		ArrayList<Task> tasksList = new ArrayList<Task>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			tasksList.add(new Task(c.getInt(columnId), c.getString(columnName)));

		db.close();
		return tasksList;
	}

	public void addCheckIn(Wifi wifi) {
		SQLiteDatabase db = this.getWritableDatabase();

		// Get id from tasks which ssid and bssid matches
		Cursor c = db.rawQuery("SELECT " + KEY_ID +
				" FROM " + TABLE_TASK +
				" WHERE " + KEY_SSID + " = \"" + wifi.getSsid() +
				"\" AND " + KEY_BSSID + " = \"" + wifi.getBssid() + "\"" , null);

		if (c.getCount() == 0)
			return;

		// Get current time from Android
		Long checkIn = System.currentTimeMillis();

		ArrayList<Integer> tasksId = new ArrayList<Integer>();
		int idColumn = c.getColumnIndex(KEY_ID);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			tasksId.add(c.getInt(idColumn));

		// Insert a new row for each task ID
		for (int taskId : tasksId) {
			c = db.rawQuery("SELECT " + KEY_ID + ", " + KEY_CHECKOUT +
					" FROM " + TABLE_RECORD +
					" WHERE " + KEY_ID_TASK + " = " +  Integer.toString(taskId) +
					" ORDER BY " + KEY_CHECKIN + " DESC LIMIT 1"
					, null);

			int checkOutColumn = c.getColumnIndex(KEY_CHECKOUT);
			int idRecColumn = c.getColumnIndex(KEY_ID);

			if (c.moveToFirst() && (checkIn - c.getLong(checkOutColumn) <= DELAY_BETWEEN_CHECKS)) {
				db.execSQL("UPDATE " + TABLE_RECORD +
						" SET " + KEY_CHECKOUT + " = " + Long.toString(UNDEF) +
						" WHERE " + KEY_ID + " = " + Integer.toString(c.getInt(idRecColumn)) +
						";");
				Log.d(TAG, "Merged last checkout with current checkin on task " + Integer.toString(taskId));
			} else {
				ContentValues values = new ContentValues();
				values.put(KEY_CHECKIN, checkIn);
				values.put(KEY_CHECKOUT, Long.toString(UNDEF));
				values.put(KEY_ID_TASK, taskId);
				db.insert(TABLE_RECORD, null, values);

				Log.d(TAG, "INSERTED NEW RECORD " + Integer.toString(taskId) + " " + checkIn, null);
			}
		}
		db.close();
	}

	public void addCheckOut(Wifi wifi) {
		SQLiteDatabase db = this.getWritableDatabase();

		// Get id from tasks which ssid and bssid matches
		Cursor c = db.rawQuery("SELECT " + KEY_ID +
				" FROM " + TABLE_TASK +
				" WHERE " + KEY_SSID + " = \"" + wifi.getSsid() +
				"\" AND " + KEY_BSSID + " = \"" + wifi.getBssid() + "\"" , null);

		if (c.getCount() == 0)
			return;

		// Get current time from Android
		Long checkOut = System.currentTimeMillis();

		ArrayList<Integer> tasksId = new ArrayList<Integer>();
		int idColumn = c.getColumnIndex(KEY_ID);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			tasksId.add(c.getInt(idColumn));

		// For each task, update the last record row with checkout = 0
		for (int taskId : tasksId) {
			c = db.rawQuery("SELECT " + KEY_ID + ", " + KEY_CHECKOUT +
					" FROM " + TABLE_RECORD +
					" WHERE " + KEY_ID_TASK + " = " +  Integer.toString(taskId) +
					" ORDER BY " + KEY_CHECKIN + " DESC LIMIT 1"
					, null);

			// Continue to next loop iteration if the checkout value is already setted
			// or the query returned nothing
			c.moveToFirst();
			if (c.getLong(c.getColumnIndex(KEY_CHECKOUT)) != 0 || c.getCount() == 0)
				continue;

			int recIdToUpdate = c.getInt(c.getColumnIndex(KEY_ID));
			//	TODO: verify returns
			db.execSQL("UPDATE " + TABLE_RECORD +
					" SET " + KEY_CHECKOUT + " = \"" + checkOut +
					"\" WHERE " + KEY_ID + " = " + Integer.toString(recIdToUpdate) + ";");

			Log.d(TAG, "UPDATED " + Integer.toString(taskId) + " with checkout " + checkOut, null);
		}
		db.close();
	}

	public ArrayList<Record> getRecords(int taskId) throws SQLException {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT " + KEY_CHECKIN + ", " + KEY_CHECKOUT +
				" FROM " + TABLE_RECORD +
				" WHERE " + KEY_ID_TASK + " = " + Integer.toString(taskId),
				null);

		if (c.getCount() == 0)
			return null;

		ArrayList<Record> records = new ArrayList<Record>();
		int checkinColumnId = c.getColumnIndex(KEY_CHECKIN);
		int checkoutColumnId = c.getColumnIndex(KEY_CHECKOUT);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
			records.add(new Record(taskId, c.getLong(checkinColumnId), c.getLong(checkoutColumnId)));

		return records;
	}

	public void clearAllData() {
		onUpgrade(this.getWritableDatabase(), 0, 1);

		Log.d(TAG, "Database tables cleared");
	}
}
