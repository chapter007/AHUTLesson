package com.ahutlesson.android;

import java.io.IOException;

import com.ahutlesson.android.lesson.Lesson;
import com.ahutlesson.android.lesson.LessonManager;
import com.ahutlesson.android.time.Timetable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class AlarmAlertActivity extends Activity {

	private MediaPlayer player;
	private Lesson lesson;
	private AlertDialog ad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(!Timetable.loaded)
			new Timetable(this);
		if(!LessonManager.loaded)
			new LessonManager(this);
		
		int week = getIntent().getExtras().getInt("week");
		int time = getIntent().getExtras().getInt("time");
		
		lesson = LessonManager.getLessonAt(week, time, this);
		if(lesson==null){
			this.finish();
		}
		
		int curTimeBlock = Timetable.getCurrentTimeBlock(Timetable.DelayDefault);
		if(curTimeBlock != -1 && LessonManager.lessons[lesson.week][curTimeBlock] != null){
			this.finish();//������������Ͽ�������
			return;
		}
		
		playMusic();
		
		String alertMessage = preferences.getString("MessageWhenNotice", "{TIME}��{LESSON}�Σ����Ͽ��ˣ���");
		alertMessage = alertMessage.replace("{TIME}", Timetable.lessontime_name[time]);
		alertMessage = alertMessage.replace("{LESSON}", lesson.alias);
		
		ad = new AlertDialog.Builder(AlarmAlertActivity.this)

				.setTitle("�Ͽ�����")
				.setMessage(alertMessage)
				.setPositiveButton("�ر�����",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								ad.dismiss();
								AlarmAlertActivity.this.finish();
								System.exit(0);
							}
						}).show();
	}
	
	public void playMusic() {
		if (player == null) {
			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			try {
				player = new MediaPlayer();
				player.setDataSource(this, uri);
				final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
					player.setAudioStreamType(AudioManager.STREAM_ALARM);
					player.setLooping(true);
					player.prepare();
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!player.isPlaying()) {
			player.start();
		}
	}

}
