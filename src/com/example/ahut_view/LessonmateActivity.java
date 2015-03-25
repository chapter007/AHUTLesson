package com.example.ahut_view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ahut.GlobalContext;
import com.example.ahut.R;
import com.example.ahut_user.UserActivity;
import com.example.ahut_util.LessonmateAdapter;

public class LessonmateActivity extends ToolbarActivity{
	private int lid;

	private LinearLayout layoutLoading, layoutList, layoutEmpty;
	private ListView lvList;
	private LessonmateAdapter lvLessonmateAdapter;
	private ArrayList<Lessonmate> lessonmateList = new ArrayList<Lessonmate>();
	private int lessonmatePage = 1;
	public static int lessonmatesPerPage = 1;
	private View footerView;
	private TextView tvNextPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enableHomeButton();
		GlobalContext.initImageLoader();
		
		lid = getIntent().getExtras().getInt("lid");
		if (lid == 0) {
			this.finish();
			return;
		}

		String title = getIntent().getExtras().getString("title");
		if (title != null) {
			getSupportActionBar().setTitle(title);
		}

		layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
		layoutList = (LinearLayout) findViewById(R.id.layoutList);
		layoutEmpty = (LinearLayout) findViewById(R.id.layoutEmpty);
		
		lvList = (ListView) layoutList.findViewById(R.id.lvList);
		
		footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.listview_footer, null, false);
		tvNextPage = (TextView) footerView.findViewById(R.id.tvNextPage);

		footerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new LoadMoreLessonmates().execute();
			}
		});
		lvList.addFooterView(footerView);
		if (lvLessonmateAdapter == null) {
			lvLessonmateAdapter = new LessonmateAdapter(LessonmateActivity.this,
					R.layout.lessonmate_item, lessonmateList);
		}
		lvList.setAdapter(lvLessonmateAdapter);
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Lessonmate lessonmate = lessonmateList.get(position);
				Intent i;
				if (lessonmate.registered) {
					i = new Intent(LessonmateActivity.this, UserActivity.class);
					i.putExtra("uxh", lessonmate.xh);
					LessonmateActivity.this.startActivity(i);
				} else {
					i = new Intent(LessonmateActivity.this, TimetableViewerActivity.class);
					i.putExtra("uxh", lessonmate.xh);
					LessonmateActivity.this.startActivity(i);
				}
			}

		});

		// Load data
		new LoadLessonmates().execute();
	}

	private class LoadLessonmates extends
			AsyncTask<Integer, Integer, ArrayList<Lessonmate>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			layoutLoading.setVisibility(View.VISIBLE);
			layoutList.setVisibility(View.GONE);
			layoutEmpty.setVisibility(View.GONE);
			lessonmateList.clear();
		}

		@Override
		protected ArrayList<Lessonmate> doInBackground(Integer... param) {
			lessonmatePage = 1;
			try {
				return AHUTAccessor.getInstance(LessonmateActivity.this)
						.getLessonmateList(lid, lessonmatePage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Lessonmate> ret) {
			layoutLoading.setVisibility(View.GONE);
			if (ret == null)
				return;

			if (ret.size() == lessonmatesPerPage) {
				tvNextPage.setText("加载更多");
			} else {
				tvNextPage.setText("没有更多课友了");
				footerView.setOnClickListener(null);
			}

			layoutEmpty.setVisibility(View.GONE);
			layoutList.setVisibility(View.VISIBLE);
			lessonmateList.addAll(ret);
			lvLessonmateAdapter.notifyDataSetChanged();
		}
	}

	private class LoadMoreLessonmates extends
			AsyncTask<Integer, Integer, ArrayList<Lessonmate>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvNextPage.setText("加载中,请稍候...");
		}

		@Override
		protected ArrayList<Lessonmate> doInBackground(Integer... param) {
			lessonmatePage++;
			try {
				return AHUTAccessor.getInstance(LessonmateActivity.this)
						.getLessonmateList(lid, lessonmatePage);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Lessonmate> ret) {
			if (ret == null)
				return;

			if (ret.size() == lessonmatesPerPage) {
				tvNextPage.setText("加载更多");
			} else {
				tvNextPage.setText("没有更多课友了");
				footerView.setOnClickListener(null);
			}

			lessonmateList.addAll(ret);
			lvLessonmateAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.list;
	}
}
