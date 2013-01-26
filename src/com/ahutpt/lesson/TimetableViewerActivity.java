package com.ahutpt.lesson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutpt.lesson.lesson.Lesson;
import com.ahutpt.lesson.network.NetworkHelper;
import com.ahutpt.lesson.utils.ValidateHelper;
import com.ahutpt.lesson.view.ScheduleView;

public class TimetableViewerActivity extends SherlockActivity {

	private Lesson lessons[][] = new Lesson[7][5];
	private ScheduleView scheduleView;
	private LinearLayout mainLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		super.onCreate(savedInstanceState);
		mainLayout = (LinearLayout) getLayoutInflater()
				.inflate(R.layout.main, null);
		setContentView(mainLayout);

		String xh = getIntent().getExtras().getString("xh");
		getSupportActionBar().setTitle(xh + "�Ŀγ̱�");
		if(ValidateHelper.isXH(xh)){
			new getDataAsync().execute(xh);
		}else{
			exit("������Ч��ѧ��");
		}
	}

	class getDataAsync extends AsyncTask<String, String, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(TimetableViewerActivity.this, "",
					"����������...", true);
		}

		@Override
		protected String doInBackground(String... para) {
			return NetworkHelper
					.readURL("http://ahut2011.sinaapp.com/lesson/getdata.php?xh="
							+ para[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			if(!parsingData(result)) exit("����ʧ��");
		}
	}

	private boolean parsingData(String JSONDATA) {
		if (JSONDATA == null || JSONDATA.contentEquals("")){
			alert("�޷�������");
			return false;
		}
		try {
			JSONTokener jsonParser = new JSONTokener(JSONDATA);
			JSONObject lesson;
			JSONArray lessonsArray;
			try {
				lessonsArray = (JSONArray) jsonParser.nextValue();
				if (lessonsArray == null){
					alert("�޷���������");
					return false;
				}
				if(lessonsArray.length() == 0){
					alert("δ�ҵ��α���Ϣ");
					return false;
				}
				for (int i = 0; i < lessonsArray.length(); i++) {
					lesson = lessonsArray.getJSONObject(i);
					String lessonName = lesson.getString("lessonname");
					String lessonAlias = lesson.getString("lessonalias");
					String teacherName = lesson.getString("teachername");
					int week = lesson.getInt("week");
					int time = lesson.getInt("time");
					int startweek = lesson.getInt("startweek");
					int endweek = lesson.getInt("endweek");
					String lessonPlace = lesson.getString("place");
					lessons[week][time] = new Lesson(lessonName, lessonAlias, lessonPlace, teacherName, startweek, endweek, null, week, time, this);
				}
				
			} catch (NullPointerException e) {
				e.printStackTrace();
				return false;
			}

			scheduleView = new ScheduleView(TimetableViewerActivity.this, lessons);
			mainLayout.removeAllViews();
			mainLayout.addView(scheduleView);
			mainLayout.invalidate();
			return true;
		} catch (JSONException ex) {
			return false;
		} 
	}
	
	private void alert(String notice){
		Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
	}
	
	public void exit(String notice){
		Toast.makeText(this, notice, Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, Menu.NONE, "��������")
		.setIcon(android.R.drawable.ic_menu_search)
		.setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
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
							String xh = input.getText().toString();
							if(ValidateHelper.isXH(xh)){
								getSupportActionBar().setTitle(xh + "�Ŀγ̱�");
								lessons = new Lesson[7][5];
								new getDataAsync().execute(xh);
							}else{
								alert("������Ч��ѧ��");
							}
						}
					});

			alert.setNegativeButton("ȡ��", null);

			alert.show();
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
