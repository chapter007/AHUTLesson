package com.example.ahut_view;

import com.example.ahut.R;
import com.example.ahut_user.LessonManager;

import android.R.color;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;

public class GridView extends View{
	
		
		private Context context;
		private Lesson[][] lessons;
		private Timetable timetable;

		private static float weekNameSize;
		private static float weekNameMargin;
		private static float lessonNameSize;
		private static float lessonNameMargin;
		private static float lessonPlaceSize;
		private static float lessonNamePlaceGap;
		private static float timePartitionGap;
		
		private int lessonNameMaxLines = 3; 
		private int lessonNameMaxLength = 2;// ÿ��
		private int lessonPlaceMaxLength = 4;
		private int lessonPlaceMaxLines = 2; 
		
		private int markX = -1, markY = -1;
		
		public static final int BLUE = Color.parseColor("#1487d2");
		public static final int RED = Color.parseColor("#B22222");
		public static final int BLACK = Color.parseColor("#333333");
		public static final int WHITE = Color.parseColor("#ffffff");
		public static final int LIGHTBLUE = Color.parseColor("#a0c3ff");

		private static float lessonInfoSize;
		private static float lessonInfoPadding;
		private static float lessonInfoPaddingLeft;
		private static Drawable lessonInfoBackground;
		
		private GridPosition lessonPosition;
		private Lesson lesson;
		private int lessonInfoX, lessonInfoY;
		private Rect lessonInfoBorder = new Rect();
		private boolean showLessonInfo = false;
		private boolean lessonInfoIsShowing = false;
		private boolean showLessonBackgroundIfNoLesson;
		
