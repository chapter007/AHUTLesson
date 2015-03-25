package com.example.ahut_appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.ahut.MainActivity;
import com.example.ahut.R;

public class AppWidget extends AppWidgetProvider{
	
	public void onDeleted(Context context, int[] appWidgetIds) {
        System.out.println("appwidget--->onDeleted()");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        System.out.println("appwidget--->onDisabled()");
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        System.out.println("appwidget--->onEnabled()");
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    	String action=intent.getAction();
        System.out.println("appwidget--->onReceive()");
        super.onReceive(context, intent);
        if (action.equals("com.example.ahut_appwidget.COLLECTION_VIEW_ACTION")) {
			Toast.makeText(context, "test",2000);
		}
        
    } 

    public static String COLLECTION_VIEW_ACTION="com.example.ahut_appwidget.COLLECTION_VIEW_ACTION";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        System.out.println("appwidget--->onUpdate()");
        for (int i = 0; i < appWidgetIds.length; i++) {
			int appwidgetId=appWidgetIds[i];
			RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.ahut_appwidget);
			//设置图片
			Bitmap bitmap=BitmapFactory.decodeFile(context.getFilesDir()+".png");
			remoteViews.setImageViewBitmap(R.id.myWidget, bitmap);
			//设置点击事件
			Intent intent=new Intent(context, MainActivity.class);
			PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
			
			appWidgetManager.updateAppWidget(appwidgetId, remoteViews);	
		}
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        
        
    }
}
