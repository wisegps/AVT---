package com.wise.avtunicom;

import org.json.JSONObject;
import com.BaseClass.Config;
import com.BaseClass.GetSystem;
import com.BaseClass.NetThread;
import com.wise.avtunicom.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity{	
	private final String TAG = "LoginActivity";
	private final int Login = 1; 		
	
	EditText et_name,et_pwd;
	CheckBox cb_isSavePwd;
	
	/*全局变量*/
	ProgressDialog Dialog = null;    //progress
	String strGroupCode = null;      //用户组
	//int userid;                      //用户编号,发送指令需要
	String LoginName ;               //用户名
	String LoginPws ;                //密码          
	int timeout = 30000;             //超时设置
	boolean LoginNote;               //是否保存密码
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		init();
		getDM();
		getSp();
		try {
		    Class.forName("com.google.android.maps.MapActivity");
		} catch (Exception e) {
			e.printStackTrace();
			ExitDialog();
		    return;
		}
	}
	/**
	 * 提示没有谷歌服务
	 */
	private void ExitDialog(){
		AlertDialog.Builder bulder = new AlertDialog.Builder(LoginActivity.this);
		bulder.setTitle(R.string.Note);// 设置标题
		bulder.setMessage(R.string.Google);
		bulder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		bulder.setNegativeButton(android.R.string.cancel, null);
		bulder.show();
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Login:
				LoginData(msg);
				break;
			}
		}
	};
	
	OnClickListener OCL = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_login:
				//登陆事件
				LoginName = et_name.getText().toString();
				LoginPws = et_pwd.getText().toString();
				if (LoginName.equals("")|| LoginPws.equals("")) {
					Toast.makeText(LoginActivity.this, R.string.Login_null, Toast.LENGTH_LONG).show();
				}else{
					String url = Config.url + "login?username=" + LoginName + "&password=" + GetSystem.getM5DEndo(LoginPws) +"&mac=" + GetSystem.getMacAddress(LoginActivity.this);
					Dialog = ProgressDialog.show(LoginActivity.this,getString(R.string.AllCarInfoActivity_login_pd_title),
							getString(R.string.AllCarInfoActivity_login_pd_context),true);
					new Thread(new NetThread.GetDataThread(handler, url, Login)).start();
				}
				break;
			}
		}
	};
	
	private void LoginData(Message msg){
    	if(Dialog != null){
			Dialog.dismiss();
		}
		try {
			Log.d(TAG, msg.obj.toString());
			JSONObject jsonObject = new JSONObject(msg.obj.toString());
			if(jsonObject.opt("auth_code") == null){
				String status_code = jsonObject.getString("status_code");
				if(status_code.equals("5")){
					Toast.makeText(getApplicationContext(), "该账号已绑定其他手机", Toast.LENGTH_LONG).show();
				}else if(status_code.equals("2") || status_code.equals("1")){
					Toast.makeText(getApplicationContext(), R.string.AllCarInfoActivity_login_id_wrong, Toast.LENGTH_LONG).show();
				}else{
					//setNetworkMethod();
				}
			}else{
				String auth_code = jsonObject.getString("auth_code");
				String cust_id = jsonObject.getString("cust_id");
				String tree_path = jsonObject.getString("tree_path");
				String number_type = jsonObject.getString("number_type");
				String Url = "http://"+jsonObject.getString("host")+":"+jsonObject.getString("port")+"/";
				//更新配置信息
				SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("LoginName", LoginName);
				editor.putString("LoginPws", LoginPws);
				editor.putBoolean("LoginNote", LoginNote);					
				editor.commit();
				//System.out.println("="+account);
				Config.account = LoginName;
				Config.pwd = LoginPws;
				Intent intent = new Intent(LoginActivity.this, AVTActivity.class);
				intent.putExtra("auth_code", auth_code);
				intent.putExtra("cust_id", cust_id);
				intent.putExtra("number_type", number_type);
				intent.putExtra("Url", Url);
				intent.putExtra("tree_path", tree_path);
				startActivity(intent);
				finish();
			}				
		}catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * 初始化数据
	 */
	private void getSp(){ 
		//读取sharedPreferences配置信息
		SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
		String LoginName = preferences.getString("LoginName", "");
		String LoginPws = preferences.getString("LoginPws", "");
		LoginNote = preferences.getBoolean("LoginNote", true);
		et_name.setText(LoginName);
		et_pwd.setText(LoginPws);
		cb_isSavePwd.setChecked(LoginNote);
	}
	/**
	 * 初始化控件
	 */
	private void init() {
		et_name = (EditText) findViewById(R.id.et_account);
		et_pwd = (EditText) findViewById(R.id.et_password);
		cb_isSavePwd = (CheckBox) findViewById(R.id.NotePsw);
		cb_isSavePwd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox)v;
				LoginNote = cb.isChecked();
			}
		});
		Button bt_login = (Button) findViewById(R.id.bt_login);
		bt_login.setOnClickListener(OCL);
	}	
		
	/**
	 * 根据分辨率，设置字体大小
	 */
	private void getDM(){
		 DisplayMetrics dm = new DisplayMetrics();  
	     getWindowManager().getDefaultDisplay().getMetrics(dm);
	     int with = dm.widthPixels;
	     SharedPreferences preferences = getSharedPreferences("wise", Context.MODE_PRIVATE);
		 Editor editor = preferences.edit();
		 if(with >= 480){
			 editor.putInt("Text_size", 20);
			 editor.commit();
			 return;
	     }else if(with >240){
	    	 editor.putInt("Text_size", 15);
			 editor.commit();
	    	 return;
	     }else{
	    	 editor.putInt("Text_size", 8);
			 editor.commit();
	    	 return;
	     }
	}
}