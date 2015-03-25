package com.example.ahut;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.ahut_db.DatabaseHelper;
import com.example.ahut_db.DbManager;
import com.example.ahut_db.articles;
import com.example.ahut_view.ToolbarActivity;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Net extends ToolbarActivity{
	private ListView list;
	private HttpResponse response;
	private HttpEntity httpEntity;
	private String jsonData;
	private int[] ids;
	private String[] title1;
	private View footerView;
	private int length,last_id;
	private DbManager mgr;
	private SimpleAdapter adapter;
	List<Map<String, Object>> data=new ArrayList<Map<String, Object>>();
	
	@SuppressWarnings("unchecked")
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enableHomeButton();
		
		list=(ListView) findViewById(R.id.list_news);
		footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.listview_footer, null, false);
		footerView.findViewById(R.id.tvNextPage);

		mgr=new DbManager(this);
		Log.i("main", "create?");
		if (mgr.check()) {
			//有数据，先从数据库里面获取
			query();
		}else {
			//没有数据，就是第一次加载
			data=(List<Map<String, Object>>) getData();
			add();
		}
		
	
		adapter=new SimpleAdapter(this, data, R.layout.list_content, 
        		new String[]{"title"}, new int[]{R.id.text});
		list.addFooterView(footerView);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent=new Intent(Net.this,NewsReader.class);
				
				Log.i("","先看arg："+arg2+"看看id："+ids[arg2]);
				Log.i("","看看title："+title1[arg2]);
				intent.putExtra("id", ids[arg2]);
				intent.putExtra("title",title1[arg2]);
				startActivity(intent);
			}
		});
		footerView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Log.i("", "即将传值的last_id:"+last_id);
				getData(last_id);
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	protected void onDestroy() {
		Log.i("sql", "destroy");
        super.onDestroy();  
        //应用的最后一个Activity关闭时应释放DB  
        mgr.closeDB();  
    }
	
	ArrayList<articles> articles=new ArrayList<articles>();
	public void add() {
		Log.i("sql", "add");
		getData();
		mgr.add(articles);
	}
	
	public void query() {
		Log.i("sql", "query");
		List<articles> articles = mgr.query();
		ids=new int[100];
		title1=new String[100];
		int i=0;
		for (articles article : articles) {
        	Map<String, Object> map=new HashMap<String, Object>();
        	ids[i]=article.id;
			title1[i]=article.title;
			i++;
        	map.put("id", article.id);
            map.put("title", article.title);  
            data.add(map);
        }
		last_id=ids[19];
	}
	
	@SuppressWarnings("unchecked")
	public void update() {
		Log.i("update", "up?");
		data=(List<Map<String, Object>>) getData();
		mgr.updateArticle(articles);
	}
	
	private List<? extends Map<String, ?>> getData() {
		data.clear();
		articles.clear();
		HttpGet get=new HttpGet("http://php2333.sinaapp.com/list.php");
		HttpClient httpClient=new DefaultHttpClient();
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		try {
			InputStream inputStream=null;
			response=httpClient.execute(get);
			httpEntity=response.getEntity();
			inputStream=httpEntity.getContent();
			BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
			
			String result=" ";
			String line="";
			while ((line=reader.readLine()) != null) {
				result=result+line;
			}
			jsonData=result;
			JSONObject jsonObject=new JSONObject(jsonData);
			JSONArray news = jsonObject.getJSONArray("data");
			
			length=news.length();
			ids=new int[100];
			title1=new String[100];
			for(int i = 0;i<length;i++)
			{
				Map<String, Object> map=new HashMap<String, Object>();
				articles a=new articles();
				JSONObject jsonObject1=news.getJSONObject(i);
				int id = jsonObject1.getInt("id");
				String title=jsonObject1.getString("title");
		        ids[i]=id;
		        title1[i]=title;
		        a.id=id;
		        a.title=title;
		        Log.i("info", "id:"+a.id+"title:"+a.title);
		        map.put("title", title);
		        map.put("id", id);
		        articles.add(a);
		        data.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		last_id=ids[length-1];
		Log.d("data", "data:"+data);
		return data;
	}

	private List<? extends Map<String, ?>> getData(int id1) {
		HttpGet get=new HttpGet("http://php2333.sinaapp.com/list_more.php?page="+id1);
		HttpClient httpClient=new DefaultHttpClient();
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		try {
			InputStream inputStream=null;
			response=httpClient.execute(get);
			httpEntity=response.getEntity();
			inputStream=httpEntity.getContent();
			BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
			
			String result=" ";
			String line="";
			while ((line=reader.readLine()) != null) {
				result=result+line;
			}
			jsonData=result;
			JSONObject jsonObject=new JSONObject(jsonData);
			JSONArray news = jsonObject.getJSONArray("data");
			
			for(int i = 0;i<news.length();i++)
			{
				Map<String, Object> map=new HashMap<String, Object>();
				JSONObject jsonObject1=news.getJSONObject(i);
				int id = jsonObject1.getInt("id");
				String title=jsonObject1.getString("title");
		        ids[i+last_id]=id;
		        title1[i+last_id]=title;
		        map.put("title", title);
		        data.add(map);
			}
			last_id=last_id+news.length();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.refresh, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId()==R.id.refresh) {
			refresh();
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("unchecked")
	@SuppressLint("ShowToast")
	private void refresh() {
		new getNews().execute();
		
	}
	
	private ProgressDialog progressDialog;
	public class getNews extends AsyncTask<String, integer, String>{

		@Override
		protected void onPreExecute() {
			progressDialog=ProgressDialog.show(Net.this, "正在刷新", "刷新很快将完成");
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			getData();
			update();
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			adapter.notifyDataSetChanged();
			progressDialog.dismiss();
			progressDialog=null;
			super.onPostExecute(result);
		}
	}
	@Override
	protected int getLayoutResource() {
		return R.layout.net;
	}
}

