package com.ahutlesson.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ahutlesson.android.api.AHUTAccessor;
import com.ahutlesson.android.model.Message;
import com.ahutlesson.android.ui.MessageAdapter;

public class MessageActivity extends BaseActivity {

	private static final int MENU_REFRESH = 0;
	private static final int MENU_REPLY = 1;
	private static final int MENU_DELETE = 2;
	
	private LinearLayout layoutLoading, layoutList, layoutEmpty;
	private ListView lvList;
	private MessageAdapter lvMessageAdapter;
	private ArrayList<Message> list = new ArrayList<Message>();
	private int page = 1;
	public static int messagesPerPage = 1;
	private View footerView;
	private TextView tvNextPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actionBar.setLogo(R.drawable.message);
		
		setContentView(R.layout.list);
		
		layoutLoading = (LinearLayout) findViewById(R.id.layoutLoading);
		layoutList = (LinearLayout) findViewById(R.id.layoutList);
		layoutEmpty = (LinearLayout) findViewById(R.id.layoutEmpty);
		
		TextView tvEmpty = (TextView) layoutEmpty.findViewById(R.id.tvEmpty);
		tvEmpty.setText("������Ϣ");

		footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.listview_footer, null, false);
		tvNextPage = (TextView) footerView.findViewById(R.id.tvNextPage);
		footerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new LoadNextPage().execute();
			}
		});

		lvList = (ListView) layoutList.findViewById(R.id.lvList);
		lvMessageAdapter = new MessageAdapter(MessageActivity.this,
				R.layout.message_item, list);
		lvList.addFooterView(footerView);
		lvList.setAdapter(lvMessageAdapter);
		
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openContextMenu(view);
			}
		});
		registerForContextMenu(lvList);
		
		new LoadData().execute();
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,  
            ContextMenuInfo menuInfo) {  
		menu.add(Menu.NONE, MENU_REPLY, Menu.NONE, "�ظ�");  
		menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "ɾ��");  
		super.onCreateContextMenu(menu, v, menuInfo);  
	}

	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final Message n = list.get(info.position);
		if(n == null) return true;
		switch (item.getItemId()) {
		case MENU_REPLY:
			Intent i = new Intent(MessageActivity.this, NewMessageActivity.class);
			i.putExtra("uxh", n.fromUxh);
			startActivity(i);
			return true;
		case MENU_DELETE:
			new AlertDialog.Builder(MessageActivity.this)
			.setTitle("ɾ����Ϣ")
			.setMessage("ȷ��ɾ��������Ϣ��")
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new DeleteMessage().execute(n.mid);
					
				}
			}).setNegativeButton(R.string.cancel, null).show();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, R.string.refresh)
			.setIcon(R.drawable.refresh)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			new LoadData().execute();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	
	public void showData() {
		layoutLoading.setVisibility(View.GONE);
		layoutList.setVisibility(View.VISIBLE);
		lvMessageAdapter.notifyDataSetChanged();
	}

	private class LoadData extends AsyncTask<Integer, Integer, ArrayList<Message>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			layoutLoading.setVisibility(View.VISIBLE);
			layoutList.setVisibility(View.GONE);
			layoutEmpty.setVisibility(View.GONE);
			list.clear();
		}

		@Override
		protected ArrayList<Message> doInBackground(Integer... param) {
			page = 1;
			try {
				return AHUTAccessor.getInstance(MessageActivity.this).getMessageList(page);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Message> ret) {
			layoutLoading.setVisibility(View.GONE);
			if(ret == null) return;

			if(ret.size() == 0) {
				layoutEmpty.setVisibility(View.VISIBLE);
				return;
			}
			
			if(ret.size() == messagesPerPage) {
				tvNextPage.setText("���ظ���");
				footerView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new LoadNextPage().execute();
					}
				});
			}else if(ret.size() < messagesPerPage) {
				tvNextPage.setText("û�и������Ϣ��");
				footerView.setOnClickListener(null);
			}
			
			layoutEmpty.setVisibility(View.GONE);
			list.addAll(ret);
			showData();
		}
	}
	
	private class LoadNextPage extends AsyncTask<Integer, Integer, ArrayList<Message>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			tvNextPage.setText("������,���Ժ�...");
		}

		@Override
		protected ArrayList<Message> doInBackground(Integer... params) {
			page++;
			try {
				return AHUTAccessor.getInstance(MessageActivity.this).getMessageList(page);
			} catch (Exception e) {
				alert(e.getMessage());
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Message> ret) {
			if(ret == null) {
				tvNextPage.setText("���ظ���");
				return;
			}

			if(ret.size() == 0) {
				page--;
				tvNextPage.setText("û�и������Ϣ��");
				footerView.setOnClickListener(null);
				return;
			}

			if(ret.size() == messagesPerPage) {
				tvNextPage.setText("���ظ���");
				footerView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new LoadNextPage().execute();
					}
				});
			}else if(ret.size() < messagesPerPage) {
				tvNextPage.setText("û�и������Ϣ��");
				footerView.setOnClickListener(null);
			}
			
			list.addAll(ret);
			lvMessageAdapter.notifyDataSetChanged();
		}
	}

	private class DeleteMessage extends AsyncTask<Integer, Integer, String> {
		
		@Override
		protected String doInBackground(Integer... mid) {
			try {
				AHUTAccessor.getInstance(MessageActivity.this).deleteMessage(mid[0]);
			} catch (Exception e) {
				alert(e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String ret) {
			makeToast("������ɣ�");
			new LoadData().execute();
		}
	}
}
