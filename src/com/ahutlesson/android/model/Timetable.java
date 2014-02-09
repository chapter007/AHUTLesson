package com.ahutlesson.android.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.Days;

import com.ahutlesson.android.R;
import com.ahutlesson.android.api.AHUTAccessor;

public class Timetable {

	private static Timetable timetable;

	private Context context;
	private Calendar cal;
	private SharedPreferences preferences;
	public String[] begintime = new String[5];
	public String[] endtime = new String[5];
	public int beginDate_year, beginDate_month, beginDate_day;
	public int year, month, dayOfMonth, dayOfYear, weekDay;
	public int begintimemin[] = new int[5];
	public int endtimemin[] = new int[5];
	public int numOfWeek = -1;
	public boolean seasonWinter = false;
	public String[] weekName = new String[7], lessontimeName = new String[5];

	public Timetable(Context context0) {
		context = context0;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		weekName = context.getResources().getStringArray(R.array.week_name);
		lessontimeName = context.getResources().getStringArray(
				R.array.lessontime_name);
		loadData();
		initTime();
	}

	public static Timetable getInstance(Context context) {
		if (timetable == null) {
			timetable = new Timetable(context);
		}
		return timetable;
	}

	public void loadData() {
		// ����/ˢ������
		beginDate_year = preferences.getInt("begin_date_year",
				getYearOfCurrentPeriod());
		if (formerPeriodOfYear()) {
			beginDate_month = preferences.getInt("begin_date_month", 8);// important!
																		// ��0��ʼ
																		// ʵ��9��
			beginDate_day = preferences.getInt("begin_date_day", 3);
		} else {
			beginDate_month = preferences.getInt("begin_date_month", 1);// important!
																		// ��0��ʼ
																		// ʵ��9��
			beginDate_day = preferences.getInt("begin_date_day", 27);
		}

		seasonWinter = preferences.getBoolean("season_winter", false);
		loadBeginEndTime(seasonWinter);
	}

	private void loadBeginEndTime(boolean seasonWinter) {
		if (seasonWinter == false) {
			begintime[0] = "08:00";
			begintime[1] = "10:00";
			begintime[2] = "14:30";
			begintime[3] = "16:30";
			begintime[4] = "19:00";

			endtime[0] = "09:35";
			endtime[1] = "11:35";
			endtime[2] = "16:05";
			endtime[3] = "18:05";
			endtime[4] = "21:30";

			begintimemin[0] = 480;
			begintimemin[1] = 600;
			begintimemin[2] = 870;
			begintimemin[3] = 990;
			begintimemin[4] = 1140;

			endtimemin[0] = 575;
			endtimemin[1] = 695;
			endtimemin[2] = 965;
			endtimemin[3] = 1085;
			endtimemin[4] = 1290;
		} else {
			begintime[0] = "08:00";
			begintime[1] = "10:00";
			begintime[2] = "14:00";
			begintime[3] = "16:00";
			begintime[4] = "18:30";

			endtime[0] = "09:35";
			endtime[1] = "11:35";
			endtime[2] = "15:35";
			endtime[3] = "17:35";
			endtime[4] = "21:00";

			begintimemin[0] = 480;
			begintimemin[1] = 600;
			begintimemin[2] = 840;
			begintimemin[3] = 960;
			begintimemin[4] = 1110;

			endtimemin[0] = 575;
			endtimemin[1] = 695;
			endtimemin[2] = 935;
			endtimemin[3] = 1055;
			endtimemin[4] = 1260;
		}

		/*
		 * for(int i = 0;i < 5; i++){ begintimemin[i] =
		 * time2minute(begintime[i]); }
		 * 
		 * for(int i = 0;i < 5; i++){ endtimemin[i] = time2minute(endtime[i]); }
		 */
	}

	public void toggleSeason() {
		seasonWinter = !seasonWinter;
		loadBeginEndTime(seasonWinter);
	}

	public void setSeasonWinter(boolean winter) {
		// false for summer, true for winter
		Editor editor = preferences.edit();
		editor.putBoolean("season_winter", winter);
		editor.commit();
	}

