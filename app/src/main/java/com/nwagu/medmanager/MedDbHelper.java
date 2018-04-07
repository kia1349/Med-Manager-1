package com.nwagu.medmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

class MedDbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "medbase.db";
	private static final int DATABASE_VERSION = 1;
	
	MedDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		final String SQL_CREATE_PILLS_TABLE = "CREATE TABLE " +
				MedContract.PillsEntry.TABLE_NAME + "(" +
				MedContract.PillsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				MedContract.PillsEntry.COLUMN_PILL_NAME + " TEXT, " +
				MedContract.PillsEntry.COLUMN_PILL_DESC + " TEXT, " +
				MedContract.PillsEntry.COLUMN_PILL_INTERVAL + " TEXT, " +
				MedContract.PillsEntry.COLUMN_PILL_START + " TEXT, " +
				MedContract.PillsEntry.COLUMN_PILL_END + " TEXT, " +
                MedContract.PillsEntry.COLUMN_PILL_LAST + " TEXT " +
				");";
				
		sqLiteDatabase.execSQL(SQL_CREATE_PILLS_TABLE);

	}
	
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MedContract.PillsEntry.TABLE_NAME);
		onCreate(sqLiteDatabase);
	}

	long addNewPill(SQLiteDatabase nDb, String name, String desc, String interval, String start, String end) {
		ContentValues cv = new ContentValues();
		SimpleDateFormat format1 = new SimpleDateFormat("d-M-yyyy hh:mm", Locale.US);
		SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
		cv.put(MedContract.PillsEntry.COLUMN_PILL_NAME, name);
		cv.put(MedContract.PillsEntry.COLUMN_PILL_DESC, desc);
        cv.put(MedContract.PillsEntry.COLUMN_PILL_INTERVAL, interval);
        cv.put(MedContract.PillsEntry.COLUMN_PILL_START, start);
        cv.put(MedContract.PillsEntry.COLUMN_PILL_END, end);
		try {
			cv.put(MedContract.PillsEntry.COLUMN_PILL_LAST, format.format(format1.parse(start))); //last taken == start date, at beginning of medication
		} catch (ParseException ignored) {

		}
		return nDb.insert(MedContract.PillsEntry.TABLE_NAME, null, cv);
	}
}
