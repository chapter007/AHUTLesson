package com.ahutlesson.android.time;

import com.ahutlesson.android.lesson.Lesson;
import com.ahutlesson.android.lesson.LessonManager;
import com.ahutlesson.android.receiver.AlarmReceiver;
import com.ahutlesson.android.receiver.SoundSilentReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Alert {

	private Context context;
	private SharedPreferences preferences;
	private AlarmManager am;
	private boolean enableAlert,enableSilent;

	public Alert(Context context0){
		context = context0;
		if(!LessonManager.loaded)
			new LessonManager(context);
		if(!Timetable.loaded)
			new Timetable(context);
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		enableAlert = preferences.getBoolean("NoticeBeforeLesson", true);
		enableSilent = preferences.getBoolean("SilentMode", true);
	}
	
	
	public void setAlarm(){
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Lesson nextLesson = Timetable.getNextLesson(Timetable.DelayAlarm);
		Intent intent;
		if(nextLesson!=null&&enableAlert){
				long alarmTime = nextLesson.getNextTime(Timetable.DelayAlarm);
				intent = new Intent(context,AlarmReceiver.class);
				intent.putExtra("week", nextLesson.week);
				intent.putExtra("time", nextLesson.time);
				PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, alarmTime, sender);
		}
		nextLesson = Timetable.getNextLesson(Timetable.DelaySilent);
		if(nextLesson!=null&&enableSilent){
				long alarmTime = nextLesson.getNextTime(Timetable.DelaySilent);
				intent = new Intent(context,SoundSilentReceiver.class);
				intent.putExtra("week", nextLesson.week);
				intent.putExtra("time", nextLesson.time);
				PendingIntent sender1 = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP, alarmTime, sender1);
		}
	}
}