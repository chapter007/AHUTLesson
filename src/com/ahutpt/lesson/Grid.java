package com.ahutpt.lesson;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * �������ں�����
 * 
 * */
public class Grid extends ScheduleParent implements Serializable {

	private static final long serialVersionUID = 1L;
	private static Timetable timetable;
	private Canvas canvas;

	float top, left;
	float cellWidth, cellHeight;
	float calendarWidth, calendarHeight;
	float textLeft, textTop;
	
	public static final int NORMALTIME = 0;
	public static final int FREETIME = 1;
	public static final int BUSYTIME = 2;
	public static final int NEXTTIME = 3;
	
	public Grid(Activity activity, View view) {
		super(activity, view);
		timetable = new Timetable(context);
		left = borderMargin;
		top = borderMargin + weekNameSize + weekNameMargin * 2 + 4;
	}

	@Override
	public void draw(Canvas canvas0) {
		canvas = canvas0;

		calendarWidth = view.getMeasuredWidth() - left * 2;
		calendarHeight = view.getMeasuredHeight() - top - borderMargin;
		cellWidth = calendarWidth / 7;
		cellHeight = calendarHeight / 5;
		
		//����
		drawLines();
		
		//����γ�
		LessonManager mLessonManager = new LessonManager(context);
		Lesson[] lessons = mLessonManager.getAllLessons();
		if (lessons == null)
			return;
		
		//ȥ���Ľڿ��м����
		paint.setColor(Color.WHITE);
		for(int i = 0;lessons[i] != null;i++) {
			if(lessons[i].canAppend()){
				canvas.drawLine(left + cellWidth * lessons[i].week, top + cellHeight * (lessons[i].time + 1), left + cellWidth * (lessons[i].week + 1),
						top + cellHeight * (lessons[i].time + 1), paint);
			}
		}
		
		//������
		drawBackgrounds();
		
		paint.setAntiAlias(true);
		//�γ����͵ص�
		for(int i = 0;lessons[i] != null;i++) {
			if(lessons[i].canAppend()&&Timetable.nowIsAtLessonBreak(lessons[i].time)){
				drawLesson(lessons[i].week, lessons[i].time,
						lessons[i], true, lessons[i].appendMode());
			}else if(lessons[i].isAppended()&&Timetable.nowIsAtLessonBreak(lessons[i].time - 1)){
				//�ĽڿεĿμ����Ǻ����ڿΣ����û���
			}else{
				drawLesson(lessons[i].week, lessons[i].time,
						lessons[i], lessons[i].isNowHaving()==0?true:false, lessons[i].appendMode());
			}
		}
	}

	private void drawBackgrounds() {
		//������

		Lesson lesson = new Lesson(Timetable.weekDay,
				Timetable.getCurrentTimeBlock(Timetable.DelayDefault), context);
		
		//�������Ľڿομ�����
		Lesson tLesson = new Lesson(Timetable.weekDay, 0, context);
		if(tLesson.exist&&tLesson.canAppend()&&Timetable.nowIsAtLessonBreak(0)){
			drawBackground(tLesson.week, tLesson.time, BUSYTIME, tLesson.appendMode());
		}
		tLesson = new Lesson(Timetable.weekDay, 2, context);
		if(tLesson.exist&&tLesson.canAppend()&&Timetable.nowIsAtLessonBreak(2)){
			drawBackground(tLesson.week, tLesson.time, BUSYTIME, tLesson.appendMode());
		}
		
		if(lesson.exist) {
			drawBackground(lesson.week, lesson.time, BUSYTIME, lesson.appendMode());
		}else{
			int curTimeBlock = Timetable.getCurrentTimeBlock(Timetable.DelayDefault);
			if(curTimeBlock != -1){
				drawBackground(Timetable.weekDay, curTimeBlock, FREETIME, 0);
			}
		}

		lesson = timetable.getNextLesson(Timetable.DelayDefault);
		drawBackground(lesson.week, lesson.time, NEXTTIME, lesson.appendMode());

	}

	private void drawLines() {
		paint.setColor(Color.LTGRAY);

		//������
		for (int i = 0; i <= 5; i++) {
			canvas.drawLine(left, top + (cellHeight) * i, left + calendarWidth,
					top + (cellHeight) * i, paint);
		}
		//������
		for (int i = 0; i <= 7; i++) {
			canvas.drawLine(left + cellWidth * i, top, left + cellWidth * i,
					view.getMeasuredHeight() - borderMargin, paint);
		}

	}
	
