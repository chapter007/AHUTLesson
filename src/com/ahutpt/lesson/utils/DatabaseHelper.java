package com.ahutpt.lesson.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	DatabaseHelper(Context context, String name, CursorFactory cursorFactory,
			int version) {
		super(context, name, cursorFactory, version);
	}
	
	public static final int dbVersion = 3;//���ݿ�汾
	
	public DatabaseHelper(Context baseContext, String name) {
		super(baseContext,name,null,dbVersion);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE lesson (_id INTEGER PRIMARY KEY AUTOINCREMENT, lessonname TEXT, lessonalias TEXT, teachername TEXT, lessonplace TEXT, startweek INTEGER, endweek INTEGER, homework TEXT, week INTEGER ,time INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    if (oldVersion == 1 && newVersion == 2) {
	        db.execSQL("ALTER TABLE lesson ADD COLUMN homework TEXT");
	    }
	    if (oldVersion == 2 && newVersion == 3) {
	        db.execSQL("ALTER TABLE lesson ADD COLUMN startweek INTEGER");
	        db.execSQL("ALTER TABLE lesson ADD COLUMN endweek INTEGER");
	    }
	    if (oldVersion == 1 && newVersion == 3) {
	    	db.execSQL("ALTER TABLE lesson ADD COLUMN homework TEXT");
	        db.execSQL("ALTER TABLE lesson ADD COLUMN startweek INTEGER");
	        db.execSQL("ALTER TABLE lesson ADD COLUMN endweek INTEGER");
	    }
	}

}