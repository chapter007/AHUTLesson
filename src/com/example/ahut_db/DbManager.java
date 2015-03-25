package com.example.ahut_db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbManager {
	private DbHelper helper;
	private SQLiteDatabase db;  
    
    public DbManager(Context context) {  
        helper = new DbHelper(context);
        Log.i("dbm", "create?");
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里  
        db = helper.getWritableDatabase();  
    }  
      
    /** 
     * add persons 
     * @param persons 
     */  
    public void add(List<articles> articles) {  
        db.beginTransaction();  //开始事务  
        try {  
            for (articles article : articles) {  
                db.execSQL("INSERT INTO articles VALUES(null, ?)", new Object[]{article.title});  
            }  
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {
            db.endTransaction();    //结束事务  
        }  
    }
    
    /**
     * 检测是否已经存在表
     */
      public boolean check() {
    	  int a = 0;
    	  Cursor c = queryTheCursor();  
          while (c.moveToNext()) {  
          	articles article = new articles();  
          	article.id = c.getInt(c.getColumnIndex("id"));  
          	article.title = c.getString(c.getColumnIndex("title"));
          	a=article.id;
          }  
          c.close();
          if (a!=0) {
    		return true;
		}else {
			return false;
		}
    	  
	}
    /** 
     * update person's age 
     * @param person 
     */  
    public void updateArticle(ArrayList<articles> articles) {
    	db.beginTransaction();  //开始事务
    	//从数据库里获取数据
    	ArrayList<articles> articles2 = new ArrayList<articles>();  
        Cursor c = queryTheCursor2();  
        articles article2 = new articles();  
        while (c.moveToNext()) {  
        	article2.id = c.getInt(c.getColumnIndex("id"));  
        	articles2.add(article2);  
        }  
        c.close();
    	ContentValues cv=new ContentValues();
        try { 
        	int i=0;
            for (articles article : articles) {
            	Log.i("id", "article.id:"+article.id+"	title:"+article.title);
            	Log.i("", ""+article2.id);
            	if (i==0) {
            		cv.put("title", article.title);
                	String where="id"+"="+article2.id;
                	db.update("articles", cv, where, null);
                	i++;
				}
            	
            }
            db.setTransactionSuccessful();  //设置事务成功完成  
        } finally {
            db.endTransaction();    //结束事务  
        }  
    }
      
    /** 
     * delete old article 
     * @param person 
     */  
    public void deleteOldPerson(articles article) {  
        db.delete("articles", "id >= ?", new String[]{String.valueOf(article.id)});  
    }  
      
    /** 
     * query all persons, return list 
     * @return List<Person> 
     */  
    public List<articles> query() {  
        ArrayList<articles> articles = new ArrayList<articles>();  
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
        	articles article = new articles();  
        	article.id = c.getInt(c.getColumnIndex("id"));  
        	article.title = c.getString(c.getColumnIndex("title"));  
        	articles.add(article);  
        }  
        c.close();  
        return articles;  
    }  
      
    /** 
     * query all persons, return cursor 
     * @return  Cursor 
     */  
    public Cursor queryTheCursor() {  
        Cursor c = db.rawQuery("SELECT * FROM articles", null);  
        return c;  
    }
    
    public Cursor queryTheCursor2() {  
        Cursor c2 = db.rawQuery("SELECT id FROM articles limit 0,1", null);  
        return c2;  
    }
      
    /** 
     * close database 
     */  
    public void closeDB() {  
        db.close();  
    }
}
