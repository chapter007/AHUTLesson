package com.ahutlesson.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.fragment.HomeworkFragment;
import com.ahutlesson.android.fragment.LessonFragmentAdapter;
import com.ahutlesson.android.lesson.LessonManager;
import com.ahutlesson.android.time.Alert;
import com.ahutlesson.android.time.Timetable;
import com.ahutlesson.android.utils.ChangeLog;
import com.ahutlesson.android.view.Grid;
import com.ahutlesson.android.view.ScheduleView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends SherlockFragmentActivity implements
		ActionBar.OnNavigationListener {

	private Alert alert;
	
	private static ActionBar actionBar;
	
	private static int viewMode = 0;

	// ACTION
	private static final int SETTING = 0;
	private static final int HELP = 1;
	private static final int CLEAR_HOMEWORK = 2;
	
	// VIEW
	private static final int TODAY_VIEW = 0;
	private static final int GRID_VIEW = 1;
	private static final int HOMEWORK_VIEW = 2;

	//TODAY_VIEW
	private LessonFragmentAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	
	// GRID_VIEW
	private static ScheduleView scheduleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		super.onCreate(savedInstanceState);

		Intent i = new Intent(this,
				RegisterActivity.class);
		startActivity(i);
		
		// ��������
		alert = new Alert(this);

		// ׼��UI
		// List Navigation
		Context context = actionBar.getThemedContext();
		ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(context,
				R.layout.sherlock_spinner_item);
		list.add("���տγ�");
		list.add("�γ��ܱ�");
		list.add("�κ���ҵ");
		list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(list, this);

		// ShowView
		showView();

		// Changelog
		ChangeLog cl = new ChangeLog(this);
		if (cl.firstRun()) {
			cl.getLogDialog().show();
		}
		
		// Update
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.onError(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(true);
		UmengUpdateAgent.setUpdateListener(null);
		UmengUpdateAgent.update(this);
	}
	
	// ��ʾ��ͼ
	public void showView() {
		switch (viewMode) {
		case TODAY_VIEW:
			setContentView(R.layout.today);

			mAdapter = new LessonFragmentAdapter(getSupportFragmentManager());
			
			mPager = (ViewPager) findViewById(R.id.pager);
			mPager.setAdapter(mAdapter);

			mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
			mIndicator.setViewPager(mPager);
			mIndicator.setCurrentItem(Timetable.getCurrentWeekDay());
			break;
		case GRID_VIEW:
			// ���ƿα�
			scheduleView = new ScheduleView(this, LessonManager.getInstance(this).lessons);
			setContentView(scheduleView);
			scheduleView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					openLessonDetail();
				}
			});
			break;
		case HOMEWORK_VIEW:
			setContentView(R.layout.homework);
			
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.replace(R.id.frameLayoutFragment, HomeworkFragment.newInstance());
			transaction.commit();
			break;
		}
	}
	
	public void refreshTodayView(){
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// DateInfo
		LinearLayout layoutDateInfo = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.dateinfo, null);
		TextView text = new TextView(this);
		text.setGravity(Gravity.CENTER);
		text.setText(this.dateInfo());
		text.setTextSize(15);
		text.setTextColor(Color.WHITE);
		text.setPadding(10, 0, 0, 0);
		text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(MainActivity.this, TimetableSettingActivity.class);
				startActivity(i);
			}
		});
		layoutDateInfo.addView(text);
		actionBar.setCustomView(layoutDateInfo);
		actionBar.setDisplayShowCustomEnabled(true);

		switch(viewMode){
		case TODAY_VIEW:
			refreshTodayView();
			break;
		case GRID_VIEW:
			scheduleView.invalidate();
			break;
		}
		alert.setAlarm();
		MobclickAgent.onResume(this);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// ѡ�񵼺��˵�
		viewMode = itemPosition;
		showView();
		invalidateOptionsMenu();
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		switch (viewMode) {
		case TODAY_VIEW:
			menu.add(viewMode, SETTING, Menu.NONE, "����")
					.setIcon(android.R.drawable.ic_menu_preferences)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			break;
		case GRID_VIEW:
			menu.add(viewMode, HELP, Menu.NONE, "����")
					.setIcon(android.R.drawable.ic_menu_help)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

			break;
		case HOMEWORK_VIEW:
			menu.add(viewMode, CLEAR_HOMEWORK, Menu.NONE, "���������ҵ")
					.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			break;

		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SETTING:
			Intent i = new Intent(this, PreferenceActivity.class);
			startActivity(i);
			return true;
		case HELP:
			openHelpDialog();
			return true;
		case CLEAR_HOMEWORK:
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("�����ҵ")
					.setMessage("ȷ��������пγ���ҵ��")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									LessonManager.getInstance(MainActivity.this).deleteAllHomework();
									showView();
								}
							}).setNegativeButton("ȡ��", null).show();
			return true;
		case R.id.menu_timetableviewer:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("�α������");
			alert.setMessage("������ѧ�ţ�");

			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert.setView(input);

			alert.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							Intent i = new Intent(MainActivity.this, TimetableViewerActivity.class);
							i.putExtra("xh", value);
							startActivity(i);
						}
					});

			alert.setNegativeButton("ȡ��", null);

			alert.show();
			return true;
		case R.id.menu_exit:
			this.finish();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}

	// ������Ϣ
	public String dateInfo() {
		Timetable timetable = Timetable.getInstance(this);
		int numOfWeek = timetable.numOfWeek;
		if(numOfWeek > 0) {
			return "��" + String.valueOf(numOfWeek) + "��" + " " + timetable.weekname[timetable.weekDay];
		}else{
			return "δ��ѧ " + timetable.weekname[timetable.weekDay];
		}
	}

	// �γ�����
	public void openLessonDetail() {
		Intent i = new Intent(this, LessonActivity.class);
		i.putExtra("week", Grid.markWeek);
		i.putExtra("time", Grid.markTime);
		this.startActivity(i);
	}

	// �����Ի���
	private void openHelpDialog() {
		new AlertDialog.Builder(this).setTitle("ʹ��˵��")
				.setMessage(R.string.help_message)
				.setPositiveButton("ȷ��", null).show();
	}
	
}
