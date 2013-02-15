package com.ahutlesson.android.receiver;

import com.ahutlesson.android.AlarmAlertActivity;
import com.ahutlesson.android.LessonActivity;
import com.ahutlesson.android.R;
import com.ahutlesson.android.lesson.Lesson;
import com.ahutlesson.android.lesson.LessonManager;
import com.ahutlesson.android.time.Timetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.Notification;

public class AlarmReceiver extends BroadcastReceiver {

	private SharedPreferences preferences;
	private Context context;
	private int week, time;
	private boolean enableAlert, enableNotification, enableSound,
			enableNotificationSound;

	@Override
	public void onReceive(Context context0, Intent intent0) {
		// �ӵ��Ͽι㲥
		context = context0;

		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		enableAlert = preferences.getBoolean("NoticeBeforeLesson", true);
		if (!enableAlert)
			return;

		enableNotification = preferences.getBoolean(
				"SendNotificationWhenNotice", false);
		enableSound = preferences.getBoolean("PlaySoundWhenNotice", false);
		enableNotificationSound = preferences.getBoolean(
				"NotificationSoundWhenNotice", false);

		week = intent0.getExtras().getInt("week");
		time = intent0.getExtras().getInt("time");

		addNextLessonAlarm();

		if (enableNotification) {
			pushNotification();
		}

		if (enableSound) {
			Intent i = new Intent(context, AlarmAlertActivity.class);
			i.putExtra("week", week);
			i.putExtra("time", time);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}

	}

	private void addNextLessonAlarm() {
		// �����½ڿ�����
		Lesson nextLesson = Timetable.getInstance(context).getNextLesson(Timetable.DelayAlarm);
		if (nextLesson != null) {
			AlarmManager alarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			long alarmTime = nextLesson.getNextTime(Timetable.DelayAlarm);
			Intent intent = new Intent(context, AlarmReceiver.class);
			intent.putExtra("week", nextLesson.week);
			intent.putExtra("time", nextLesson.time);
			intent.putExtra("LessonInfo", nextLesson.toString());
			PendingIntent sender = PendingIntent.getBroadcast(context, 0,
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.set(AlarmManager.RTC_WAKEUP, alarmTime, sender);
		}
	}

	private void pushNotification() {
		Lesson lesson = LessonManager.getInstance(context).getLessonAt(week, time);
		if (lesson == null)
			return;
		
		Intent i = new Intent(context, LessonActivity.class);
		i.putExtra("week", week);
		i.putExtra("time", time);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		String notice = Timetable.getInstance(context).lessontime_name[time] + "��" + lesson.alias
				+ "�Σ��ص㣺" + lesson.place;
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification.Builder(context)
	        .setContentTitle("�Ͽ�����")
	        .setContentText(notice)
	        .setContentIntent(pendingIntent)
	        .setSmallIcon(R.drawable.calendar)
			.build();
		n.flags = Notification.FLAG_AUTO_CANCEL;
		if (enableNotificationSound) {
			n.defaults |= Notification.DEFAULT_SOUND;
		}
		n.defaults |= Notification.DEFAULT_VIBRATE;// VIBRATE
		n.flags |= Notification.FLAG_SHOW_LIGHTS;
		n.ledARGB = 0xff00ff00;
		n.ledOnMS = 300;
		n.ledOffMS = 1000;// LED
		nm.notify(0, n);
	}
}
