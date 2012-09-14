package com.ahutpt.lesson;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingNoticeActivity extends SherlockPreferenceActivity {
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		getSupportActionBar().setHomeButtonEnabled(false);
		addPreferencesFromResource(R.xml.setting_notice);
	}
	
}
