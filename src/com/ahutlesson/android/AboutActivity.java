package com.ahutlesson.android;

import android.os.Bundle;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		actionBar.setTitle("����");
		setContentView(R.layout.about);
		
	}
}
