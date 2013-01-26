package com.ahutpt.lesson;

import com.ahutpt.lesson.network.NetworkHelper;
import com.ahutpt.lesson.utils.ValidateHelper;
import com.ahutpt.lesson.lesson.LessonManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

public class ManageDBActivity extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.setting_db);
		
		if(!LessonManager.loaded){
			new LessonManager(this);
		}
		
		Preference downDB = (Preference)findPreference("down_db");
		downDB.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(ManageDBActivity.this).setTitle("���ȷ��")
				.setMessage("���ؿα�ǰ�������ǰ�Ŀα�\n�Ƿ������")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						beginUpdate();
					}

				}).setNegativeButton("ȡ��", null).show();
				return true;
			}
			
		});
		Preference delDB = (Preference)findPreference("delete_db");
		delDB.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(ManageDBActivity.this).setTitle("���ȷ��")
				.setMessage("ȷ��Ҫ��տα���")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LessonManager.deleteDB();
					}

				}).setNegativeButton("ȡ��", null).show();
				return true;
			}
			
		});
	}

	private void beginUpdate() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String xh = preferences.getString("down_xh", "");
		if(xh.contentEquals("")){
			alert("��������ѧ��");
			return;
		}		
		if(!ValidateHelper.isXH(xh)){
			alert("ѧ����Ч");
			return;
		}
		new UpdateAsync().execute(xh);
	}
	
	
	class UpdateAsync extends AsyncTask<String, String, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(ManageDBActivity.this, "",
					"����������...", true);
		}

		@Override
		protected String doInBackground(String... para) {
			return NetworkHelper
					.readURL("http://ahut2011.sinaapp.com/lesson/getdata.php?xh=" + para[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			switch(LessonManager.updateDB(result)){
			case LessonManager.EMPTY_RESPONSE:
				alert("������δ��������");
				break;
			case LessonManager.EMPTY_DATA:
				alert("δ�ҵ��α���Ϣ");
				break;
			case LessonManager.PARSE_ERROR:
				alert("��������ʧ��");
				break;
			case LessonManager.UPDATE_OK:
				alert("�������سɹ�");
				break;
			}
			dialog.dismiss();
		}
	}
	
	public void alert(String notice) {
		Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
	}
}
