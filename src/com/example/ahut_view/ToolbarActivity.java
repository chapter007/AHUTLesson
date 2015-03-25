package com.example.ahut_view;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahut.MainActivity;
import com.example.ahut.R;
import com.example.ahut_user.UserActivity;

public abstract class ToolbarActivity extends ActionBarActivity{
	protected Toolbar mToolbar;
	protected Handler baseHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());
		if (getLayoutResource()==R.layout.profile||
				getLayoutResource()==R.layout.user||
				getLayoutResource()==R.layout.about) {
			mToolbar=(Toolbar) findViewById(R.id.toolbar_trans);
		}else {
			mToolbar=(Toolbar) findViewById(R.id.toolbar);
		}
		setSupportActionBar(mToolbar);
	}
	
	protected abstract int getLayoutResource();
	
	public void enableHomeButton() {
		if(getSupportActionBar() == null) return;
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void disableHomeButton() {
		if(getSupportActionBar() == null) return;
		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}
	
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Log.i("", "activity: "+this);
			if (!this.getClass().equals(MainActivity.class)) {
				finish();
			}
			return (true);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void openActivity(Class<?> cls) {
		Intent i = new Intent(this, cls);
		startActivity(i);
	}
	
	public void alert(final String message) {
		if(isFinishing()) return;
		runOnUiThread(new Runnable(){
			@SuppressLint("NewApi")
			@Override
			public void run() {
				if (android.os.Build.VERSION.SDK_INT >= 11) {
					TextView showText = new TextView(ToolbarActivity.this);
					showText.setTextSize(18);
					showText.setPadding(10, 10, 10, 10);
					showText.setText(message);
					showText.setTextIsSelectable(true);
					new MaterialAlertDialog.Builder(ToolbarActivity.this)
					.setView(showText, true)
					.setPositiveButton(R.string.ok).show();
				} else {
					new MaterialAlertDialog.Builder(ToolbarActivity.this)
					.setMessage(message)
					.setPositiveButton(R.string.ok).show();
				}
			}
		});
	}
	
	public void alert(final String title, final String message) {
		if(isFinishing()) return;
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				new AlertDialog.Builder(ToolbarActivity.this)
				.setIcon(R.drawable.ahutlesson)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(R.string.ok, null).show();
			}
		});
	}
	
	public void makeToast(final String message) {
		if(isFinishing()) return;
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Toast.makeText(ToolbarActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}
}
