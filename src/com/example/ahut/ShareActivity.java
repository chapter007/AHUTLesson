package com.example.ahut;

import com.example.ahut_view.ToolbarActivity;

import android.os.Bundle;

public class ShareActivity extends ToolbarActivity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		enableHomeButton();
		
	}

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.share;
	}
}