		//if lessons is null, load local
		public GridView(Context _context, Lesson[][] _lessons) {
			super(_context);
			context = _context;
			lessons = _lessons;
			if (lessons == null) {
				lessons = LessonManager.getInstance(context).getLessons();
			}
			timetable = Timetable.getInstance(context);

			this.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showLessonInfo();
				}
			});
		}

		public GridPosition getCurrentGridPosition() {
			return calcGridPosition();
		}
		
		public void updateLessonPosition(boolean showLessonBackgroundIfNoLesson) {
			this.showLessonBackgroundIfNoLesson = showLessonBackgroundIfNoLesson;
			lessonPosition = calcGridPosition();
			showLessonInfo = false;
			invalidate();
		}
		
		public void refreshView() {
			lessons = LessonManager.getInstance(context).getLessons();
			showLessonInfo = false;
			lessonPosition = null;
			lesson = null;
			invalidate();
			lessonInfoIsShowing = false;
		}
		
		private TextPaint textPaint = new TextPaint();
		private Canvas canvas;
		private float top, weekNameHeight;
		private float viewWidth, viewHeight, calendarWidth, calendarHeight, cellWidth, cellHeight;
		private float leftBorder[] = new float[8];
		private float topBorder[] = new float[6];
		private float bottomBorder[] = new float[6];
		private Paint linePaint;

		@Override
		protected void onDraw(Canvas _canvas) {
			canvas = _canvas;
			init();
			drawWeekNames();
			drawLines();
			drawLessonBackground();
			drawLesson();
			drawLessonInfo();
		}

		private void init() {
			textPaint.setAntiAlias(true);
			
			weekNameSize = context.getResources().getDimension(R.dimen.weekname_size);
			weekNameMargin = context.getResources().getDimension(R.dimen.weekname_margin);
			lessonNameSize = context.getResources().getDimension(R.dimen.lessonname_size);
			lessonPlaceSize = context.getResources().getDimension(R.dimen.lessonplace_size);
			lessonInfoSize = context.getResources().getDimension(R.dimen.lessoninfo_size);
			lessonInfoPadding = context.getResources().getDimension(R.dimen.lessoninfo_padding);
			lessonInfoPaddingLeft = context.getResources().getDimension(R.dimen.lessoninfo_padding_left);
			
	        viewWidth = this.getMeasuredWidth();
	        viewHeight = this.getMeasuredHeight();

			calendarWidth = viewWidth;
			cellWidth = calendarWidth / 7;
			lessonNameMargin = cellWidth / 15;
			weekNameHeight = weekNameSize + weekNameMargin * 2 + 5;

	        top = weekNameHeight;
	        
			calendarHeight = viewHeight - weekNameHeight;
			timePartitionGap = calendarHeight / 40;
			cellHeight = (calendarHeight - timePartitionGap * 2) / 5;
			lessonNamePlaceGap = cellHeight / 40;

			lessonNameMaxLength = (int) ((cellWidth - lessonNameMargin) / (float) lessonNameSize);
			lessonPlaceMaxLength = (int) (cellWidth / (float) lessonPlaceSize);
			lessonNameMaxLines = (int) ((cellHeight - lessonPlaceMaxLength * 2 - lessonNamePlaceGap) / lessonNameSize);
			
			for (int i = 0; i < 8; i++) {
				leftBorder[i] = cellWidth * i;
			}
			for (int i = 0; i < 6; i++) {
				topBorder[i] = top + cellHeight * i;
				if (i > 1) topBorder[i] += timePartitionGap;
				if (i > 3) topBorder[i] += timePartitionGap;
			}
			for (int i = 0; i < 6; i++) {
				bottomBorder[i] = top + cellHeight * (i + 1);
				if (i > 1) bottomBorder[i] += timePartitionGap;
				if (i > 3) bottomBorder[i] += timePartitionGap;
			}
		}
		
		private void drawWeekNames() {
			textPaint.setColor(Color.WHITE);
			textPaint.setTextSize(weekNameSize);
			Paint paintbg = new Paint();
			paintbg.setColor(BLUE);
			String[] weekNames = context.getResources().getStringArray(R.array.week_name);
			canvas.drawRect(0, 0, canvas.getWidth(), weekNameHeight, paintbg);
			
			float weekNameXOffset = (cellWidth - textPaint.measureText(weekNames[0])) / 2; //����������
			float weekNameYOffset = weekNameSize + weekNameMargin;
			for (int i = 0; i < 7; i++) {
				canvas.drawText(weekNames[i], leftBorder[i] + weekNameXOffset, weekNameYOffset, textPaint);
			}
		}

		private void drawLines() {
			linePaint = new Paint();
			linePaint.setARGB(10, 0, 0, 0);
			linePaint.setStyle(Style.FILL);
			linePaint.setPathEffect(new DashPathEffect(new float[] {5,10}, 0));
			
			//������
			for (int i = 1; i < 5; i++) {
				canvas.drawLine(0, topBorder[i], calendarWidth, topBorder[i], linePaint);
			}
			canvas.drawLine(0, bottomBorder[1], calendarWidth, bottomBorder[1], linePaint);
			canvas.drawLine(0, bottomBorder[3], calendarWidth, bottomBorder[3], linePaint);

			//������
			for (int i = 1; i < 7; i++) {
				canvas.drawLine(leftBorder[i], top, leftBorder[i], bottomBorder[1], linePaint);
			}
			for (int i = 1; i < 7; i++) {
				canvas.drawLine(leftBorder[i], topBorder[2], leftBorder[i], bottomBorder[3], linePaint);
			}
			for (int i = 1; i < 7; i++) {
				canvas.drawLine(leftBorder[i], topBorder[4], leftBorder[i], bottomBorder[4], linePaint);
			}
		}
		
		private void drawLessonBackground() {
			if (lessonPosition == null) return;
			if (!showLessonBackgroundIfNoLesson && !lessonPosition.hasLesson) return;

			Paint paintbg = new Paint();
			paintbg.setColor(LIGHTBLUE);
			canvas.drawRect(leftBorder[lessonPosition.week] + 1, topBorder[lessonPosition.time] + 1, leftBorder[lessonPosition.week + 1] - 1, bottomBorder[lessonPosition.time] - 1, paintbg);
			if (lesson != null) {
				if (lessonCanAppend(lesson)) {
					canvas.drawRect(leftBorder[lessonPosition.week] + 1, topBorder[lessonPosition.time + 1] - 2, leftBorder[lessonPosition.week + 1] - 1, bottomBorder[lessonPosition.time + 1] - 1, paintbg);
				} else if (lessonIsAppended(lesson)) {
					canvas.drawRect(leftBorder[lessonPosition.week] + 1, topBorder[lessonPosition.time - 1] + 1, leftBorder[lessonPosition.week + 1] - 1, bottomBorder[lessonPosition.time - 1] + 2, paintbg);
				}
			}
		}
		
		private void drawLesson() {
			for (Lesson[] lessonsOfDay : lessons) {
				if (lessonsOfDay != null) {
					for (Lesson lesson:lessonsOfDay) {
						if (lesson != null) {
							drawSingleLesson(lesson);
						}
					}
				}
			}
		}

		private float textLeft, textTop,textRight;
		
		
		private void drawSingleLesson(Lesson lesson) {
			// һ��γ�
			if(lesson == null) return;
			textPaint.setTextSize(lessonNameSize);
			int week,time;
			int appendMode = lessonAppendMode(lesson);
			if(appendMode == -1){
				return;//�����ڿβ���
			}
			
			week = lesson.week;
			time = lesson.time;
			String place;
			LessonName name = new LessonName(lesson.name);
			place = lesson.place;
			float cellHeight = this.cellHeight;
			int lessonNameMaxLines = this.lessonNameMaxLines;

			if (appendMode == 1) {
				Lesson nextLesson = (!Timetable.isValidWeekTime(week, time)) ? null : lessons[week][time + 1];
				if(nextLesson == null) return;
				if(timetable.isNowHavingLesson(nextLesson) == 0) return;
				cellHeight *= 2;
				lessonNameMaxLines = (int) ((cellHeight - lessonPlaceMaxLength * 2 - lessonNamePlaceGap) / lessonNameSize);
				if (lessonPosition == null || (!lesson.atPosition(lessonPosition) && !nextLesson.atPosition(lessonPosition))) {
					Paint bgPaint = new Paint();
					bgPaint.setColor(color.white);
					canvas.drawLine(leftBorder[week], bottomBorder[time], leftBorder[week + 1], bottomBorder[time], bgPaint);
				}
			}
			
			float length = name.length();
			int lines = (int) ((length - 1) / (float) lessonNameMaxLength) + 1;
			if (lines > lessonNameMaxLines) lines = lessonNameMaxLines;
			int lessonNameMaxLength = this.lessonNameMaxLength;
			if (lessonNameMaxLength == 3 && length >= 4 && length <= 5) {
				lines = 2;
				lessonNameMaxLength = 2;
			}

			int placeLines = 2;
			boolean isRoomNumber = false;
			if (place.length() <= lessonPlaceMaxLength) {
				placeLines = 1;
			} else if (place.length() <= lessonPlaceMaxLength * lessonPlaceMaxLines) {
				if (isInt(place.substring(place.length() - 3, place.length()))) {
					isRoomNumber = true;
					placeLines = 2;

				} else {
					placeLines = lessonPlaceMaxLines;
				}
			}
			
			//���γ���
			textTop = topBorder[time] + (cellHeight - lessonPlaceSize * placeLines - lessonNamePlaceGap + lessonNameSize * lines) / 2; //�γ������һ�еĵײ�Y
			
			if (!lesson.isBeforeEnd(timetable.numOfWeek)) {
				textPaint.setARGB(30, 0, 0, 0);
			} else {
				textPaint.setColor(BLACK);
			}
			
			String text;
			for (int i = 0; i < lines; i++) {
				if (name.length() <= lessonNameMaxLength) {
					text = name.toString();
				} else {
					text = name.substring(0, lessonNameMaxLength);
					name = new LessonName(name.substring(lessonNameMaxLength));
				}
				textLeft = leftBorder[week] + (cellWidth - textPaint.measureText(text)) / 2;
				textRight=textTop - lessonNameSize * (lines - i - 1);
				canvas.drawText(text, textLeft, textRight, textPaint);
			}


			// ���ص�
			textPaint.setTextSize(lessonPlaceSize);
			if(!lesson.isBeforeEnd(timetable.numOfWeek)){
				textPaint.setARGB(30, 0, 0, 0);
			}else{
				textPaint.setColor(Color.parseColor("#B22222"));
			}

			textTop += lessonNamePlaceGap + lessonPlaceSize;
			
			if (place.length() <= lessonPlaceMaxLength) {
				textLeft = leftBorder[week] + (cellWidth - textPaint.measureText(place)) / 2;
				canvas.drawText(place, textLeft, textTop, textPaint);
			} else if (place.length() <= lessonPlaceMaxLength * lessonPlaceMaxLines) {
				if (isRoomNumber) {
					String building = place.substring(0, place.length() - 3);
					String roomNumber = place.substring(place.length() - 3, place.length());
					if (building.length() > lessonPlaceMaxLength)
						building = building.substring(0, lessonPlaceMaxLength);
					textLeft = leftBorder[week] + (cellWidth - textPaint.measureText(building)) / 2;
					canvas.drawText(building, textLeft, textTop, textPaint);
					textLeft = leftBorder[week] + (cellWidth - textPaint.measureText(roomNumber)) / 2;
					canvas.drawText(roomNumber, textLeft, textTop + lessonPlaceSize, textPaint);
				} else {
					text = place.substring(0, lessonPlaceMaxLength);
					textLeft = leftBorder[week] + (cellWidth - textPaint.measureText(text)) / 2;
					canvas.drawText(text, textLeft, textTop, textPaint);
					text = place.substring(lessonPlaceMaxLength);
					if (text.length() > lessonPlaceMaxLength)
						text = text.substring(0, lessonPlaceMaxLength);
					textLeft = leftBorder[week] + (cellWidth - textPaint.measureText(text)) / 2;
					canvas.drawText(text, textLeft, textTop + lessonPlaceSize, textPaint);
				}
			}
		}

		private boolean isInt(String str) {
			for(int i = 0;i < str.length(); i++){
				if(!Character.isDigit(str.charAt(i))){
					return false;
				}
			}
			return true;
		}
		
		public boolean lessonCanAppend(Lesson lesson) {
			// �������п�
			if (lesson.time == 0 || lesson.time == 2) {
				Lesson appendLesson =  lessons[lesson.week][lesson.time + 1];
				if (appendLesson != null) {
					if (appendLesson.name.contentEquals(lesson.name)) {
						return true;
					}
				}
			}
			return false;
		}

		public boolean lessonIsAppended(Lesson lesson) {
			// ǰ�����п�
			if (lesson.time == 1 || lesson.time == 3) {
				Lesson appendLesson = lessons[lesson.week][lesson.time - 1];
				if (appendLesson!=null) {
					if (appendLesson.name.contentEquals(lesson.name)) {
						return true;
					}
				}
			}
			return false;
		}
		
		public int lessonAppendMode(Lesson lesson){
			if(lessonCanAppend(lesson)) return 1;
			if(lessonIsAppended(lesson)) return -1;
			return 0;
		}

		private void drawLessonInfo() {
			if (!showLessonInfo) return;

			textPaint.setColor(WHITE);
			textPaint.setTextSize(lessonInfoSize);
			
			if (lessonInfoBackground == null) {
				lessonInfoBackground =context.getResources().getDrawable(R.drawable.lessoninfo_bg);
			}
			
			boolean flipLeft = false;
			boolean flipTop = false;
			
			String teacherText = lesson.teacher;
			if (teacherText.length() > 5) {
				teacherText = teacherText.substring(0, 5);
			}
			String durationText = lesson.getDuration();
			String timeText = timetable.begintime[lesson.time] + "~\n"
					+ timetable.endtime[lesson.time];

			int textWidth, textHeight;
			
			textWidth = (int) textPaint.measureText(timeText)-5;
			textWidth += lessonInfoPaddingLeft * 2;
			textHeight = (int) (lessonInfoPadding * 3  + lessonInfoSize * 3);
			textHeight += lessonInfoBackground.getIntrinsicHeight() / 4;
			
			if (lessonInfoX + textWidth > viewWidth) flipLeft = true;
			if (lessonInfoY + textHeight > viewHeight) flipTop = true;
			
			/*
			lessonInfoBorder.left = flipLeft ? lessonInfoX - textWidth : lessonInfoX;
			lessonInfoBorder.top = flipTop ? lessonInfoY - textHeight : lessonInfoY;
			lessonInfoBorder.right = flipLeft ? lessonInfoX : lessonInfoX + textWidth;
			lessonInfoBorder.bottom = flipTop? lessonInfoY : lessonInfoY + textHeight;
			*/
			lessonInfoBorder.left = (int) (leftBorder[lessonPosition.week] + 1);
			lessonInfoBorder.top = (int) (topBorder[lessonPosition.time] + 1);
			lessonInfoBorder.right = (int) (leftBorder[lessonPosition.week + 1] - 1);
			lessonInfoBorder.bottom =  (int) (bottomBorder[lessonPosition.time] - 1);
			
			lessonInfoBackground.setBounds(lessonInfoBorder);
			lessonInfoBackground.draw(canvas);
			
			String info=" "+teacherText+"\n"+durationText+"\n"+timeText;
			float textLeft = lessonInfoBorder.left + lessonInfoPaddingLeft;
			StaticLayout layout = new StaticLayout(info, textPaint, 300,  
			        Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
			canvas.translate(textLeft, (lessonInfoBorder.top + lessonInfoPadding + lessonInfoSize));
			layout.draw(canvas);
			lessonInfoIsShowing = true;
		}

		private GridPosition calcGridPosition() {
			//return null if not in grid
			int gridX = (int) (markX / cellWidth);
			int gridY = -1;
			for (int i = 0; i < 5; i++) {
				if (markY > topBorder[i] && markY < bottomBorder[i])
					gridY = i;
			}
			if (!Timetable.isValidWeekTime(gridX, gridY)) return null;
			lesson = lessons[gridX][gridY];
			GridPosition position = new GridPosition();
			position.week = gridX;
			position.time = gridY;
			if (lesson != null) {
				position.hasLesson = true;
				position.lid = lesson.lid;
			}
			return position;
		}

		private void showLessonInfo() {
			if (lessonInfoIsShowing) {
				if (lessonInfoBorder.contains(markX, markY)) {
					openLessonmateActivity();
				} else if ((lessonPosition = calcGridPosition()) != null && lessonPosition.hasLesson) {
					lessonInfoX = markX;
					lessonInfoY = markY;
					showLessonInfo = true;
					invalidate();
				} else {
					showLessonBackgroundIfNoLesson = false;
					showLessonInfo = false;
					invalidate();
					lessonInfoIsShowing = false;
				}
			} else {
				lessonPosition = calcGridPosition();
				if (lessonPosition == null || !lessonPosition.hasLesson) return;
				lessonInfoX = markX;
				lessonInfoY = markY;
				showLessonInfo = true;
				invalidate();
			}
		}
		
		private void openLessonmateActivity() {
			if (lessonPosition == null) return;
			if (lessonPosition.lid == 0) return;
			Intent i = new Intent(context, LessonmateActivity.class);
			i.putExtra("lid", lessonPosition.lid);
			i.putExtra("week", lessonPosition.week);
			i.putExtra("time", lessonPosition.time);
			i.putExtra("title", lessons[lessonPosition.week][lessonPosition.time].getTitle());
			context.startActivity(i);
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			markX = (int) event.getX();
			markY = (int) event.getY();
			return super.onTouchEvent(event);
		}

	}