	public void initTime() {
		cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		weekDay = getCurrentWeekDay();
		numOfWeek = getNumOfWeekSincePeriod();
	}

	public void refreshNumOfWeek() {
		numOfWeek = getNumOfWeekSincePeriod();
	}

	public int getNumOfWeekSincePeriod() {
		// ���㿪ѧ�ڼ���
		Calendar beginCal = Calendar.getInstance();
		beginCal.set(beginDate_year, beginDate_month, beginDate_day);
		Date beginDate = beginCal.getTime();

		int days = Days.daysBetween(new org.joda.time.DateTime(beginDate),
				new org.joda.time.DateTime(Calendar.getInstance().getTime()))
				.getDays();

		if (days >= 0 && days <= 180)
			return days / 7 + 1;
		else
			return 0;
	}

	public int dayOfYear(int year) {
		// һ���е�����
		if ((year % 4 == 0 && year % 400 != 0) || year % 400 == 0)
			return 365;
		else
			return 366;
	}

	public boolean formerPeriodOfYear() {
		// true summer
		// false spring
		cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		return ((month >= 0 && month <= 1) || (month >= 8)) ? true : false;
	}

	public int getYearOfCurrentPeriod() {
		// ���㵱ǰѧ�ڵĿ�ѧ���
		if (formerPeriodOfYear()) {
			return year - 1;
		} else {
			return year;
		}
	}

	public int getTimeId(String time, int advanceMode) {
		// ĳһʱ���Ӧ��ʱ���
		int min = time2minute(time);
		for (int i = 0; i < 5; i++) {
			if (min >= (begintimemin[i] - getTimeDelay(advanceMode))
					&& min <= (endtimemin[i] + getTimeDelay(advanceMode))) {
				return i;
			}
		}
		return -1;
	}

	public int getCurrentTimeBlock(int advanceMode) {
		// ���ص�ǰʱ��Σ���������Ͽ�ʱ��Σ�����-1
		SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm");
		String time = sDateFormat.format(new java.util.Date());
		return getTimeId(time, advanceMode);
	}

	public static final int DelayDefault = 0;
	public static final int DelayAlarm = 1;
	public static final int DelaySilent = 2;

	public int getNextTimeBlock(int advanceMode) {
		// ���ؽ�Ҫ������ʱ���
		// advanceMode:0Ĭ�� 1������ǰʱ�� 2������ǰʱ��
		int timeInAdvance = 0;
		switch (advanceMode) {
		case DelayAlarm:
			timeInAdvance = Integer.valueOf(preferences.getString(
					"AlarmTimeBeforeLesson", "20"));
			break;
		case DelaySilent:
			timeInAdvance = Integer.valueOf(preferences.getString(
					"SilentDelay", "10"));
			break;
		}
		int min = getCurrentMinute();
		for (int i = 0; i < 5; i++) {
			if (min < (begintimemin[i] - timeInAdvance))
				return i;
		}
		return 5;
	}

	public static int time2minute(String time) {
		int hour = Integer.valueOf(time.substring(0, 2));
		int minute = Integer.valueOf(time.substring(3, 5));
		return hour * 60 + minute;
	}

	public static boolean checkTime(String time) {
		if (time.length() == 5) {
			if (Integer.valueOf(time.substring(0, 2)) >= 0
					&& Integer.valueOf(time.substring(0, 2)) < 24) {
				if (Integer.valueOf(time.substring(3, 5)) >= 0
						&& Integer.valueOf(time.substring(3, 5)) < 60) {
					return true;
				}
			}
		}
		return false;
	}

	public Lesson getCurrentLesson(int advanceMode) {
		// ��ǰʱ��Ŀ�
		LessonManager lessonManager = LessonManager.getInstance(context);
		return lessonManager.getLessonAt(weekDay,
				getCurrentTimeBlock(advanceMode));
	}

	public long getCurrentLessonEndTime(int time, int advanceMode) {
		// ��ǰʱ��Ŀν���ʱ��
		Calendar c = Calendar.getInstance();
		String t = endtime[time];
		int hour = Integer.valueOf(t.substring(0, 2));
		int min = Integer.valueOf(t.substring(3));
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis() + getTimeDelay(advanceMode) * 60 * 1000;
	}

