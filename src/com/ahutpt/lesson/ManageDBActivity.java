package com.ahutpt.lesson;

import com.ahutpt.lesson.network.NetworkHelper;
import com.ahutpt.lesson.lesson.LessonManager;

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

public class ManageDBActivity extends android.preference.PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String xh = preferences.getString("down_xh", "");
		if(xh.contentEquals("")){
			alert("��������ѧ��");
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
			dialog.dismiss();
			if(LessonManager.updateDB(result)){
				alert("���سɹ�");
			}else{
				alert("����ʧ��");
			}
		}
	}
	
	public void alert(String notice) {
		Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
	}
}
