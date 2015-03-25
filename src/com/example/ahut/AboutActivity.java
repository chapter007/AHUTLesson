package com.example.ahut;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ahut_user.LessonManager;
import com.example.ahut_view.ChangeLog;
import com.example.ahut_view.SwipeBackActivity;
import com.example.ahut_view.Timetable;
import com.example.ahut_view.TimetableSetting;
import com.example.ahut_view.ToolbarActivity;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class AboutActivity extends ToolbarActivity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		enableHomeButton();
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.about;
	}
}
