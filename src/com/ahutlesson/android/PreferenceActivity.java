package com.ahutlesson.android;

import com.ahutlesson.android.model.LessonManager;
import com.ahutlesson.android.model.UserManager;
import com.ahutlesson.android.utils.ChangeLog;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

public class PreferenceActivity extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(R.drawable.preference);
		
		addPreferencesFromResource(R.xml.preferences);

		Preference changelog = (Preference) findPreference("changelog");
		changelog.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				ChangeLog cl = new ChangeLog(PreferenceActivity.this);
				cl.getFullLogDialog().show();
				return true;
			}

		});

		Preference share = (Preference) findPreference("share");
		share.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				share();
				return true;
			}

		});

		Preference downDB = (Preference) findPreference("down_lesson");
		downDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				confirm("���ؿα��������еĿα�������", new Runnable() {
					@Override
					public void run() {
						new DownLessonTask().execute();
					}
					
				});
				return true;
			}

		});
		Preference delDB = (Preference) findPreference("delete_db");
		delDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				confirm("ȷ��Ҫ��տα���", new Runnable() {
					@Override
					public void run() {
						LessonManager.getInstance(PreferenceActivity.this).deleteDB();
					}
					
				});
				return true;
			}

		});

		Preference checkUpdate = (Preference) findPreference("check_update");
		checkUpdate.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
					@Override
					public void onUpdateReturned(int updateStatus,
							UpdateResponse updateInfo) {
						switch (updateStatus) {
						case 0: // has update
							UmengUpdateAgent.showUpdateDialog(
									PreferenceActivity.this, updateInfo);
							break;
						case 1: // has no update
							alert("������ʹ�����°�");
							break;
						case 3: // time out
							alert("�������ӳ�ʱ");
							break;
						}
					}
				});
				UmengUpdateAgent.update(PreferenceActivity.this);
				return true;
			}

		});

	}

	class DownLessonTask extends AsyncTask<Integer, Integer, String> {
		
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(PreferenceActivity.this, "���Ե�","����������...", true);
		}

		@Override
		protected String doInBackground(Integer... para) {
			try {
				UserManager.getInstance(PreferenceActivity.this).updateLessonDB();
			} catch (Exception e) {
				return e.getMessage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String ret) {
			progressDialog.dismiss();
			if(ret != null) {
				alert(ret);
			}else{
				alert("���سɹ���");
			}
		}
	}

	public void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "�������ص�ַ��http://ahutapp.com/lesson");
		startActivity(Intent.createChooser(intent, "����"));
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
	
	public void confirm(String message, final Runnable r) {
		new AlertDialog.Builder(PreferenceActivity.this)
		.setMessage(message)
		.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						r.run();
					}

				}).setNegativeButton("ȡ��", null).show();
	}
	
	public void alert(String message) {
		new AlertDialog.Builder(this)
		.setMessage(message)
		.setPositiveButton("ȷ��", null).show();
	}
	
}
