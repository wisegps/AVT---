package com.wise.avtunicom;

import com.BaseClass.Config;
import com.wise.avtunicom.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ConfigActivity extends Activity{
	
	EditText ET_time;
	RadioGroup radioGroup02;
	RadioButton radioButton03,radioButton04;
	private boolean isRef = true;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config);
		
		Button BT_ok = (Button)findViewById(R.id.BT_config_update_ok);
		BT_ok.setOnClickListener(new BTOnClickListener());
		Button BT_cancle = (Button)findViewById(R.id.BT_config_update_cancle);
		BT_cancle.setOnClickListener(new BTOnClickListener());
		
		radioGroup02 = (RadioGroup) findViewById(R.id.RadioGroup02);
		radioButton03 = (RadioButton) findViewById(R.id.RadioButton03);
		radioButton04 = (RadioButton) findViewById(R.id.RadioButton04);
		radioGroup02.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == radioButton03.getId()) {
					isRef = true;
				}else if (checkedId == radioButton04.getId()) {
					isRef = false;
				}
			}
		});
		
		ET_time = (EditText)findViewById(R.id.ET_config_update_time);
		getSharedPrefernece();
	}
	class BTOnClickListener implements OnClickListener{
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.BT_config_update_ok:
				SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putInt("ShortTime", Integer.valueOf(ET_time.getText().toString()));
				editor.putBoolean("isRef", isRef);
				editor.commit();
				Toast.makeText(ConfigActivity.this, R.string.config_update_in_xml_ok, Toast.LENGTH_LONG).show();
				ConfigActivity.this.finish();
				break;
			case R.id.BT_config_update_cancle:
				ConfigActivity.this.finish();
				break;
			}
		}}
	/**
	 * ∂¡»°≈‰÷√–≈œ¢
	 */
	private void getSharedPrefernece(){
		SharedPreferences preferences = getSharedPreferences("wise", Context.MODE_PRIVATE);
		int ShortTime = preferences.getInt("ShortTime", 30);
		boolean isRef = preferences.getBoolean("isRef", true);
		ET_time.setText(String.valueOf(ShortTime));
		if(isRef){
			radioButton03.setChecked(true);
		}else{
			radioButton04.setChecked(true);
		}
	}
}