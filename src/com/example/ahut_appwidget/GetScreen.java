package com.example.ahut_appwidget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;

public class GetScreen {
	
	public Bitmap myShot(Activity activity) {
		// 获取windows中最顶层的view
		View view = activity.getWindow().getDecorView();
		view.buildDrawingCache();
		// 获取状态栏高度
		Rect rect = new Rect();
		view.getWindowVisibleDisplayFrame(rect);
		int statusBarHeights = rect.top;
		Display display = activity.getWindowManager().getDefaultDisplay();
		// 获取屏幕宽和高
		int widths = display.getWidth();
		int heights = display.getHeight();
		// 允许当前窗口保存缓存信息
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		// 去掉状态栏
		Log.i("status", ""+statusBarHeights);
		Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(),0,150, 
				widths, heights - 150);
		// 销毁缓存信息
		view.destroyDrawingCache();
		return bmp;
	}

}
