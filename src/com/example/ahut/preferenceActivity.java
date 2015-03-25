package com.example.ahut;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.example.ahut_user.LessonManager;
import com.example.ahut_user.UserManager;
import com.example.ahut_view.Timetable;
import com.example.ahut_view.TimetableSetting;
import com.example.ahut_view.ToolbarActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class preferenceActivity extends ToolbarActivity {
	
	private SettingsFragments msettingsFragments;
	static ProgressDialog progressDialog;
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enableHomeButton();
		if (savedInstanceState==null) {
			msettingsFragments=new SettingsFragments();
			replaceFragment(R.id.setting, msettingsFragments);
		}
		

	}

	public static class SettingsFragments extends PreferenceFragment{
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			
			Preference setBeginDate = (Preference) findPreference("set_begin_date");
			setBeginDate
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference arg0) {
							final Timetable timetable = Timetable
									.getInstance(getActivity());
							final TimetableSetting timetableSetting = timetable
									.getTimetableSetting();
							DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker datePicker,
										int year, int month, int dayOfMonth) {
									timetableSetting.year = year;
									timetableSetting.month = month + 1;
									timetableSetting.day = dayOfMonth;
									timetable.setTimetableSetting(timetableSetting);
								}
							};
							DatePickerDialog dialog = new DatePickerDialog(
									getActivity(), dateListener,
									timetableSetting.year,
									timetableSetting.month - 1,
									timetableSetting.day);
							dialog.show();
							return false;
						}

					});

			CheckBoxPreference seasonWinter = (CheckBoxPreference) findPreference("season_winter");
			seasonWinter
					.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

						@Override
						public boolean onPreferenceChange(Preference arg0,
								Object arg1) {
							Timetable.getInstance(getActivity())
									.toggleSeason();
							return true;
						}
					});

			Preference downDB = (Preference) findPreference("down_lesson");
			downDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				public boolean onPreferenceClick(Preference preference) {
					confirm("下载课表会清空现有的课表，继续吗？", new Runnable() {
						@Override
						public void run() {
							new DownLesson().execute();
						}
					});
					return true;
				}

			});

			Preference downUserInfo = (Preference) findPreference("user_downinfo");
			downUserInfo
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							new DownUserInfo().execute();
							return true;
						}

					});

			Preference userLogout = (Preference) findPreference("user_logout");
			userLogout
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							confirm("确定要注销账户吗？", new Runnable() {
								@Override
								public void run() {
									clearImageCache();
									SharedPreferences preferences = PreferenceManager
											.getDefaultSharedPreferences(getActivity());
									preferences.edit().clear().commit();
									Intent i = getActivity().getBaseContext().getPackageManager()
											.getLaunchIntentForPackage(
													getActivity().getBaseContext()
															.getPackageName());
									i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(i);
								}
							});
							return true;
						}
					});

			Preference delDB = (Preference) findPreference("delete_db");
			delDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				public boolean onPreferenceClick(Preference preference) {
					confirm("确定要清空课表吗？", new Runnable() {
						@Override
						public void run() {
							LessonManager.getInstance(getActivity())
									.deleteDB();
						}
					});
					return true;
				}
			});

			Preference clearImageCache = (Preference) findPreference("clear_image_cache");
			clearImageCache
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							clearImageCache();
							alert("图片缓存已清除");
							return true;
						}
					});
		}
		
		private class DownUserInfo extends AsyncTask<Integer, Integer, String> {
			@Override
			protected String doInBackground(Integer... para) {
				clearImageCache();
				try {
					UserManager.getInstance(getActivity())
							.updateUserInfo();
					return null;
				} catch (Exception e) {
					return e.getMessage();
				}
			}

			@Override
			protected void onPostExecute(String ret) {
				if (ret != null) {
					alert(ret);
				} else {
					alert("下载成功！");
				}
			}
		}
		
		class DownLesson extends AsyncTask<Integer, Integer, String> {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = ProgressDialog.show(getActivity(),
						"请稍等", "下载课表中...", true);
			}

			@Override
			protected String doInBackground(Integer... para) {
				try {
					UserManager.getInstance(getActivity())
							.updateLessonDB();
					return null;
				} catch (Exception e) {
					return e.getMessage();
				}
			}

			@Override
			protected void onPostExecute(String ret) {
				try {
					progressDialog.dismiss();
					progressDialog = null;
				} catch (Exception e) {
				}
				if (ret != null) {
					alert(ret);
				} else {
					alert("下载成功！");
				}
			}
		}
		
		public void alert(String message) {
			new AlertDialog.Builder(getActivity()).setMessage(message)
					.setPositiveButton(R.string.ok, null).show();
		}
		
		public void clearImageCache() {
			GlobalContext.initImageLoader();
			ImageLoader.getInstance().clearMemoryCache();
		}
		
		@Override
		public void onPause() {
			try {
				progressDialog.dismiss();
				progressDialog = null;
			} catch (Exception e) {
			}
			super.onPause();
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case android.R.id.home:
				//finish();
				return (true);
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		public void confirm(String message, final Runnable r) {
			new AlertDialog.Builder(getActivity())
					.setMessage(message)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									r.run();
								}
							}).setNegativeButton(R.string.cancel, null).show();
		}
	}
	
	public void replaceFragment(int viewId,android.app.Fragment fragment) {
		FragmentManager fragmentManager=getFragmentManager();
		fragmentManager.beginTransaction().replace(viewId, fragment).commit();
	}


	@Override
	protected int getLayoutResource() {
		return R.layout.preference_activity;
	}

}
