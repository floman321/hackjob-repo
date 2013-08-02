package com.example.omnishare;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBController extends SQLiteOpenHelper
{
	private static final String LOGCAT = null;

	public DBController(Context applicationcontext)
	{
		super(applicationcontext, "androidsqlite.db", null, 2);
		Log.d(LOGCAT, "Created");
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		String query;
		query = "CREATE TABLE meeting ( meetingId INTEGER PRIMARY KEY, meetingName TEXT, meetingLocation TEXT, meetingDate TEXT, meetingCode TEXT)";
		database.execSQL(query);
		// add other table and foreign key
		Log.d(LOGCAT, "Meeting Table Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int version_old,
			int current_version)
	{
		String query;
		query = "DROP TABLE IF EXISTS meeting";
		database.execSQL(query);
		onCreate(database);
	}

	public void insertMeeting(HashMap<String, String> queryValues)
	{
		SQLiteDatabase database = this.getWritableDatabase();// db created here
		ContentValues values = new ContentValues();
		values.put("meetingName", queryValues.get("meetingName"));
		values.put("meetingLocation", queryValues.get("meetingLocation"));
		values.put("meetingDate", queryValues.get("meetingDate"));
		values.put("meetingCode", queryValues.get("meetingCode"));

		database.insert("meeting", null, values);
		database.close();
	}

	public void updateMeeting(HashMap<String, String> queryValues)
	{
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("meetingName", queryValues.get("meetingName"));
		values.put("meetingLocation", queryValues.get("meetingLocation"));
		values.put("meetingDate", queryValues.get("meetingDate"));
		values.put("meetingCode", queryValues.get("meetingCode"));

		database.update("meeting", values, "meetingId" + " = ?", new String[] { queryValues.get("meetingId") });
		database.close();
		// String updateQuery =
		// "Update  words set txtWord='"+word+"' where txtWord='"+ oldWord +"'";
		// Log.d(LOGCAT,updateQuery);
		// database.rawQuery(updateQuery, null);
		// return database.update("words", values, "txtWord  = ?", new String[]
		// { word });
	}

	public void deleteMeeting(String id)
	{
		Log.d(LOGCAT, "deleted meeting " + id);
		SQLiteDatabase database = this.getWritableDatabase();
		String deleteQuery = "DELETE FROM meeting where meetingId='" + id + "'";
		Log.d("query", deleteQuery);
		database.execSQL(deleteQuery);
	}

	// returns all the meetings
	public ArrayList<HashMap<String, String>> getAllMeetings()
	{
		ArrayList<HashMap<String, String>> wordList;
		wordList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM meeting";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst())
		{
			do
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("meetingId", cursor.getString(0));
				map.put("meetingName", cursor.getString(1));
				map.put("meetingLocation", cursor.getString(2));
				map.put("meetingDate", cursor.getString(3));
				map.put("meetingCode", cursor.getString(4));
				wordList.add(map);
			}
			while (cursor.moveToNext());
		}

		// return contact list
		return wordList;
	}

	// returns all the info for a meeting
	public HashMap<String, String> getMeetingInfo(String id)
	{
		HashMap<String, String> wordList = new HashMap<String, String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM meeting where meetingId='" + id
				+ "'";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst())
		{
			do
			{
			//	HashMap<String, String> map = new HashMap<String, String>();
				wordList.put("meetingId", cursor.getString(0));
				wordList.put("meetingName", cursor.getString(1));
				wordList.put("meetingLocation", cursor.getString(2));
				wordList.put("meetingDate", cursor.getString(3));
				wordList.put("meetingCode", cursor.getString(4));
				//wordList.add(map);
			}
			while (cursor.moveToNext());
		}
		return wordList;
	}
}
