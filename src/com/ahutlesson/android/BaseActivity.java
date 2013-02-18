package com.ahutlesson.android;

import android.app.AlertDialog;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class BaseActivity extends SherlockActivity {

	protected ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		actionBar = getSupportActionBar();
		enableHomeButton();
		super.onCreate(savedInstanceState);
	}

	public void enableHomeButton() {
		if(actionBar == null) return;
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	public void disableHomeButton() {
		if(actionBar == null) return;
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return (true);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void alert(String message) {
		new AlertDialog.Builder(this)
		.setMessage(message)
		.setPositiveButton(R.string.cancel, null).show();
	}
	
	public void alert(String title, String message) {
		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(R.string.cancel, null).show();
	}
}
