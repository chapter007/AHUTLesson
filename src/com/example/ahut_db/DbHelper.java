package com.example.ahut_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{

	private static final String DATABASE_NAME = "article.db";  
    private static final int DATABASE_VERSION = 1;  
      
    public DbHelper(Context context) {  
        //CursorFactory设置为null,使用默认值  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("dbm", "create?");
		 db.execSQL("CREATE TABLE IF NOT EXISTS articles" +  
	                "(id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 db.execSQL("ALTER TABLE articles ADD COLUMN other STRING");
	}

}
