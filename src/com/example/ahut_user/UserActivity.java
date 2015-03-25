package com.example.ahut_user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahut.R;
import com.example.ahut_util.ValidateHelper;
import com.example.ahut_view.AHUTAccessor;
import com.example.ahut_view.TimetableViewerActivity;
import com.example.ahut_view.ToolbarActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserActivity extends ToolbarActivity{
	private String uxh,uxy;
	private ImageView ivAvatar;
	private TextView tvUname, tvUxh,tvSignature, tvUserInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		enableHomeButton();
		
		uxh = getIntent().getExtras().getString("uxh");
		if(!ValidateHelper.isXH(uxh)) {
			this.finish();
			return; 
		}

		tvUname = (TextView) findViewById(R.id.tvUname);
		tvUxh = (TextView) findViewById(R.id.tvUxh);
		tvSignature = (TextView) findViewById(R.id.tvSignature);
		tvUserInfo = (TextView) findViewById(R.id.tvUserInfo);
		ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
		
		Button btnViewTimetable = (Button) findViewById(R.id.btnViewTimetable);
		btnViewTimetable.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(UserActivity.this, TimetableViewerActivity.class);
				i.putExtra("uxh", uxh);
				startActivity(i);
			}
		});
		
		new LoadUserInfo().execute();
	}
	public String getxy() {
		new LoadUserInfo().execute();
		return uxy;
	}
	
	private class LoadUserInfo extends AsyncTask<String, String, UserInfo> {

		@Override
		protected UserInfo doInBackground(String... arg0) {
			
			try {
				return AHUTAccessor.getInstance(UserActivity.this).getUserInfo(uxh);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(UserInfo uinfo) {
			if(uinfo == null)  return;
			tvUname.setText(uinfo.uname);
			tvUxh.setText(uinfo.uxh);
			tvSignature.setText(uinfo.signature);
			String userInfo = "�Ա�:" + uinfo.xb + "\n"
					+ "�༶:" + uinfo.bj + "\n"
					+ "רҵ:" + uinfo.zy + "\n"
					+ "ѧԺ:" + uinfo.xy + "\n"
					+ "���ڼ�:" + uinfo.rx + "\n\n"
					+ "ע��ʱ��:" + uinfo.registerTime + "\n"
					+ "����¼:" + uinfo.lastloginTime;
			uxy=uinfo.xy;
			
			tvUserInfo.setText(userInfo);
			Log.i("", "��ô�������ˣ�"+uxy);
			if(uinfo.hasAvatar) {
		        ImageLoader.getInstance().displayImage(AHUTAccessor.getAvatarURI(uinfo.uxh), ivAvatar);
			}
		}
		
	}

	@Override
	protected int getLayoutResource() {
		
		return R.layout.user;
	}

	

	
}
