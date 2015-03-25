package com.example.ahut;

import com.example.ahut_view.Lesson;
import com.example.ahut_view.Timetable;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SoundNormalReceiver extends BroadcastReceiver{
public void onReceive(Context context, Intent intent) {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		
		int ringerMode = preferences.getInt("savedRingerMode", AudioManager.RINGER_MODE_NORMAL);
		am.setRingerMode(ringerMode);
		Toast.makeText(context, "�ѻָ��Ͽ�ǰ����ģʽ", Toast.LENGTH_LONG).show();
		
		//������һ��
		boolean enableSilent = preferences.getBoolean("SilentMode", true);
		Lesson nextLesson = Timetable.getInstance(context).getNextLesson(Timetable.DelaySilent);
		if(nextLesson != null && enableSilent){
			AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			long alarmTime = Timetable.getInstance(context).getNextTime(nextLesson, Timetable.DelaySilent);
			Intent intent1 = new Intent(context,SoundSilentReceiver.class);
			intent1.putExtra("week", nextLesson.week);
			intent1.putExtra("time", nextLesson.time);
			PendingIntent sender1 = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.set(AlarmManager.RTC_WAKEUP, alarmTime, sender1);
		}
	}
}