	public long getCurrentLessonEndTime(Lesson lesson, int advanceMode) {
		if (canAppend(lesson)) {
			return Timetable.getInstance(context).getCurrentLessonEndTime(
					lesson.time + 1, advanceMode);
		} else {
			return Timetable.getInstance(context).getCurrentLessonEndTime(
					lesson.time, advanceMode);
		}

	}

	public long getNextTime(Lesson lesson, int advanceMode) {
		// ��һ���ϴ˿ε�ʱ�䣨���룩
		return Timetable.getInstance(context).getNextLessonBeginTime(lesson,
				advanceMode);
	}

	public long getNextEndTime(Lesson lesson, int advanceMode) {
		return Timetable.getInstance(context).getNextLessonEndTime(lesson,
				advanceMode);
	}

	public boolean canAppend(Lesson lesson) {
		// �������п�
		if (lesson.time == 0 || lesson.time == 2) {
			Lesson appendLesson = LessonManager.getInstance(context)
					.getLessonAt(lesson.week, lesson.time + 1);
			if (appendLesson != null) {
				if (appendLesson.lid == lesson.lid) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isAppended(Lesson lesson) {
		// ǰ�����п�
		if (lesson.time == 1 || lesson.time == 3) {
			Lesson appendLesson = LessonManager.getInstance(context)
					.getLessonAt(lesson.week, lesson.time - 1);
			if (appendLesson != null) {
				if (appendLesson.lid == lesson.lid) {
					return true;
				}
			}
		}
		return false;
	}

	public int appendMode(Lesson lesson) {
		if (canAppend(lesson))
			return 1;
		if (isAppended(lesson))
			return -1;
		return 0;
	}

	public Lesson getNextLesson(int advanceMode) {
		int week;
		int time = getNextTimeBlock(advanceMode);
		Lesson lesson;
		LessonManager lessonManager = LessonManager.getInstance(context);
		for (week = getCurrentWeekDay(); week < 7; week++) {
			while (time < 5) {
				lesson = lessonManager.getLessonAt(week, time);
				if (lesson != null && lesson.isInRange(numOfWeek)
						&& isNowHavingLesson(lesson) == -1
						&& !isAppended(lesson))
					return lesson;
				time++;
			}
			time = 0;
		}
		return null;
	}

	public long getNextLessonBeginTime(Lesson lesson, int advanceMode) {
		// ĳ���Ͽ�ʱ��(����)��������������
		int week = lesson.week;
		int time = lesson.time;
		Calendar c = Calendar.getInstance();
		String t = begintime[time];
		int weekOfMonth = c.get(Calendar.WEEK_OF_MONTH);
		int hour = Integer.valueOf(t.substring(0, 2));
		int min = Integer.valueOf(t.substring(3));
		c.setTimeInMillis(System.currentTimeMillis());
		if (isNowHavingLesson(week, time) != -1) {
			c.set(Calendar.WEEK_OF_MONTH, weekOfMonth + 1);
		}
		int sysWeek = normalWeek2SystemWeek(week);
		if (sysWeek == 1) {
			// ϵͳ�������������ܵ�������
			c.set(Calendar.WEEK_OF_MONTH, weekOfMonth + 1);
		}

		c.set(Calendar.DAY_OF_WEEK, sysWeek);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis() - getTimeDelay(advanceMode) * 60 * 1000;
	}

	public long getNextLessonEndTime(Lesson lesson, int advanceMode) {
		int week = lesson.week;
		int time = lesson.time;
		Calendar c = Calendar.getInstance();
		String t = endtime[time];
		int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
		int hour = Integer.valueOf(t.substring(0, 2));
		int min = Integer.valueOf(t.substring(3));
		c.setTimeInMillis(System.currentTimeMillis());
		if (isNowHavingLesson(week, time) != -1) {
			c.set(Calendar.DAY_OF_YEAR, dayOfYear + 7);
		}
		c.set(Calendar.DAY_OF_WEEK, normalWeek2SystemWeek(week));
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis() + getTimeDelay(advanceMode) * 60 * 1000;
	}

	public int isNowHavingLesson(int week, int time) {
		// ���ܱ���״̬
		// -1��û�ϣ�0�����ϣ�1�Ϲ���
		weekDay = getCurrentWeekDay();
		Calendar curCal = Calendar.getInstance();
		if (weekDay < week) {
			return -1;
		} else if (weekDay > week) {
			return 1;
		} else {
			int curMinute = curCal.get(Calendar.HOUR_OF_DAY) * 60
					+ curCal.get(Calendar.MINUTE);
			if (curMinute < begintimemin[time]) {
				return -1;
			} else if (curMinute >= begintimemin[time]
					&& curMinute <= endtimemin[time]) {
				return 0;
			} else {
				return 1;
			}
		}
	}

	public int isNowHavingLesson(Lesson lesson) {
		return isNowHavingLesson(lesson.week, lesson.time);
	}

	public int getTimeDelay(int advanceMode) {
		switch (advanceMode) {
		case DelayAlarm:
			return Integer.valueOf(preferences.getString(
					"AlarmTimeBeforeLesson", "20"));
		case DelaySilent:
			return Integer.valueOf(preferences.getString("SilentDelay", "10"));
		}
		return 0;
	}

	public static int systemWeek2NormalWeek(int sys) {
		int week = sys - 2;
		if (week == -1)
			return 6;
		return week;
	}

	public static int normalWeek2SystemWeek(int week) {
		if (week == 6)
			return 1;
		return week + 2;
	}

	public static String miliTime2String(long miliTime) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("MM��d�� HH:mm");
		Date date = new Date();
		date.setTime(miliTime);
		return sDateFormat.format(date);
	}

	public static int getCurrentWeekDay() {
		Calendar calendar = Calendar.getInstance();
		return systemWeek2NormalWeek(calendar.get(Calendar.DAY_OF_WEEK));
	}

	public static int getCurrentMinute() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.HOUR_OF_DAY) * 60
				+ calendar.get(Calendar.MINUTE);
	}

