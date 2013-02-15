package com.ahutlesson.android.view;

import com.ahutlesson.android.lesson.Lesson;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class ScheduleView extends View{
	public Schedule sch;

	public ScheduleView(Activity activity, Lesson[][] lessons) {
		super(activity);
		setLongClickable(true);
		sch = new Schedule(activity, this, lessons);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {

		sch.draw(canvas);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		sch.grid.markLesson(event.getX(), event.getY());
		ScheduleView.this.invalidate();
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return true;
	}
}