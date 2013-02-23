package com.ahutlesson.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.model.Lesson;
import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.Timetable;
import com.umeng.analytics.MobclickAgent;

public class LessonDetailActivity extends BaseActivity {
	
	private static final int MENU_EDIT = 0;
	private static final int MENU_EDITHOMEWORK = 1;
	private static final int MENU_DELETE = 2;
	private int week, time;
	private Lesson lesson;
	private TextView tvLessonName, tvLessonPlace, tvTeacherName, tvLessonWeek,
			tvHomework, tvLessonTime, tvCurrentTime;
	
	private LessonManager lessonManager;
	private Timetable timetable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		week = getIntent().getExtras().getInt("week");
		time = getIntent().getExtras().getInt("time");

		if(!Timetable.isValidWeekTime(week, time)) {
			this.finish();
			return;
		}
        
		setContentView(R.layout.lessondetail);

		tvLessonName = (TextView) findViewById(R.id.tvLessonName);
		tvLessonPlace = (TextView) findViewById(R.id.tvLessonPlace);
		tvTeacherName = (TextView) findViewById(R.id.tvTeacherName);
		tvLessonWeek = (TextView) findViewById(R.id.tvLessonWeek);
		tvHomework = (TextView) findViewById(R.id.tvHomework);
		tvLessonTime = (TextView) findViewById(R.id.tvLessonTime);

		tvCurrentTime = (TextView) findViewById(R.id.tvCurrentTime);

	}

	@Override
	protected void onResume() {
		// ɾ�����޸ĺ���������
		super.onResume();
		
		lessonManager = LessonManager.getInstance(this);
		timetable = Timetable.getInstance(this);

		tvCurrentTime.setText(timetable.weekname[week]
				+ timetable.lessontime_name[time]);
		tvLessonTime.setText(timetable.begintime[time] + " ~ "
				+ timetable.endtime[time]);
		
		
		lesson = lessonManager.getLessonAt(week, time);
		if (lesson != null) {
			tvLessonName.setText(lesson.name);
			tvLessonPlace.setText(lesson.place);
			tvLessonWeek.setText(lesson.getDuration());
			tvTeacherName.setText(lesson.teacher);
			if (lesson.homework != null && !lesson.homework.contentEquals("")) {
				tvHomework.setText(lesson.homework);
			}
		} else {
		}

		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	protected void editHomework() {
		// �����ҵ
		if (lesson == null)
			return;
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("�༭��ҵ");
		alert.setMessage("�����뱾�γ̵���ҵ");

		final EditText input = new EditText(this);
		alert.setView(input);
		if (lesson.hasHomework && lesson.homework != null
				&& !lesson.homework.contentEquals("")) {
			input.setText(lesson.homework);
		}
		alert.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (!value.contentEquals("")) {
					lesson.homework = value;
					lesson.hasHomework = true;
					lessonManager.editHomework(week, time, lesson.homework);
					tvHomework.setText(lesson.homework);
				}
			}
		});
		alert.setNegativeButton("���", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (!value.contentEquals("")) {
					lesson.homework = null;
					lesson.hasHomework = false;
					lessonManager.deleteHomework(week, time);
					tvHomework.setText("��");
				}
			}
		});
		alert.show();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_EDIT, Menu.NONE, R.string.edit)
			.setIcon(R.drawable.edit)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0, MENU_EDITHOMEWORK, Menu.NONE, "�༭��ҵ")
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0, MENU_DELETE, Menu.NONE, R.string.delete)
			.setIcon(R.drawable.delete)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			if (lesson == null) return true;
			Intent i = new Intent(this, EditLessonActivity.class);
			i.putExtra("week", week);
			i.putExtra("time", time);
			startActivity(i);
			return true;
		case 1:
			editHomework();
			return true;
		case 2:
			if (lesson == null) return true;
			new AlertDialog.Builder(LessonDetailActivity.this)
					.setTitle("ɾ���γ�")
					.setMessage("ȷ��ɾ�����γ̣�")
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (lesson != null) {
										LessonManager.getInstance(
												LessonDetailActivity.this)
												.deleteLesson(lesson);
									}
									LessonDetailActivity.this.finish();
								}

							}).setNegativeButton(R.string.cancel, null).show();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	
	
}
