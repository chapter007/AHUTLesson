package com.ahutlesson.android;

import com.ahutlesson.android.model.UserManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends BaseActivity {

	private ProgressDialog progressDialog;
	private Handler handler = new Handler();
	private String uxh, password, confirmPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		disableHomeButton();
		setContentView(R.layout.register);
		
		Button btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText etRegisterXH = (EditText) findViewById(R.id.etRegisterXH);
				EditText etRegisterPassword = (EditText) findViewById(R.id.etRegisterPassword);
				EditText etRegisterConfirmPassword = (EditText) findViewById(R.id.etRegisterConfirmPassword);
				uxh = etRegisterXH.getText().toString();
				password = etRegisterPassword.getText().toString();
				confirmPassword = etRegisterConfirmPassword.getText().toString();
				if(uxh.length() == 0 || password.length() == 0) {
					alert("�û��������벻��Ϊ��!");
				}else if(!confirmPassword.contentEquals(password)) {
					alert("������������벻һ��!");
				}else{
					doRegister();
				}
					
			}
		});
		
		Button btnToLogin = (Button) findViewById(R.id.btnToLogin);
		btnToLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(i);
				RegisterActivity.this.finish();
			}
		});
	}

	private void doRegister() {
		progressDialog = ProgressDialog.show(RegisterActivity.this, "���Ե�...", "�ύ��...", true);
		new Thread() {
			public void run() {
				try {
	    			UserManager.getInstance(RegisterActivity.this).registerUser(uxh,	password);
	            	handler.post(new Runnable() {
	            		public void run() {
	        				progressDialog.setMessage("�������ؿα�...");
	            		}
	            	});
					UserManager.getInstance(RegisterActivity.this).updateLessonDB();
	            	handler.post(new Runnable() {
	            		public void run() {
	            			progressDialog.dismiss();
	            		}
	            	});
					Intent i = new Intent(RegisterActivity.this, MainActivity.class);
					startActivity(i);
					RegisterActivity.this.finish();
	            }catch(final Exception e) {
	            	handler.post(new Runnable() {
	            		public void run() {
	            			progressDialog.dismiss();
	            			alert(e.getMessage());
	            		}
	            	});
	            }
			}
		}.start();
	}
}
