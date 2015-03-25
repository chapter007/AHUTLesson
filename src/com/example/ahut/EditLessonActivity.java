package com.example.ahut;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ahut_user.LessonManager;
import com.example.ahut_view.Lesson;
import com.example.ahut_view.Timetable;
import com.example.ahut_view.ToolbarActivity;
import com.umeng.analytics.MobclickAgent;

public class EditLessonActivity extends ToolbarActivity{
	private Lesson lesson;
	private int week, time, startWeek, endWeek;
	private String lessonName, lessonPlace, teacherName;
	private EditText etLessonName, etLessonPlace, etTeacherName,
			etStartWeek, etEndWeek;

	private Timetable timetable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		timetable = Timetable.getInstance(this);
		enableHomeButton();
		
		week = getIntent().getExtras().getInt("week");
		time = getIntent().getExtras().getInt("time");


		if (!((week >= 0 && week <= 6) && (time >= 0 && time <= 4))) {
			this.finish();
		}

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(timetable.weekName[week] + timetable.lessontimeName[time]);

		etLessonName = (EditText) findViewById(R.id.etLessonName);
		etLessonPlace = (EditText) findViewById(R.id.etLessonPlace);
		etTeacherName = (EditText) findViewById(R.id.etTeacherName);
		etStartWeek = (EditText) findViewById(R.id.etStartWeek);
		etEndWeek = (EditText) findViewById(R.id.etEndWeek);

		lesson = LessonManager.getInstance(this).getLessonAt(week, time);
		if (lesson != null) {
			etLessonName.setText(lesson.name);
			etLessonPlace.setText(lesson.place);
			etTeacherName.setText(lesson.teacher);
			etStartWeek.setText(String.valueOf(lesson.startweek));
			etEndWeek.setText(String.valueOf(lesson.endweek));
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("ok?cancel", ""+item.getItemId());
		switch (item.getItemId()) {
		case R.id.menu_edit_ok:
			editLesson();
			finish();
			return true;
		case R.id.menu_edit_cancel:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void editLesson() {
		lessonName = etLessonName.getText().toString();
		lessonPlace = etLessonPlace.getText().toString();
		teacherName = etTeacherName.getText().toString();
		String startWeekText = etStartWeek.getText().toString();
		String endWeekText = etEndWeek.getText().toString();
		try {
			startWeek = Integer.valueOf(startWeekText);
			endWeek = Integer.valueOf(endWeekText);
			if (startWeek > endWeek) throw new Exception("结束周不能小于起始周");
		} catch (NumberFormatException ex) {
			makeToast("起始周或结束周输入错误");
			startWeek = timetable.numOfWeek;
			endWeek = startWeek + 1;
		} catch (Exception ex) {
			makeToast(ex.getMessage());
			startWeek = timetable.numOfWeek;
			endWeek = startWeek + 1;
		}
		
		LessonManager lessonManager = LessonManager.getInstance(this);
		if (lessonManager.hasLessonAt(week, time)) {
			lessonManager.editLessonAt(lessonName, lessonPlace,
				teacherName, startWeek, endWeek, week, time);
		} else {
			lessonManager.addLessonAt(lessonName, lessonPlace,
					teacherName, startWeek, endWeek, week, time);
		}
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.edit_lesson;
	}
}
