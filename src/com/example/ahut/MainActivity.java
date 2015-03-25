package com.example.ahut;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.json.JSONObject;

import com.example.ahut_appwidget.GetScreen;
import com.example.ahut_user.LessonManager;
import com.example.ahut_user.User;
import com.example.ahut_user.UserActivity;
import com.example.ahut_user.UserManager;
import com.example.ahut_util.Util;
import com.example.ahut_view.AHUTAccessor;
import com.example.ahut_view.GridPosition;
import com.example.ahut_view.GridView;
import com.example.ahut_view.MaterialAlertDialog;
import com.example.ahut_view.MaterialAlertDialog.Builder;
import com.example.ahut_view.Timetable;
import com.example.ahut_view.TimetableSetting;
import com.example.ahut_view.TimetableViewerActivity;
import com.example.ahut_view.ToolbarActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;












import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.InputType;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends ToolbarActivity implements OnClickListener{

	private View dateInfoView;
	private TextView tvDateInfo,name;
	private GridView gridView;
	private ImageView people;
	private LinearLayout drawer_main;
	private ActionBarDrawerToggle mDrawerToggle;  
    private LinearLayout myLessonTable,message,info;
    private LinearLayout setting,about; 
    private User user;
    private UserManager userManager;  
    private DrawerLayout left_drawer;	
	private FrameLayout framelayoutView;
	private SharedPreferences preferences;
	public static boolean needRefresh = false;
	private static final int MENU_ADD = 10;
	private static final int MENU_EDIT = 11;
	private static final int MENU_DELETE = 12;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalContext.mainActivity=this;
        GlobalContext.initImageLoader();       
		//init
		initLayout();
		//user info
		userManager = UserManager.getInstance(this);
		user = userManager.getUser();
		name.setText(user.uname);
		preferences=PreferenceManager.getDefaultSharedPreferences(this);
		if (preferences.getBoolean("First", true)) {
			preferences.edit().putBoolean("First", false).commit();
			Log.i("preferences", "imfirst");
		}else {
			Log.i("preferences", "first");
			preferences.edit().putBoolean("First", true).commit();
		}
		ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(user.uxh), people);

		people.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity(ProfileActivity.class);
			}
		});
		
       
        mDrawerToggle = new ActionBarDrawerToggle(this, left_drawer,
                mToolbar, 0, 0) {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                }
            };
            
        left_drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        
		framelayoutView=(FrameLayout) findViewById(R.id.content_frame);
		GlobalContext.mainActivity = this;
		
		// if not login
		UserManager userManager = UserManager.getInstance(this);
		if (!userManager.hasLocalUser()) {
			openActivity(RegisterActivity.class);
			finish();
			return;
		}
		// dateInfo
		dateInfoView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.actionbar_customview, null, false);
		tvDateInfo = (TextView) dateInfoView.findViewById(R.id.tvCumstomView);
		tvDateInfo.setText(dateInfo());
		getSupportActionBar().setCustomView(dateInfoView);
		getSupportActionBar().setDisplayShowCustomEnabled(true);

		PreferenceManager
				.getDefaultSharedPreferences(this);

		gridView = new GridView(this, null);
		framelayoutView.addView(gridView);
		registerForContextMenu(gridView);

  		
		// Changelog
		/*ChangeLog cl = new ChangeLog(MainActivity.this);
		if (cl.firstRun()) {
			cl.getLogDialog().show();
		}*/

		// Update
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean autoUpdate = prefs.getBoolean("auto_update", true);
		if (autoUpdate)
			new UpdateTask().execute();

		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		
	}

	protected void initLayout() {
		left_drawer=(DrawerLayout) findViewById(R.id.left_drawer);
		people=(ImageView) findViewById(R.id.people);
		drawer_main=(LinearLayout) findViewById(R.id.drawer_main);
		name=(TextView) findViewById(R.id.name);
		myLessonTable=(LinearLayout) findViewById(R.id.myLessonTable);
		message=(LinearLayout) findViewById(R.id.message);
		info=(LinearLayout) findViewById(R.id.info);
		setting=(LinearLayout) findViewById(R.id.setting);
		about=(LinearLayout) findViewById(R.id.about);
		
		myLessonTable.setOnClickListener(this);
		message.setOnClickListener(this);
		info.setOnClickListener(this);
		setting.setOnClickListener(this);
		about.setOnClickListener(this);
		
	}
	
	protected void getScreen() throws IOException {
		GetScreen getScreen=new GetScreen();
		Bitmap bitmap=getScreen.myShot(MainActivity.this);
		FileOutputStream fos=null;
		try {
			fos=new FileOutputStream(getFilesDir()+".png");
			if (fos!=null) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected void LessonBrower() {
		Builder alert;
		alert = new MaterialAlertDialog.Builder(this);
		alert.setTitle(R.string.timetable_viewer);
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setHint("输入学号搜索");
		input.setHintTextColor(Color.parseColor("#c4c4c4"));
		alert.setView(input, true);
		alert.setPositiveButton(R.string.ok);
		alert.show();
		alert.setButtonListener(new MaterialAlertDialog.OnClickListener() {
			@Override
			public boolean onClick(MaterialAlertDialog dialog, int which) {
				switch (which) {
				case MaterialAlertDialog.POSITIVE:
					String value = input.getText().toString();
					Intent i = new Intent(MainActivity.this,
							TimetableViewerActivity.class);
					i.putExtra("uxh", value);
					startActivity(i);
					break;
				}
				return true;
			}
		});
		
	}
	
	public void refresh() {
		if (tvDateInfo != null) tvDateInfo.setText(dateInfo());
		if (gridView != null) gridView.refreshView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (needRefresh) {
			refresh();
		}
		Alarm.setAlarm(this);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		needRefresh = true;
		MobclickAgent.onPause(this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (gridView == null) return;
		GridPosition position = gridView.getCurrentGridPosition();
		if (position == null) return;
		if (position.hasLesson) {
			gridView.updateLessonPosition(false);
			menu.add(0, MENU_EDIT, Menu.NONE, R.string.edit);
			menu.add(0, MENU_DELETE, Menu.NONE, R.string.delete);
		} else {
			gridView.updateLessonPosition(true);
			menu.add(0, MENU_ADD, Menu.NONE, R.string.add);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		if (gridView == null) return false;
		GridPosition position = gridView.getCurrentGridPosition();
		if (position == null) return false;
		switch (item.getItemId()) {
		case MENU_ADD:
			Intent intent = new Intent(this, EditLessonActivity.class);
			intent.putExtra("week", position.week);
			intent.putExtra("time", position.time);
			startActivity(intent);
			break;
		case MENU_EDIT:
			Intent intent1 = new Intent(this, EditLessonActivity.class);
			intent1.putExtra("week", position.week);
			intent1.putExtra("time", position.time);
			startActivity(intent1);
			break;
		case MENU_DELETE:
			LessonManager.getInstance(this).deleteLessonAt(position.week,
					position.time);
			refresh();
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onContextMenuClosed(android.view.Menu menu) {
		refresh();
		super.onContextMenuClosed(menu);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	                          
	public String dateInfo() {
		Timetable timetable = Timetable.getInstance(this);
		timetable.refreshNumOfWeek();
		int numOfWeek = timetable.numOfWeek;
		if (numOfWeek > 0) {
			return "第" + String.valueOf(numOfWeek) + "周" + " "
					+ timetable.weekName[timetable.weekDay];
		} else {
			return "未开学 " + timetable.weekName[timetable.weekDay];
		}
	}

	public class UpdateTask extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected Void doInBackground(Integer... arg0) {
			try {
				JSONObject ret = AHUTAccessor.getInstance(MainActivity.this)
						.checkUpdate();
				if (ret.has("upToDate"))
					Util.log("upToDate");
				if (ret.has("hasNewLessondbVer")) {
					Util.log("found New Lessondb Version");
					UserManager.getInstance(MainActivity.this).updateLessonDB();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							refresh();
						}
					});
					String latestLessondbVer = ret
							.getString("latestLessondbVer");
					makeToast("已更新课表数据 (" + latestLessondbVer + ")");
				}
				if (ret.has("hasNewTimetableSetting")) {
					Util.log("found New Timetable Setting");
					TimetableSetting timetableSetting = new TimetableSetting();
					JSONObject retSetting = ret
							.getJSONObject("newTimetableSetting");
					timetableSetting.year = retSetting.getInt("year");
					timetableSetting.month = retSetting.getInt("month");
					timetableSetting.day = retSetting.getInt("day");
					timetableSetting.setSeason(retSetting.getInt("season"));
					Timetable.getInstance(MainActivity.this)
							.setTimetableSetting(timetableSetting);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							refresh();
						}
					});
					makeToast("当前时令：" + timetableSetting.getBeginDate());
				}
			} catch (Exception ex) {
				Util.log(ex.getMessage());
			}
			return null;
		}

	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.search) {
			LessonBrower();
		}
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
 
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.myLessonTable:
			left_drawer.closeDrawer(drawer_main);
			break;
		case R.id.message:
			openActivity(Net.class);
			break;
		case R.id.info:
			Intent i=new Intent(MainActivity.this,UserActivity.class);
			i.putExtra("uxh", user.uxh);
			startActivity(i);
			break;
		case R.id.setting:
			openActivity(preferenceActivity.class);
			break;
		case R.id.about:
			openActivity(AboutActivity.class);
			break;
		
		}
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.drawer;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		try {
			if (preferences.getBoolean("First", true)) {
				Log.i("getScreen", "run?");
				getScreen();
				preferences.edit().putBoolean("First", false).commit();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
