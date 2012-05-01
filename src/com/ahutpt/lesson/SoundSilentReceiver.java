package com.ahutpt.lesson;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class SoundSilentReceiver extends BroadcastReceiver {
	
	private SharedPreferences preferences;
	private Context context;
	private int week,time;
	private boolean enableSilent,enableVibrate;

	@Override
	public void onReceive(Context context0, Intent intent) {
		context = context0;
		
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		enableSilent = preferences.getBoolean("SilentMode", true);
		enableVibrate = preferences.getBoolean("VibrateWhenSilentMode", false);

		week = intent.getExtras().getInt("week");
		time = intent.getExtras().getInt("time");;

		if(enableSilent){
			AudioManager am=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			if(enableVibrate){
				Log.i("ahutlesson", "����Ϊ��������");
			    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			}else{
				Log.i("ahutlesson", "����Ϊ����");
			    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
			
			setCancelSilent(week,time);
		}
	}

	private void setCancelSilent(int week,int time) {
		// �ָ���������
		Log.i("ahutlesson", "����ȡ�� " + "week: " + week + ", time: " + time);
		Timetable timetable = new Timetable(context);
		Lesson nextLesson = timetable.getCurrentLesson();
		long alarmTime = nextLesson.getNextEndTime();
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		int silentDelay = Integer.valueOf(preferences.getString("SilentDelay", "10"));
		Intent intent = new Intent(context,SoundNormalReceiver.class);
		intent.putExtra("week", week);
		intent.putExtra("time", time);
		intent.putExtra("mode", "cancel");
		PendingIntent sender1 = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, alarmTime + silentDelay * 60 * 1000 , sender1);
	}
}
