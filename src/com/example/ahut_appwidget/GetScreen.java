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
		// ��ȡwindows������view
		View view = activity.getWindow().getDecorView();
		view.buildDrawingCache();
		// ��ȡ״̬���߶�
		Rect rect = new Rect();
		view.getWindowVisibleDisplayFrame(rect);
		int statusBarHeights = rect.top;
		Display display = activity.getWindowManager().getDefaultDisplay();
		// ��ȡ��Ļ��͸�
		int widths = display.getWidth();
		int heights = display.getHeight();
		// ����ǰ���ڱ��滺����Ϣ
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		// ȥ��״̬��
		Log.i("status", ""+statusBarHeights);
		Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(),0,150, 
				widths, heights - 150);
		// ���ٻ�����Ϣ
		view.destroyDrawingCache();
		return bmp;
	}

}
