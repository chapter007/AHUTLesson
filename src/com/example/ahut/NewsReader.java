package com.example.ahut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ahut_view.ToolbarActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;

public class NewsReader extends ToolbarActivity{
	private HttpEntity httpEntity;
	private HttpResponse httpResponse;
	private String jsData,title;
	private int	article_id;
	private WebView webView;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		webView=(WebView)findViewById(R.id.newsreader);
		WebSettings webSettings=webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		
		Intent intent=getIntent();
		article_id=intent.getIntExtra("id", 0);
		title=intent.getStringExtra("title");
		getSupportActionBar().setTitle(title);
		enableHomeButton();
		Log.i("wtf", ""+article_id);
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		HttpGet get=new HttpGet("http://php2333.sinaapp.com/list_content.php?id="+article_id);
		HttpClient httpClient=new DefaultHttpClient();
		try {
			httpResponse=httpClient.execute(get);
			httpEntity=httpResponse.getEntity();
			InputStream inputStream=httpEntity.getContent();
			BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
			
			String result=" ";
			String line="";
			while ((line=reader.readLine()) != null) {
				result=result+line;
			}
			jsData=result;
			Log.i("", "jsondata:"+jsData);
			try {
				JSONObject jsonObject=new JSONObject(jsData);
				JSONArray jsonArray=jsonObject.getJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2=jsonArray.getJSONObject(i);
					String content=jsonObject2.getString("content");
					webView.loadDataWithBaseURL("", content, "text/html", "utf-8", "");
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.newsreader;
	}
}
