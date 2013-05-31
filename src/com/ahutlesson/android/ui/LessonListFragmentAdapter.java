package com.ahutlesson.android.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class LessonListFragmentAdapter extends FragmentStatePagerAdapter {

	public static final String[] weekNames = new String[] { "����һ", "���ڶ�",
			"������", "������", "������", "������", "������" };

	public LessonListFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return LessonListFragment.newInstance(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return weekNames[position];
	}

	@Override
	public int getCount() {
		return 7;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}
