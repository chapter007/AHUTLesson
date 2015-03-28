package com.example.ahut;

import java.io.IOException;

import com.example.ahut_user.LessonManager;
import com.example.ahut_view.Lesson;
import com.example.ahut_view.Timetable;
import com.example.ahut_view.ToolbarActivity;

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

public class AlarmActivity extends ToolbarActivity{
	private MediaPlayer player;
	private Lesson lesson;
	private AlertDialog ad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Timetable timetable = Timetable.getInstance(this);
		LessonManager lessonManager = LessonManager.getInstance(this);

		int week = getIntent().getExtras().getInt("week");
		int time = getIntent().getExtras().getInt("time");

		lesson = lessonManager.getLessonAt(week, time);
		if (lesson == null) {
			this.finish();
		}

		int curTimeBlock = timetable
				.getCurrentTimeBlock(Timetable.DelayDefault);
		if (curTimeBlock != -1
				&& lessonManager.getLessonAt(lesson.week, curTimeBlock) != null) {
			this.finish();// ������������Ͽ�������
			return;
		}

		playMusic();

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String alertMessage = preferences.getString("MessageWhenAlarm",
				"{TIME}��{LESSON}�Σ����Ͽ��ˣ���");
		alertMessage = alertMessage.replace("{TIME}",
				timetable.lessontimeName[time]);
		alertMessage = alertMessage.replace("{LESSON}", lesson.name);

		ad = new AlertDialog.Builder(AlarmActivity.this)

				.setTitle("�Ͽ�����")
				.setMessage(alertMessage)
				.setPositiveButton("�ر�����",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								ad.dismiss();
								AlarmActivity.this.finish();
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
				final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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

	@Override
	protected int getLayoutResource() {
		return 0;
	}
}