	public static boolean isValidWeek(int week) {
		return (week >= 0 && week <= 6) ? true : false;
	}

	public static boolean isValidTime(int time) {
		return (time >= 0 && time <= 4) ? true : false;
	}

	public static boolean isValidWeekTime(int week, int time) {
		return isValidWeek(week) && isValidTime(time);
	}

	public boolean nowIsAtLessonBreak(int week, int i) {
		// 0 �����ĽڿεĿμ䣬 2 �����ĽڿεĿμ�
		if (week != weekDay)
			return false;
		int min = getCurrentMinute();
		switch (i) {
		case 0:
			return (min >= endtimemin[0] && min <= begintimemin[1]) ? true
					: false;
		case 2:
			return (min >= endtimemin[2] && min <= begintimemin[3]) ? true
					: false;
		}
		return false;
	}

	public void updateTimetableSetting() throws Exception {
		TimetableSetting timetableSetting = AHUTAccessor.getInstance(context)
				.getTimetableSetting();
		setTimetableSetting(timetableSetting);
	}

	public void setTimetableSetting(TimetableSetting timetableSetting) {
		Editor editor = preferences.edit();
		editor.putInt("begin_date_year", timetableSetting.year);
		beginDate_year = timetableSetting.year;
		if (timetableSetting.month >= 1 && timetableSetting.month <= 12) {
			editor.putInt("begin_date_month", timetableSetting.month - 1);
			beginDate_month = timetableSetting.month - 1;
		}
		if (timetableSetting.day >= 1 && timetableSetting.day <= 31) {
			editor.putInt("begin_date_day", timetableSetting.day);
			beginDate_day = timetableSetting.day;
		}
		editor.commit();

		refreshNumOfWeek();
		setSeasonWinter(timetableSetting.seasonWinter);
	}

	public TimetableSetting getTimetableSetting() {
		TimetableSetting timetableSetting = new TimetableSetting();
		timetableSetting.year = beginDate_year;
		timetableSetting.month = beginDate_month + 1;
		timetableSetting.day = beginDate_day;
		timetableSetting.seasonWinter = seasonWinter;
		return timetableSetting;
	}

}
