package com.example.ahut_view;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.ahut.R;
import com.example.ahut_util.ValidateHelper;

public class TimetableViewerActivity extends ToolbarActivity{
	private String uxh;
	private GridView gridView;
	private FrameLayout frameLayout;
	private Lesson lessons[][] = new Lesson[7][5];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		enableHomeButton();
		frameLayout=(FrameLayout) findViewById(R.id.frame);
		uxh = getIntent().getExtras().getString("uxh");
		
		if (ValidateHelper.isXH(uxh)) {
			new GetLessons().execute(uxh);
		} else {
			makeToast("不是有效的学号");
			finish();
		}
	}

	ProgressDialog dialog;

	class GetLessons extends AsyncTask<String, String, LessonsInfo> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(TimetableViewerActivity.this, "",
					"数据下载中...", true);
		}

		@Override
		protected LessonsInfo doInBackground(String... para) {
			try {
				return AHUTAccessor.getInstance(TimetableViewerActivity.this)
						.getLessons(uxh);
			} catch (Exception e) {
				alert(e.getMessage());
				
				return null;
			}
		}

		@Override
		protected void onPostExecute(LessonsInfo ret) {
			dialog.dismiss();
			if (ret!=null) {
				lessons = ret.lessons;
				getSupportActionBar().setTitle(ret.xm + "的课表");
				showLessons();
			}
		}
	}

	@Override
	protected void onPause() {
		try {
			dialog.dismiss();
			dialog = null;
		} catch (Exception e) {
		}
		super.onPause();
	}

	private void showLessons() {
		if (lessons != null)
			gridView = new GridView(TimetableViewerActivity.this, lessons);
		frameLayout.addView(gridView);
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.timetable_view;
	}

}