	private void drawBackground(int week,int time, int mode, int append) {
		// ���½ڿα���
		// append:0 Ĭ�� 1 ��չ�½� -1 ��չ�Ͻ�
		Paint paintbg = new Paint();
		switch(mode){
		case NORMALTIME:
			paintbg.setColor(Color.WHITE);
			break;
		case FREETIME:
			paintbg.setColor(Color.parseColor("#228B22"));
			break;
		case BUSYTIME:
			paintbg.setColor(Color.parseColor("#B22222"));
			break;
		case NEXTTIME:
			paintbg.setColor(Color.parseColor("#CDCDCD"));
			break;
		}
		switch (append){
		case 0:
			canvas.drawRect(left + cellWidth * (week), top + cellHeight
					* time, left + cellWidth * (week + 1), top
					+ cellHeight * (time + 1), paintbg);
			break;
		case 1:
			canvas.drawRect(left + cellWidth * week, top
					+ cellHeight * time, left + cellWidth
					* (week + 1), top + cellHeight
					* (time + 2), paintbg);
			break;
		case -1:
			canvas.drawRect(left + cellWidth * week, top
					+ cellHeight * (time - 1), left + cellWidth
					* (week + 1), top + cellHeight
					* (time + 1), paintbg);
			break;
		}
	}

	private void drawLesson(int week, int time, Lesson lesson,boolean busytime,
			int appendMode) {
		// һ��γ�
		String name, place;
		name = lesson.alias;
		place = lesson.place;
		textLeft = left + cellWidth * week
				+ (cellWidth - paint.getTextSize() * 2) / 2;
		Lesson mlesson;
		switch(appendMode){
		case 0:
			textTop = top + cellHeight * time
			+ (cellHeight - paint.getTextSize() * 2 - 20) / 2;
			break;
		case 1:
			mlesson = new Lesson(week, time + 1, context);
			if(mlesson.isNowHaving()==0)return;
			textTop = top + cellHeight * time
			+ (cellHeight * 2 - paint.getTextSize() * 2 - 20) / 2;
			break;
		case -1:
			mlesson = new Lesson(week, time - 1, context);
			if(mlesson.isNowHaving()==0)return;
			textTop = top + cellHeight * (time - 1)
			+ (cellHeight * 2 - paint.getTextSize() * 2 - 20) / 2;
			break;
		}

		// ���γ���
		paint.setTextSize(lessonNameSize);
		if (busytime)
			paint.setColor(Color.WHITE);
		else
			paint.setColor(Color.BLACK);
		if (name.length() <= 2) {
			canvas.drawText(name, textLeft, textTop, paint);
		} else if (name.length() <= 4) {
			canvas.drawText(name.substring(0, 2), textLeft, textTop, paint);
			canvas.drawText(name.substring(2), textLeft,
					textTop + (paint.getTextSize() + 5), paint);
		} else {
			canvas.drawText(name.substring(0, 2), textLeft, textTop, paint);
			canvas.drawText(name.substring(2, 4), textLeft,
					textTop + (paint.getTextSize() + 5), paint);
			canvas.drawText("...", textLeft, textTop
					+ (paint.getTextSize() + 5) * 2, paint);
		}
		// ���ص�
		paint.setTextSize(lessonPlaceSize);
		if (busytime)
			paint.setColor(Color.WHITE);
		else
			paint.setColor(Color.parseColor("#B22222"));

		switch(appendMode){
		case 0:
			textTop = top + cellHeight * (time + 1) - 15;
			break;
		case 1:
			textTop = top + cellHeight * (time + 2) - cellHeight / 2 - 15;
			break;
		case -1:
			textTop = top + cellHeight * (time) + cellHeight / 2 - 15;
			break;
		}

		if (place.length() <= 4) {
			textLeft = left + cellWidth * week
					+ (cellWidth - paint.measureText(place)) / 2;
			canvas.drawText(place, textLeft, textTop, paint);
		} else if (place.length() <= 8) {
			textTop -= paint.getTextSize();
			String roomNumber = place.substring(place.length() - 3,
					place.length());
			if (isInt(roomNumber)) {
				textLeft = left
						+ cellWidth
						* week
						+ (cellWidth - paint.measureText(place.substring(0,
								place.length() - 3))) / 2;
				canvas.drawText(place.substring(0, place.length() - 3),
						textLeft, textTop, paint);
				textLeft = left + cellWidth * week
						+ (cellWidth - paint.measureText(roomNumber)) / 2;
				canvas.drawText(roomNumber, textLeft,
						textTop + paint.getTextSize(), paint);
			} else {
				textLeft = left
						+ cellWidth
						* week
						+ (cellWidth - paint.measureText(place.substring(0, 4)))
						/ 2;
				canvas.drawText(place.substring(0, 4), textLeft, textTop, paint);
				textLeft = left + cellWidth * week
						+ (cellWidth - paint.measureText(place.substring(4)))
						/ 2;
				canvas.drawText(place.substring(4), textLeft,
						textTop + paint.getTextSize(), paint);
			}
		}
	}

	private boolean isInt(String substring) {
		return substring.matches("\\d*");
	}

	public void openLessonDetail(float x, float y) {
		// �򿪿γ�����
		int week = (int) (x / cellWidth);
		int time = (int) ((y - top) / cellHeight);
		Intent i = new Intent(context, LessonActivity.class);
		i.putExtra("week", week);
		i.putExtra("time", time);
		context.startActivity(i);
	}
}
