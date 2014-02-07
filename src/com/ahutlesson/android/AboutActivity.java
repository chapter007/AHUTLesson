package com.ahutlesson.android;

import com.ahutlesson.android.utils.ChangeLog;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.about);
		
		final ChangeLog cl = new ChangeLog(AboutActivity.this);
		
		TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
		tvVersion.setText("�汾: " + cl.getThisVersion());
		
		
		Button btnCheckUpdate = (Button) findViewById(R.id.btnCheckUpdate);
		Button btnChangelog = (Button) findViewById(R.id.btnChangelog);
		
		btnCheckUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				makeToast("��ʼ������...");
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
					@Override
					public void onUpdateReturned(int updateStatus,
							UpdateResponse updateInfo) {
						switch (updateStatus) {
						case 0: // has update
							UmengUpdateAgent.showUpdateDialog(AboutActivity.this, updateInfo);
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
				UmengUpdateAgent.update(AboutActivity.this);
			}
		});
		
		btnChangelog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				cl.getFullLogDialog().show();
			}
		});
		
	}
}
