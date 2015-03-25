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
 * 想要实现向右滑动删除Activity效果只需要继承SwipeBackActivity即可，如果当前页面含有ViewPager
 * 只需要调用SwipeBackLayout的setViewPager()方法即可
 * 继承类无需onKeyDown    当前activity返回按钮只需要Activity.this.finish();即可
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
