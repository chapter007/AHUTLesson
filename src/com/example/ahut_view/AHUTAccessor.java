package com.example.ahut_view;

import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.ahut_user.LessonListInfo;
import com.example.ahut_user.LessonManager;
import com.example.ahut_user.User;
import com.example.ahut_user.UserInfo;
import com.example.ahut_user.UserManager;
import com.example.ahut_util.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class AHUTAccessor {
	private static AHUTAccessor accessor;
	private static Context context;

	private static final int TIMEOUT_CONNECTION = 5000;
	private static final int TIMEOUT_SOCKET = 10000;
	private HttpParams httpParameters = new BasicHttpParams();;

	public static final String SERVER_URL = "http://ahutlesson.sinaapp.com/";

	// public static final String SERVER_URL = "http://192.168.170.50/lesson/";

	public AHUTAccessor(Context context0) {
		context = context0;

		HttpConnectionParams.setConnectionTimeout(httpParameters,
				TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
	}

	public static AHUTAccessor getInstance(Context context) {
		if (accessor == null) {
			accessor = new AHUTAccessor(context);
		}
		return accessor;
	}

	public JSONObject getURL(String URL) throws Exception {
		HttpGet request = new HttpGet(URL);
		String cookie = UserManager.getInstance(context).getCookie();
		if (cookie != null) {
			request.addHeader("Cookie", "ck=" + cookie);
		}
		String strResult = null;
		try {
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient(
					httpParameters);
			HttpResponse httpResponse = defaultHttpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
				strResult = new String(strResult.getBytes("ISO-8859-1"),
						"UTF-8");
			}
			defaultHttpClient.getConnectionManager().shutdown();
			Util.log(URL);
			Util.log(strResult);
		} catch (SocketTimeoutException e) {
			throw new Exception("���ӳ�ʱ�����Ժ�����");
		} catch (Exception e) {
			throw new Exception("���ӷ�����ʧ�ܣ�������������");
		}
		try {
			JSONTokener jsonParser = new JSONTokener(strResult);
			JSONObject ret = (JSONObject) jsonParser.nextValue();
			int retCode = ret.getInt("code");
			if (retCode == 1) {
				String msg = ret.getString("msg");
				Log.i("","����ʲô��"+msg);
				throw new Exception(msg);
			} else if (retCode == 0) {
				return ret;
			}
		} catch (JSONException e) {
			throw new Exception("�������ݳ���");
		}
		throw new Exception("�����������˴��������");
	}

	public JSONObject postURL(String URL, List<NameValuePair> params)
			throws Exception {
		HttpPost request = new HttpPost(URL);
		String cookie = UserManager.getInstance(context).getCookie();
		if (cookie != null) {
			request.addHeader("Cookie", "ck=" + cookie);
		}
		String strResult = null;
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient(
					httpParameters);
			HttpResponse httpResponse = defaultHttpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
				strResult = new String(strResult.getBytes("ISO-8859-1"),
						"UTF-8");
			}
			defaultHttpClient.getConnectionManager().shutdown();
			Util.log(URL);
			Util.log(strResult);
		} catch (SocketTimeoutException e) {
			throw new Exception("���ӳ�ʱ�����Ժ�����");
		} catch (Exception e) {
			throw new Exception("���ӷ�����ʧ�ܣ�������������");
		}
		JSONTokener jsonParser = new JSONTokener(strResult);
		try {
			JSONObject ret = (JSONObject) jsonParser.nextValue();
			int retCode = ret.getInt("code");
			if (retCode == 1) {
				String msg = ret.getString("msg");
				throw new Exception(msg);
			} else if (retCode == 0) {
				return ret;
			}
		} catch (JSONException e) {
			throw new Exception("�������ݳ���");
		}
		throw new Exception("�����������˴��������");
	}

	public String regsterUser(String uxh, String password) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("x", uxh));
		params.add(new BasicNameValuePair("p", password));
		JSONObject ret = postURL(SERVER_URL
				+ "api/user.handler.php?act=register", params);
		String cookie = ret.getString("data");
		return cookie;
	}

	public String validateUser(String uxh, String password) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("x", uxh));
		params.add(new BasicNameValuePair("p", password));
		JSONObject ret = postURL(SERVER_URL + "api/user.handler.php?act=login",
				params);
		String cookie = ret.getString("data");
		return cookie;
	}

	public User getLoginUserInfo() throws Exception {
		User user = new User();
		JSONObject ret = getURL(SERVER_URL
				+ "api/user.handler.php?act=getloginuserinfo");
		JSONObject userInfo = ret.getJSONObject("data");
		try {
			user.uxh = userInfo.getString("uxh");
			user.uname = userInfo.getString("uname");
			user.bj = userInfo.getString("bj");
			user.password = userInfo.getString("password");
			user.signature = userInfo.getString("signature");
			user.isAdmin = (userInfo.getInt("is_admin") == 1);
		} catch (Exception e) {
			throw new Exception("�����û���½��Ϣ���ݳ���");
		}
		return user;
	}

	public LessonListInfo getLessonList(String uxh) throws Exception {
		JSONObject ret = getURL(SERVER_URL + "api/getlessonlist.php?xh=" + uxh);
		LessonListInfo info = new LessonListInfo();
		ArrayList<Lesson> lessonList = new ArrayList<Lesson>();
		try {
			JSONArray lessons = ret.getJSONArray("data");
			JSONObject lesson;
			for (int i = 0; i < lessons.length(); i++) {
				lesson = lessons.getJSONObject(i);
				int lid = lesson.getInt("lid");
				String lessonName = lesson.getString("lessonname");
				String teacherName = lesson.getString("teachername");
				int week = lesson.getInt("week");
				int time = lesson.getInt("time");
				int startweek = lesson.getInt("startweek");
				int endweek = lesson.getInt("endweek");
				String lessonPlace = lesson.getString("place");
				lessonList.add(new Lesson(lid, lessonName, lessonPlace,
						teacherName, startweek, endweek, week, time));
			}
			if (lessonList.size() == 0)
				throw new Exception("��ѧ�ſα�Ϊ�գ�����ѧ���Ƿ�������߷���");
			info.lessonList = lessonList;
			JSONObject metadata = ret.getJSONObject("metadata");
			info.xm = metadata.getString("xm");
			info.build = metadata.getString("build");
			return info;
		} catch (Exception ex) {
			throw new Exception("�����α����ݳ���");
		}
	}

	public LessonsInfo getLessons(String uxh) throws Exception {
		LessonListInfo lessonListInfo = getLessonList(uxh);
		LessonsInfo lessonsInfo = new LessonsInfo();
		Lesson[][] lessons = new Lesson[7][5];
		for (Lesson lesson : lessonListInfo.lessonList) {
			lessons[lesson.week][lesson.time] = lesson;
		}
		lessonsInfo.lessons = lessons;
		lessonsInfo.xm = lessonListInfo.xm;
		return lessonsInfo;
	}

	public static String getAvatarURI(String uxh) {
		return SERVER_URL + "api/getavatar.php?uxh=" + uxh;
	}

	public ArrayList<LessonmateSimilarity> getLessonmateSimilarity(String xh) throws Exception {
		JSONObject ret = getURL(SERVER_URL + "api/getlessonmatesimilarity.php?xh="
				+ xh);
		ArrayList<LessonmateSimilarity> list = new ArrayList<LessonmateSimilarity>();
		try {
			JSONArray data = ret.getJSONArray("data");
			JSONObject obj;
			for (int i = 0; i < data.length(); i++) {
				obj = data.getJSONObject(i);
				LessonmateSimilarity l = new LessonmateSimilarity();
				l.xh = obj.getString("xh2");
				l.similarity = obj.getDouble("similarity");
				list.add(l);
			}
			return list;
		} catch (Exception ex) {
			throw new Exception("�������ݳ���");
		}
	}
	
	public ArrayList<Lessonmate> getLessonmateList(int lid, int page)
			throws Exception {
		JSONObject ret = getURL(SERVER_URL + "api/getlessonmates.php?lid="
				+ lid + "&page=" + page);
		ArrayList<Lessonmate> list = new ArrayList<Lessonmate>();
		try {
			JSONArray lessonmates = ret.getJSONArray("data");
			JSONObject lessonmate;
			for (int i = 0; i < lessonmates.length(); i++) {
				lessonmate = lessonmates.getJSONObject(i);
				Lessonmate l = new Lessonmate();
				l.xh = lessonmate.getString("xh");
				l.xm = lessonmate.getString("xm");
				l.zy = lessonmate.getString("zy");
				l.bj = lessonmate.getString("bj");
				l.registered = (lessonmate.getInt("registered") == 1);
				l.hasAvatar = (lessonmate.getInt("has_avatar") == 1);
				l.signature = lessonmate.getString("signature");
				list.add(l);
			}
			JSONObject metadata = ret.getJSONObject("metadata");
			LessonmateActivity.lessonmatesPerPage = metadata
					.getInt("lessonmatesPerPage");
			return list;
		} catch (Exception ex) {
			throw new Exception("�������ݳ���");
		}
	}

	public void uploadAvatar(Bitmap bm) throws Exception {
		String strResult = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, 90, bos);
		byte[] data = bos.toByteArray();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost request = new HttpPost(SERVER_URL + "api/uploadavatar.php");
		String cookie = UserManager.getInstance(context).getCookie();
		if (cookie != null) {
			request.addHeader("Cookie", "ck=" + cookie);
		} else
			throw new Exception("��δ��¼��");
		try {
			ByteArrayBody bab = new ByteArrayBody(data, "avatar.jpg");
			MultipartEntity reqEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("avatar_file", bab);
			request.setEntity(reqEntity);
			HttpResponse httpResponse = httpClient.execute(request);
			strResult = EntityUtils.toString(httpResponse.getEntity());
			strResult = new String(strResult.getBytes("ISO-8859-1"), "UTF-8");
			Util.log(strResult);
		} catch (Exception e) {
			throw e;
		}
		try {
			JSONTokener jsonParser = new JSONTokener(strResult);
			JSONObject ret = (JSONObject) jsonParser.nextValue();
			int retCode = ret.getInt("code");
			if (retCode == 1) {
				String msg = ret.getString("msg");
				throw new Exception(msg);
			} else if (retCode == 0) {
				return;
			}
		} catch (JSONException e) {
			throw new Exception("�������ݳ���");
		}
		throw new Exception("�����������˴��������");
	}

	public void setSignature(String signature) throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("s", signature));
		postURL(SERVER_URL + "api/user.handler.php?act=setsignature", params);
		return;
	}

	public UserInfo getUserInfo(String uxh) throws Exception {
		UserInfo userInfo = new UserInfo();
		JSONObject ret = getURL(SERVER_URL
				+ "api/user.handler.php?act=getuserinfo&uxh=" + uxh);
		try {
			JSONObject userInfoObject = ret.getJSONObject("data");
			userInfo.uxh = userInfoObject.getString("uxh");
			userInfo.uname = userInfoObject.getString("uname");
			userInfo.signature = userInfoObject.getString("signature");
			userInfo.hasAvatar = (userInfoObject.getInt("has_avatar") == 1);
			userInfo.isAdmin = (userInfoObject.getInt("is_admin") == 1);
			userInfo.xb = userInfoObject.getString("xb");
			userInfo.bj = userInfoObject.getString("bj");
			userInfo.zy = userInfoObject.getString("zy");
			userInfo.xy = userInfoObject.getString("xy");
			userInfo.rx = userInfoObject.getInt("rx");
			userInfo.registerTime = userInfoObject.getString("register_time");
			userInfo.lastloginTime = userInfoObject.getString("lastlogin_time");
		} catch (Exception e) {
			throw new Exception("��������");
		}
		return userInfo;
	}

	public TimetableSetting getTimetableSetting() throws Exception {
		TimetableSetting timetableSetting = new TimetableSetting();
		JSONObject ret = getURL(SERVER_URL + "api/gettimetable.php");
		try {
			JSONObject retObject = ret.getJSONObject("data");
			timetableSetting.year = retObject.getInt("year");
			timetableSetting.month = retObject.getInt("month");
			timetableSetting.day = retObject.getInt("day");
			timetableSetting.setSeason(retObject.getInt("season"));
		} catch (Exception e) {
			throw new Exception("����ʱ����������ݳ���");
		}
		return timetableSetting;
	}

	public JSONObject checkUpdate() throws Exception {
		Timetable timetable = Timetable.getInstance(context);
		TimetableSetting timetableSetting = timetable.getTimetableSetting();
		LessonManager lessonManager = LessonManager.getInstance(context);
		ChangeLog cl = new ChangeLog(context);
		String appVer = cl.getThisVersion();
		String lessondbVer = lessonManager.getLessondbVersion();
		String timetableParam = "ty=" + timetableSetting.year + "&tm="
				+ timetableSetting.month + "&td=" + timetableSetting.day
				+ "&ts=" + timetableSetting.getSeason();
		JSONObject ret = getURL(SERVER_URL
				+ "api/update.handler.php?act=check&a=" + appVer + "&l="
				+ lessondbVer + "&" + timetableParam);
		return ret.getJSONObject("data");
	}
}
