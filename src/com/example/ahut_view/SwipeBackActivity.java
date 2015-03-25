package com.example.ahut_view;

import com.example.ahut.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

/**
 * @author tim
 * @date 2014-12-19
 * @email tim_ding@qq.com
 */
/**
 * ��Ҫʵ�����һ���ɾ��ActivityЧ��ֻ��Ҫ�̳�SwipeBackActivity���ɣ������ǰҳ�溬��ViewPager
 * ֻ��Ҫ����SwipeBackLayout��setViewPager()��������
 * �̳�������onKeyDown    ��ǰactivity���ذ�ťֻ��ҪActivity.this.finish();����
 *
 */
public class SwipeBackActivity extends ToolbarActivity {
	protected SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base, null);
		layout.attachToActivity(this);
	}
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}
	@Override
	protected int getLayoutResource() {
		return 0;
	}
	 

}
