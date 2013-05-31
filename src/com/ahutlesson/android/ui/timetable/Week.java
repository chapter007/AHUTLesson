package com.ahutlesson.android.ui.timetable;

import com.ahutlesson.android.R;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

/**
 * ����������
 * 
 * */
public class Week extends ScheduleParent {
	private String[] weekNames;

	public Week(Activity activity, View view) {
		super(activity, view);
		weekNames = activity.getResources().getStringArray(R.array.week_name);
		// �������������ֵĴ�С
		paint.setTextSize(weekNameSize);
		paint.setAntiAlias(true);
	}

	@Override
	public void draw(Canvas canvas) {

		float left = borderMargin;
		float top = borderMargin;
		paint.setColor(Color.LTGRAY);
		canvas.drawLine(0, top, canvas.getWidth(), top, paint);
		
		float everyWeekWidth = (view.getMeasuredWidth() - borderMargin * 2) / 7;
		for (int i = 0; i < 7; i++) {
			if (i == 5 || i == 6)
				// ��������,�յ���ɫ�������ط��õ�
				paint.setColor(Color.parseColor("#D2691E"));
			else
				paint.setColor(Color.BLACK);

			left = borderMargin + everyWeekWidth * i
					+ (everyWeekWidth - paint.measureText(weekNames[i])) / 2;
			// ��ʼ������������
			canvas.drawText(weekNames[i], left, top + paint.getTextSize()
					+ weekNameMargin, paint);
		}

	}

}
