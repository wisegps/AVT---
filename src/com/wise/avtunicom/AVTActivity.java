package com.wise.avtunicom;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.BaseClass.AllStaticClass;
import com.BaseClass.Config;
import com.BaseClass.GetSystem;
import com.BaseClass.NetThread;
import com.BaseClass.ResolveData;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.wise.avtunicom.R;
import com.wise.extend.CarAdapter;
import com.wise.extend.CarLocationOverlay;
import com.wise.extend.MeOverlay;
import com.wise.extend.MoveLocusOverlay;
import com.wise.extend.LineOverlay;
import com.wise.extend.PoiOverlay;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.util.ArrayAdapter;
import com.wise.util.CarInfo;
import com.wise.util.ContacterData;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class AVTActivity extends MapActivity implements OnGestureListener,IXListViewListener{	
	private final String TAG = "AVTActivity";
	
	private final int GetContacter = 1;  //获取用户组信息
	private final int GetContacterCar = 2; //获取用户组下的车辆信息
	private final int GetRefreshData = 3; //获取刷新数据
	private final int UPDATEMAIN = 4; //定时刷新车辆数据
	private final int LocausOver  = 5; //轨迹回放完毕
	private final int LocausNow  = 6; //每隔一段时间画一次图像
	private final int UpdatePwd  = 7; //修改用户密码
	private final int GetPoi  = 8; //解析poi
	private final int GetTotal  = 9; //获取统计数据
	private final int GetLocation  = 10; //获取地址
	private final int GetFristLocus = 11; //获取前100条记录
	private final int GetNextLocus = 12; //当前一段播放完毕，加载另一段
	//控件
	ViewFlipper flipper;
	GestureDetector gestureScanner;
	Spinner s_contacter;
	XListView lv_cars;
	AutoCompleteTextView ListAutoComplete,MapAutoComplete;
	ImageView iv_ListClear,iv_MapClear,iv_Map,iv_Me,iv_play,iv_pause,iv_stop;
	LinearLayout layout_bar; //播放控制
	ProgressBar bar;//轨迹回访控制条
	EditText et_start,et_stop;
	SeekBar sb_speed;
	ProgressDialog Dialog;
	TextView tv_map_change,tv_address,tv_SearchStatistic;
	Button bt_ZoomDown,bt_ZoomUp;
	
	//全局变量
	List<ContacterData> contacterDatas = new ArrayList<ContacterData>();//用户集合
	List<CarInfo> carinfos; // 所有车辆数据集合list
	List<CarInfo> carPath; // 轨迹回放list
	List<Overlay> pathOverlays = new ArrayList<Overlay>(); //轨迹图层
	List<Overlay> carOverlays = new ArrayList<Overlay>(); //车辆图层
	
	List<String> carNums;          //绑定到自动感应控件
	CarAdapter carAdapter;

	String Url;
	String tree_path;
	String number_type = "";
	String auth_code;
	String cust_id;
	Integer userid = null; // 用户ID
	String LoginName; // 账号
	int ShortTime; // 定时刷新时间
	int Text_size; // 字体大小

	int item; //车所在list的位置
	int PROGRESS = 0; // 进度条开始位置
	GeoPoint LastPoint;  //上一个点实时监控用到
	GeoPoint Point; // 默认定位的点
	GeoPoint p;     //当前位置
	
	MapView mMapView;
	MapController mMapController;
	private List<Overlay> mapOverLays; // 图层列表
	View popView;     // 气泡窗口
	String startTime;
	String stopTime;
	private static final int PageNumber = 20; //每次加载轨迹的数目
	int contacter_item = 0; //选中spinner第几项
	int page_total; //当前页的记录数，用来判断轨迹是否加载完毕
	int page = 1;   //当前页数，用来加载轨迹
	boolean isSpinnerChange = true; //spinner隐藏不处罚select事件
	/**
	 * 每次加载车辆数目
	 */
	final int Car_Page_Number = 20;
	/**
	 * 上一次获取的车辆数据
	 */
	int Car_Page_total = 0;
	int Car_Page = 1; //当前页数
	String latestTime = ""; //更新车辆位置
	/**
	 * 轨迹逐步回放的间隔时间
	 */
	int SENDTIME = 1000;
	/**
	 * 第一次加载页面，需要启动定时刷新，
	 */
	boolean IsFristOnCreate = true;
	/**
	 * 是否监控，ture实时监控，并显示路线图。false取消实时监控
	 */
	private boolean ISSEARCH = false;
	private boolean IsFristLocus = true;
	/**
	 * false 地图 ，true 卫星
	 */
	boolean IsSatellite = false;
	/**
	 * 是否在轨迹回访中（跟踪），是，停止刷新数据，否刷新数据
	 */
	boolean IsLock = false;
	/**
	 * 刷新所有数据，退出时关闭
	 */
	boolean IsUpdateMain = true;
	/**
	 * 控制轨迹回访线程
	 */
	boolean ISSTARTBAR = false;
	/**
	 * 程序是否在后台
	 */
	boolean isPause = false;	
	//定位管理
	LocationManager locationManager;
	private LocationListener gpsListener=null;
	private LocationListener networkListner=null;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
	}    
    private OnClickListener OCL = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_ZoomDown:
				mMapController.zoomOut();
				break;
			case R.id.bt_ZoomUp:
				mMapController.zoomIn();
				break;			
			case R.id.iv_Map://跳转到地图页
				flipper.setDisplayedChild(1);
				break;			
			case R.id.iv_Search://跳转到列表页
				flipper.setDisplayedChild(0);
				break;
			case R.id.tv_map_change://卫星和地图切换			
				//if(!IsLock){//如果在轨迹或回访不能对图标处理
					//ChangeMap();
				//}				
				if(IsSatellite){//地图
					IsSatellite = false;
					mMapView.setSatellite(false);
					tv_map_change.setText(R.string.Satellite);
				}else{//卫星
					IsSatellite = true;
					mMapView.setSatellite(IsSatellite);
					tv_map_change.setText(R.string.Traffic);
				}
				break;
			case R.id.iv_Me://定位到当前位置
				try {
					mMapController.animateTo(p);
				} catch (Exception e) {
					//没有定位信息
					Toast.makeText(AVTActivity.this, R.string.Location_wrong, Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.iv_ListClear://清除列表
				ListAutoComplete.setText("");
				break;
			case R.id.iv_MapClear://清除地图
				MapAutoComplete.setText("");
				break;
			case R.id.bt_menu_car://跟踪功能
				if (ISSEARCH) {//取消跟踪
					ISSEARCH = false;
					System.gc();
					ClearOverlay(pathOverlays);//删除轨迹线
				} else {//开始跟踪
					ISSEARCH = true;
					ClearOverlay(carOverlays);//删除汽车标记
					ShowAllCar();
				}
				break;
			case R.id.bt_monitor_locus://轨迹回放
				popView.setVisibility(View.GONE);
				ISSEARCH = false;//关闭跟踪
				LocausDialog();
				break;			
			case R.id.iv_play://开始播放轨迹
				iv_play.setEnabled(false);//播放按钮设为不可用
				ISSTARTBAR = true;
				new Thread(new startBarThread()).start();
				break;			
			case R.id.iv_pause://暂停和继续播放
				if(PROGRESS > 0){
					//停止播放线程
					ISSTARTBAR = false;
				}
				break;			
			case R.id.iv_stop://退出轨迹回放				
				ISSTARTBAR = false;//停止回访线程
				//数据回复到开始状态
				PROGRESS = 0;
				bar.setProgress(0);
				LocusNow(PROGRESS);
				break;
			}
		}
	};
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GetContacter://获取组信息
				JsonContacter(msg.obj.toString());
				break;
			case GetContacterCar://获取组下的车辆
				if(Dialog != null){
					Dialog.dismiss();
				}
				JsonContacterCar(msg.obj.toString());
				if(Car_Page_total == Car_Page_Number){
					lv_cars.setPullLoadEnable(true);
				}else{
					lv_cars.setPullLoadEnable(false);
				}
				if(isSpinnerChange){
					isSpinnerChange = false;
					carAdapter = new CarAdapter(AVTActivity.this, carinfos);
					lv_cars.setAdapter(carAdapter);
					if(carinfos.size() > 0){
						//查找位置
						new Thread(new NetThread.GetLocation(carinfos.get(item).getLat(), carinfos.get(item).getLon(), handler,GetLocation,AVTActivity.this)).start();
						Point = new GeoPoint(AllStaticClass.StringToInt(carinfos.get(item).getLat()), AllStaticClass.StringToInt(carinfos.get(item).getLon()));
						mMapController.animateTo(Point);
					}					
				}else{
					carAdapter.notifyDataSetChanged();
					onLoad();
				}
				if(IsFristOnCreate){
					IsFristOnCreate = false;
					//开启刷新线程
					new Thread(new UpdateMain()).start();
				}
				break;			
			case UPDATEMAIN://定时刷新所有车辆信息
				try {
					String RefreshUrl = "http://42.121.53.152/customer/" + cust_id + "/active_gps_data?auth_code=" + auth_code + "&update_time=" + 
							URLEncoder.encode(latestTime, "utf-8") + "&mode=all&tree_path=" + contacterDatas.get(contacter_item).getTree_path();
					new Thread(new NetThread.GetDataThread(handler, RefreshUrl, GetRefreshData)).start();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				break;
			case GetRefreshData: //解析更新数据并绑定
				jsonRefreshData(msg.obj.toString().trim());
				if(!IsLock){//如果在轨迹回访不刷新数据
					if (popView != null) {
						popView.setVisibility(View.GONE);
					}
					LastPoint = Point;//跟踪用到
					System.out.println("刷新所有数据");
					carAdapter.notifyDataSetChanged();//刷新列表数据
					//当前车位置
					if(carinfos.size() > 0){
						Point = new GeoPoint(AllStaticClass.StringToInt(carinfos.get(item).getLat()), AllStaticClass.StringToInt(carinfos.get(item).getLon()));
						if(ISSEARCH){
							mapOverLays.add(new LineOverlay(LastPoint, Point));//处于跟踪状态
							mMapController.animateTo(Point);
						}else{
							mMapController.animateTo(mMapView.getMapCenter());
						}
						ShowAllCar();
					}					
				}
				break;
			case LocausOver:
				//回访完毕后的动作
				iv_play.setEnabled(true);
				iv_pause.setEnabled(true);
				PROGRESS = 0;
				break;
			//轨迹回访步骤
			case LocausNow:
				LocusNow(msg.arg1);
				break;
			//验证密码返回
			case UpdatePwd:
				if(Dialog != null){
					Dialog.dismiss();
				}
				String resultPwd = msg.obj.toString();
				if(resultPwd.indexOf("0")> 0){
					Toast.makeText(AVTActivity.this, R.string.change_pwd_true,Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(AVTActivity.this, R.string.change_pwd_false,Toast.LENGTH_SHORT).show();
				}
				break;
			case GetPoi://显示热点
				jsonPoiData(msg.obj.toString().trim());
				break;
			case GetTotal:
				//得到统计信息
//				try{
//					TotalData totalData = ResolveData.ResolveTotal(theResult);
//					String statistic = getString(R.string.total)+": "+totalData.getToatal()+"          "
//					          +getString(R.string.accon)+": "+totalData.getAccOn()+"          "
//					          +getString(R.string.accoff)+": "+totalData.getAccOff();
//					tv_statistic.setText(statistic);
//					tv_SearchStatistic.setText(statistic);
//				}catch (Exception e) {
//					e.printStackTrace();
//					System.out.println("统计信息异常");
//				}
				break;
			case GetLocation:
				String LocationString = (String) msg.obj;
				tv_address.setText(LocationString);
				break;
			case GetFristLocus:
				jsonLocusData(msg.obj.toString());
				if(Dialog != null){
					Dialog.dismiss();
				}
				break;
			case GetNextLocus:
				System.out.println("开始加载");
				page++;
				String url = Url + "vehicle/"+ carinfos.get(item).getObjectID() +"/gps_data2?auth_code=" + auth_code + "&start_time=" + URLEncoder.encode(startTime) + "&end_time=" + URLEncoder.encode(stopTime) + "&page_no="+ page +"&page_count="+PageNumber;
				new Thread(new NetThread.GetDataThread(handler, url, GetFristLocus)).start();
				break;
			}
		}
	};
	/**
	 * 解析用户组信息
	 * @param str
	 */
	private void JsonContacter(String str){
		List<String> listContacter = new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				ContacterData contacterData = new ContacterData();
				contacterData.setContacter(json.getString("contacter"));
				contacterData.setTree_path(json.getString("tree_path"));
				contacterDatas.add(contacterData);
				listContacter.add(json.getString("contacter"));
			}
			//绑定spinner
			android.widget.ArrayAdapter<String> Adapter = new android.widget.ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listContacter);
			Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 设置下拉列表的风格
			s_contacter.setAdapter(Adapter);
			s_contacter.setOnItemSelectedListener(onItemSelectedListener);
			//获取用户下车辆数据
			carinfos = new ArrayList<CarInfo>();
			carNums = new ArrayList<String>();
			String url = Url + "customer/" + cust_id + "/vehicle?auth_code=" + auth_code + "&tree_path=" + contacterDatas.get(contacter_item).getTree_path() + "&mode=all&business_type=2&page_no=" + Car_Page + "&page_count=" + Car_Page_Number;
			new Thread(new NetThread.GetDataThread(handler, url, GetContacterCar)).start();
		} catch (JSONException e) {
			e.printStackTrace();
			if(Dialog != null){
				Dialog.dismiss();
			}
		}
	}
	boolean isSpinnerShow = false;
	//spinner 隐藏的情况下不会触发onItemSelectedListener
	OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			if(isSpinnerShow == true){
				item = 0;
				carinfos = new ArrayList<CarInfo>();
				carNums = new ArrayList<String>();
				isSpinnerChange = true;
				contacter_item = arg2;
				Car_Page = 1;
				String url = Url + "customer/" + cust_id + "/vehicle?auth_code=" + auth_code + "&tree_path=" + contacterDatas.get(contacter_item).getTree_path() + "&mode=all&business_type=2&page_no=" + Car_Page + "&page_count=" + Car_Page_Number;
				new Thread(new NetThread.GetDataThread(handler, url, GetContacterCar)).start();
			}
			isSpinnerShow = true;
		}
		public void onNothingSelected(AdapterView<?> arg0) {}
	};
	/**
	 * 解析车辆数据
	 * @param str
	 */
	private void JsonContacterCar(String str){
		try {
			JSONObject jsonObject = new JSONObject(str);
			Car_Page_total = Integer.valueOf(jsonObject.getString("page_total"));
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				CarInfo carInfo = new CarInfo();			
				JSONObject json = jsonArray.getJSONObject(i);
				carInfo.setObj_name(json.getString("obj_name"));
				carInfo.setObjectID(json.getString("obj_id"));
				if(json.optJSONObject("active_gps_data") == null){
					carInfo.setLat("0");
					carInfo.setLon("0");
					carInfo.setDirect("0");
					carInfo.setSpeed(0);
					carInfo.setMileage("0");
					carInfo.setRcv_time("");
					carInfo.setMDTStatus("");
		    		carInfo.setCarStatus(1);
				}else{
					JSONObject jsonData = json.getJSONObject("active_gps_data");
					String rcv_time = GetSystem.ChangeTime(jsonData.getString("rcv_time"), 0);
					int gps_flag = Integer.valueOf(jsonData.getString("gps_flag"));
					int speed = (int)Double.parseDouble(jsonData.getString("speed"));
					carInfo.setLat(jsonData.getString("lat"));
					carInfo.setLon(jsonData.getString("lon"));
					carInfo.setDirect(jsonData.getString("direct"));
					carInfo.setSpeed(speed);
					carInfo.setRcv_time(rcv_time);
					carInfo.setMileage(jsonData.getString("mileage"));
					JSONArray jsonArrayStatus = jsonData.getJSONArray("uni_status");
		    		JSONArray jsonArrayAlerts = jsonData.getJSONArray("uni_alerts");
		    		String status = ResolveData.getStatusDesc(rcv_time, gps_flag, speed, ResolveData.getUniStatusDesc(jsonArrayStatus), ResolveData.getUniAlertsDesc(jsonArrayAlerts));
		    		carInfo.setMDTStatus(status);
		    		carInfo.setCarStatus(ResolveData.getCarStatus(rcv_time, jsonArrayAlerts, speed));
		    		latestTime = GetSystem.LatestTime(latestTime, rcv_time);
				}
				carNums.add(json.getString("obj_name"));
	    		carinfos.add(carInfo);
			}
			bindData();
    		ShowAllCar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 解析更新数据
	 * @param str
	 */
	private void jsonRefreshData(String str){
		try {
			JSONArray jsonArray = new JSONArray(str);
			for (int i = 0; i < jsonArray.length(); i++) {	
				JSONObject json = jsonArray.getJSONObject(i);
				for (int j = 0; j < carinfos.size(); j++) {
					CarInfo carInfo = carinfos.get(j);
					if(json.getString("obj_id").equals(carInfo.getObjectID())){
						if(json.optJSONObject("active_gps_data") == null){
							
						}else{
							JSONObject jsonData = json.getJSONObject("active_gps_data");
							String rcv_time = GetSystem.ChangeTime(jsonData.getString("rcv_time"), 0);
							int gps_flag = Integer.valueOf(jsonData.getString("gps_flag"));
							int speed = (int)Double.parseDouble(jsonData.getString("speed"));
							carInfo.setLat(jsonData.getString("lat"));
							carInfo.setLon(jsonData.getString("lon"));
							carInfo.setDirect(jsonData.getString("direct"));
							carInfo.setSpeed(speed);
							carInfo.setRcv_time(rcv_time);
							carInfo.setMileage(jsonData.getString("mileage"));
							JSONArray jsonArrayStatus = jsonData.getJSONArray("uni_status");
				    		JSONArray jsonArrayAlerts = jsonData.getJSONArray("uni_alerts");
				    		String status = ResolveData.getStatusDesc(rcv_time, gps_flag, speed, ResolveData.getUniStatusDesc(jsonArrayStatus), ResolveData.getUniAlertsDesc(jsonArrayAlerts));
				    		carInfo.setMDTStatus(status);
				    		latestTime = GetSystem.LatestTime(latestTime, rcv_time);
						}
						break;
					}
				}				
			}
    		carAdapter.notifyDataSetChanged();
    		bindData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 显示poi热点
	 * @param str
	 */
	private void jsonPoiData(String str){
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {		
				JSONObject json = jsonArray.getJSONObject(i);
				GeoPoint point = new GeoPoint(AllStaticClass.StringToInt(json.getString("lat")), AllStaticClass.StringToInt(json.getString("lon")));
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.other);
				PoiOverlay poiOverlay = new PoiOverlay(point, bitmap);
				mapOverLays.add(poiOverlay);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void jsonLocusData(String str){
		try {
			JSONObject jsonObject = new JSONObject(str);
			page_total = Integer.valueOf(jsonObject.getString("page_total"));
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for(int i = 0 ; i < jsonArray.length() ; i++){
				JSONObject jsonData= jsonArray.getJSONObject(i);
				CarInfo carInfo = new CarInfo();
				carInfo.setLat(jsonData.getString("rev_lat"));
				carInfo.setLon(jsonData.getString("rev_lon"));
				carInfo.setDirect(jsonData.getString("direct"));
				carInfo.setMileage(jsonData.getString("mileage"));
				carInfo.setSpeed((int)Double.parseDouble(jsonData.getString("speed")));
				carInfo.setRcv_time(GetSystem.ChangeTime(jsonData.getString("rcv_time"),0));
				//String status = ResolveData.getStatusDesc(rcv_time, gps_flag, speed, ResolveData.getUniStatusDesc(jsonArrayStatus), ResolveData.getUniAlertsDesc(jsonArrayAlerts));
	    		//TODO carInfo.setMDTStatus(status);
				carPath.add(carInfo);
				Log.d(TAG, carInfo.toString());
			}				
			if(IsFristLocus){
				if(jsonArray.length() == 0){
					Toast.makeText(AVTActivity.this, R.string.monitor_locus_null,Toast.LENGTH_LONG).show();
				}else{
					IsLock = true;
					Log.d(TAG, "显示数据");
					IsFristLocus = false;
					layout_bar.setVisibility(View.VISIBLE);//显示轨迹控制条
					bar.setMax(Integer.valueOf(jsonObject.getString("total")));	
					ClearOverlay(carOverlays);//TODO 删除车辆标记
					ClearOverlay(pathOverlays);//删除轨迹
				}
			}else{
				if(jsonArray.length() != 0){
					//locus();
					ISSTARTBAR = true;
					new Thread(new startBarThread()).start();
				}						
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	OnSeekBarChangeListener OSBCL = new OnSeekBarChangeListener() {		
		public void onStopTrackingTouch(SeekBar seekBar) {}
		public void onStartTrackingTouch(SeekBar seekBar) {}
		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
			SENDTIME = 1500 - seekBar.getProgress()*10;
		}
	};	
	/**
	 * 把运行轨迹画到地图上
	 */
	private void locus1() {	
		IsLock = true;
		ClearOverlay(carOverlays);//TODO 删除车辆标记
		ClearOverlay(pathOverlays);//删除轨迹
		for (int i = 0; i < carPath.size() - 1; i++) {//循环画出轨迹线
			GeoPoint startPoint = new GeoPoint(AllStaticClass.StringToInt(carPath.get(i).getLat()), AllStaticClass.StringToInt(carPath.get(i).getLon()));
			GeoPoint stopPoint = new GeoPoint(AllStaticClass.StringToInt(carPath.get(i + 1).getLat()),AllStaticClass.StringToInt(carPath.get(i + 1).getLon()));
			LineOverlay myOverlay = new LineOverlay(startPoint, stopPoint);
			mapOverLays.add(myOverlay);
			pathOverlays.add(myOverlay);
		}
		mMapController.animateTo(new GeoPoint(AllStaticClass.StringToInt(carPath.get(PROGRESS).getLat()), AllStaticClass.StringToInt(carPath.get(PROGRESS).getLon()))); 
	}
	MoveLocusOverlay moveLocusOverlay;
	/**
	 * 轨迹图标移动
	 * @param index
	 */
	private void LocusNow(int index){
		GeoPoint stopPoint = new GeoPoint(AllStaticClass.StringToInt(carPath.get(index).getLat()),AllStaticClass.StringToInt(carPath.get(index).getLon()));
		//TODO 划线
		if(index != 0){
			GeoPoint startPoint = new GeoPoint(AllStaticClass.StringToInt(carPath.get(index - 1).getLat()), AllStaticClass.StringToInt(carPath.get(index - 1).getLon()));
			LineOverlay myOverlay = new LineOverlay(startPoint, stopPoint);
			mapOverLays.add(myOverlay);
			pathOverlays.add(myOverlay);
		}		
		//移动图标
		Log.d(TAG, "index="+index);
		if(moveLocusOverlay != null){
			mapOverLays.remove(moveLocusOverlay);
		}
		//GeoPoint stopPoint = new GeoPoint(AllStaticClass.StringToInt(carPath.get(index).getLat()), AllStaticClass.StringToInt(carPath.get(index).getLon()));
		int CarStatus = carPath.get(index).getCarStatus();
		String Direct = carPath.get(index).getDirect();
		Drawable drawable = AllStaticClass.DrawableBimpMap(AVTActivity.this, CarStatus,Direct);
		moveLocusOverlay = new MoveLocusOverlay(stopPoint, GetSystem.drawableToBitmap(drawable));
		mapOverLays.add(moveLocusOverlay); // 图层添加到map显示
		mMapController.animateTo(stopPoint); // 定位
		bar.setProgress(index + 1);
		ShowLocalPop(index);
	}
	private void ShowLocalPop(int i){
		MapView.LayoutParams geoLP = (MapView.LayoutParams) popView.getLayoutParams();
		geoLP.point = new GeoPoint(AllStaticClass.StringToInt(carPath.get(i).getLat()), AllStaticClass.StringToInt(carPath.get(i).getLon()));;
		TextView tv_car_id = (TextView) popView.findViewById(R.id.pop_car_id);
		tv_car_id.setVisibility(View.GONE);
		TextView my_line = (TextView) popView.findViewById(R.id.my_line);
		my_line.setVisibility(View.GONE);
		TextView tv_car_MSTStatus = (TextView) popView.findViewById(R.id.pop_car_MSTStatus);
		TextView tv_car_Mileage = (TextView) popView.findViewById(R.id.pop_car_Mileage);
		TextView tv_car_Speed = (TextView) popView.findViewById(R.id.pop_car_Speed);
		TextView tv_car_GpsTime = (TextView) popView.findViewById(R.id.pop_car_GpsTime);		
		TextView bt_menu_car = (TextView)popView.findViewById(R.id.bt_menu_car);
		bt_menu_car.setVisibility(View.GONE);
		bt_menu_car.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_menu_car.getPaint().setAntiAlias(true);
		bt_menu_car.setOnClickListener(OCL);		
		TextView bt_monitor_locus = (TextView)popView.findViewById(R.id.bt_monitor_locus);
		bt_monitor_locus.setVisibility(View.GONE);
		bt_monitor_locus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_monitor_locus.getPaint().setAntiAlias(true);
		bt_monitor_locus.setOnClickListener(OCL);		
		int Speed = carPath.get(i).getSpeed();
		String mileage = carPath.get(i).getMileage();
		String Rcv_time = carPath.get(i).getRcv_time();		
		tv_car_MSTStatus.setText(carinfos.get(item).getObj_name());
		tv_car_Mileage.setText(getString(R.string.car_mileage) + mileage + " km");
		tv_car_Speed.setText(getString(R.string.car_speed) + Speed + " km/h");
		tv_car_GpsTime.setText(Rcv_time);
		mMapView.updateViewLayout(popView, geoLP);
		popView.setVisibility(View.VISIBLE);
	}
	MeOverlay meOverlay;
	/**
	 * 当前位置
	 */
	private void ShowMeLocation(GeoPoint gp){
		try{
			if(meOverlay != null){
				mapOverLays.remove(meOverlay);
			}
			Drawable drawable = getResources().getDrawable(R.drawable.icon_locr);
			meOverlay = new MeOverlay(gp, GetSystem.drawableToBitmap(drawable), Accuracy);
	        mapOverLays.add(meOverlay);
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("个人定位异常");
		}
	}
	/**
	 * 定时刷新主线程
	 * @author honesty
	 *
	 */
	class UpdateMain implements Runnable {
		public void run() {
			while (IsUpdateMain) {
				int updateTime;
				//判断配置文件是否自动刷新
				SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
				boolean isRef = preferences.getBoolean("isRef", true);
				if(isRef){
					updateTime = (preferences.getInt("ShortTime", 30)) * 1000;
				}else{
					updateTime = 180000;
				}
				System.out.println("定时刷新时间：" + updateTime);
				try {
					Thread.sleep(updateTime);
					//判断是否程序运行在后台
					if(!isPause){
						Message message = new Message();
						message.what = UPDATEMAIN;
						handler.sendMessage(message);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 显示全部车辆
	 */
	private void ShowAllCar(){
		ClearOverlay(carOverlays);//删除汽车标记
		HideLocaus();
		for(int i = 0 ; i < carinfos.size() ; i++){
			String Lat = carinfos.get(i).getLat();
			String Lon = carinfos.get(i).getLon();
			int Speed = carinfos.get(i).getSpeed();
			String Direct = carinfos.get(i).getDirect();
			String RegNum = carinfos.get(i).getObj_name();
			int CarStatus = carinfos.get(i).getCarStatus();
			String MSTStatus = carinfos.get(i).getMDTStatus();
			String Mileage = carinfos.get(i).getMileage();
			String gps_time = carinfos.get(i).getRcv_time();
			
			String snippet = RegNum + ",," + gps_time + ",," + MSTStatus + ",," + Speed + "km/h" + ",," + Mileage + "km";
			
			GeoPoint Point = new GeoPoint(AllStaticClass.StringToInt(Lat), AllStaticClass.StringToInt(Lon)); // 得到经纬度
			Drawable drawable = AllStaticClass.DrawableBimpMap(AVTActivity.this, CarStatus,Direct);
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();
			drawable.setBounds(-w/2, -h/2, w/2, h/2);
			CarLocationOverlay itemOverLay;
			//判断当前车辆是否跟踪，要用特殊标记
			if(i == item){
				itemOverLay = new CarLocationOverlay(drawable, AVTActivity.this, RegNum, Point, ISSEARCH,IsSatellite,AVTActivity.this,Text_size,i);
			}else{
				itemOverLay = new CarLocationOverlay(drawable, AVTActivity.this, RegNum, Point, false,IsSatellite,AVTActivity.this,Text_size,i);
			}
			OverlayItem overLayItem = new OverlayItem(Point, RegNum, snippet); // 绑定点击事件
			overLayItem.setMarker(drawable);
			itemOverLay.addOverLay(overLayItem);
			mapOverLays.add(itemOverLay); // 图层添加到map显示
			itemOverLay.setOnFocusChangeListener(onFocusChangeListener); // 绑定显示事件
			carOverlays.add(itemOverLay);
		}
		Log.d(TAG, "图层数量："+mapOverLays.size());
	}	
	/**
	 * 绑定自动感应控件
	 */
	private void bindData(){
    	//绑定数据到AutoCompleteTextView
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,carNums); 
    	//列表搜索控件
    	ListAutoComplete.setAdapter(adapter);
    	ListAutoComplete.setThreshold(0);
    	ListAutoComplete.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String Regnum = ListAutoComplete.getText().toString();
				for(int i = 0 ; i < carinfos.size() ; i++){
					if(carinfos.get(i).getObj_name().equals(Regnum)){
						lv_cars.setSelection(i); //定位到对应行
						//选中对应车辆
						ChooseCar(i,1);
						break;
					}
				}
			}
		});
    	//地图搜索控件
    	MapAutoComplete.setAdapter(adapter);
    	MapAutoComplete.setThreshold(0);
    	MapAutoComplete.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String Regnum = MapAutoComplete.getText().toString();
				for(int i = 0 ; i < carinfos.size() ; i++){
					if(carinfos.get(i).getObj_name().equals(Regnum)){
						ChooseCar(i,1);
						lv_cars.setSelection(i);
						break;
					}
				}
			}
		});
    }    
	/**
	 * 切换卫星图
	 */
	private void ChangeMap(){ //TODO 删除
		ClearOverlay(carOverlays);
		ShowAllCar();
	}
	/**
	 * 轨迹回放
	 * @author honesty
	 *
	 */
	class startBarThread implements Runnable {
		public void run() {
			while (ISSTARTBAR) {
				try {
					if (PROGRESS >= carPath.size()) {//阶段回放完成 
						ISSTARTBAR = false; //停止线程
						Message message = new Message();
						if(page_total == PageNumber){
							message.what = GetNextLocus; //加载剩下的数据
						}else{
							message.what = LocausOver; //回放完成
						}
						handler.sendMessage(message);
					} else {
						Thread.sleep(SENDTIME);
						if(ISSTARTBAR){
							Message message = new Message();
							message.what = LocausNow;
							message.arg1 = PROGRESS;
							handler.sendMessage(message);
							PROGRESS++;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}	
	/**
	 * 隐藏轨迹回访
	 */
	private void HideLocaus(){		
		ISSTARTBAR = false;//关闭线程		
		layout_bar.setVisibility(View.GONE);//隐藏
		bar.setProgress(0);
		PROGRESS = 0;
	}	
	private void LocausDialog() {
		View viewtime = LayoutInflater.from(AVTActivity.this).inflate(R.layout.timedialog, null);
		ImageView iv_start = (ImageView) viewtime.findViewById(R.id.iv_start);
		iv_start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timeDialog(1);
			}
		});
		ImageView iv_stop = (ImageView) viewtime.findViewById(R.id.iv_stop);
		iv_stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timeDialog(2);
			}
		});
		et_start = (EditText) viewtime.findViewById(R.id.et_start);
		et_start.setInputType(InputType.TYPE_NULL);
		et_stop = (EditText) viewtime.findViewById(R.id.et_stop);
		et_stop.setInputType(InputType.TYPE_NULL);
		AlertDialog.Builder timeBuilder = new AlertDialog.Builder(AVTActivity.this);
		timeBuilder.setView(viewtime);
		timeBuilder.setTitle(R.string.car_dialog_title);
		timeBuilder.setPositiveButton(R.string.car_dialog_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (et_start.getText().toString().equals("")|| et_stop.getText().toString().equals("")) {
							Toast.makeText(AVTActivity.this,R.string.car_dialog_et_null,Toast.LENGTH_SHORT).show();
						} else if (!AllStaticClass.LimitTime(et_start.getText().toString(),et_stop.getText().toString())) {
							Toast.makeText(AVTActivity.this,R.string.car_dialog_time_limit,Toast.LENGTH_SHORT).show();
						} else {
							Dialog = ProgressDialog.show(AVTActivity.this,getString(R.string.serach_pd_title),getString(R.string.monitor_locus_load),true);
							//查询轨迹前重置数据
							iv_play.setEnabled(true);
							IsFristLocus = true;
							PROGRESS = 0;
							Car_Page = 1;
							carPath = new ArrayList<CarInfo>();
							String url = Url + "vehicle/" + carinfos.get(item).getObjectID() +"/gps_data2?auth_code=" + auth_code + "&start_time=" + URLEncoder.encode(startTime) + "&end_time=" + URLEncoder.encode(stopTime) + "&page_no="+ page +"&page_count="+PageNumber;
							new Thread(new NetThread.GetDataThread(handler, url, GetFristLocus)).start();
						}
					}
				});
		timeBuilder.setNegativeButton(android.R.string.cancel, null);
		timeBuilder.show();
	}
	
	private void timeDialog(final int i) {
		View view = LayoutInflater.from(AVTActivity.this).inflate(R.layout.time, null);
		final DatePicker dp_start = (DatePicker) view.findViewById(R.id.dp_start);
		final TimePicker tp_start = (TimePicker) view.findViewById(R.id.tp_start);
		tp_start.setIs24HourView(true);
		if(i==1){
			tp_start.setCurrentHour(0);
			tp_start.setCurrentMinute(0);
		}else{
			tp_start.setCurrentHour(23);
			tp_start.setCurrentMinute(59);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(AVTActivity.this);
		builder.setView(view);
		builder.setTitle(R.string.time_choose);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 轨迹回放
						if (i == 1) {// 起始时间
							startTime = AllStaticClass.intToString(dp_start.getYear()) + "-"
									+ AllStaticClass.intToString(dp_start.getMonth() + 1)
									+ "-"
									+ AllStaticClass.intToString(dp_start.getDayOfMonth())
									+ " "
									+ AllStaticClass.intToString(tp_start.getCurrentHour())
									+ ":"
									+ AllStaticClass.intToString(tp_start.getCurrentMinute())
									+ ":" + "00";
							et_start.setText(startTime);
						} else {
							stopTime = AllStaticClass.intToString(dp_start.getYear()) + "-"
									+ AllStaticClass.intToString(dp_start.getMonth() + 1)
									+ "-"
									+ AllStaticClass.intToString(dp_start.getDayOfMonth())
									+ " "
									+ AllStaticClass.intToString(tp_start.getCurrentHour())
									+ ":"
									+ AllStaticClass.intToString(tp_start.getCurrentMinute())
									+ ":" + "00";
							et_stop.setText(stopTime);
						}
					}

				});
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}	
	private OnItemClickListener OICL = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			flipper.setDisplayedChild(1);
			ChooseCar((arg2 - 1),0);
		}
	};
	/**
	 * 切换车辆
	 * @param arg
	 * @param where 0，列表 1，地图
	 */
	public void ChooseCar(int arg,int where){
		carAdapter.setSelectItem(arg);
		carAdapter.notifyDataSetInvalidated();
		if(arg != item){
			item = arg;
			//获取位置
			new Thread(new NetThread.GetLocation(carinfos.get(arg).getLat(), carinfos.get(arg).getLon(), handler,GetLocation,AVTActivity.this)).start();		
			//选中车辆
			carAdapter.setSelectItem(arg);
			//carAdapter.notifyDataSetInvalidated();
			if(ISSEARCH){//如果在监控则停止监控
				ISSEARCH = false;			
				ClearOverlay(carOverlays);//删除汽车标记
				ClearOverlay(pathOverlays);
				ShowAllCar();
			}
			if(IsLock){//停止轨迹回访			
				ISSTARTBAR = false;			
				IsLock = false;
				ClearOverlay(pathOverlays);
				//删除轨迹回访跑的图标
				mapOverLays.remove(moveLocusOverlay);
				popView.setVisibility(View.GONE);
				ShowAllCar();
			}
		}	
		//定位到当前坐标
		Point = new GeoPoint(AllStaticClass.StringToInt(carinfos.get(arg).getLat()), AllStaticClass.StringToInt(carinfos.get(arg).getLon()));
		mMapController.animateTo(Point);	
		ShowPop(arg);
	}	
	private void ShowPop(int i){
		MapView.LayoutParams geoLP = (MapView.LayoutParams) popView.getLayoutParams();
		geoLP.point = Point;
		TextView tv_car_id = (TextView) popView.findViewById(R.id.pop_car_id);
		tv_car_id.setVisibility(View.VISIBLE);
		TextView my_line = (TextView) popView.findViewById(R.id.my_line);
		my_line.setVisibility(View.VISIBLE);
		TextView tv_car_MSTStatus = (TextView) popView.findViewById(R.id.pop_car_MSTStatus);
		TextView tv_car_Mileage = (TextView) popView.findViewById(R.id.pop_car_Mileage);
		TextView tv_car_Speed = (TextView) popView.findViewById(R.id.pop_car_Speed);
		TextView tv_car_GpsTime = (TextView) popView.findViewById(R.id.pop_car_GpsTime);		
		TextView bt_menu_car = (TextView)popView.findViewById(R.id.bt_menu_car);
		bt_menu_car.setVisibility(View.VISIBLE);
		bt_menu_car.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_menu_car.getPaint().setAntiAlias(true);
		bt_menu_car.setOnClickListener(OCL);
		TextView bt_monitor_locus = (TextView)popView.findViewById(R.id.bt_monitor_locus);
		bt_monitor_locus.setVisibility(View.VISIBLE);
		bt_monitor_locus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_monitor_locus.getPaint().setAntiAlias(true);
		bt_monitor_locus.setOnClickListener(OCL);		
		int Speed = carinfos.get(i).getSpeed();
		String RegNum = carinfos.get(i).getObj_name();
		String MSTStatus = carinfos.get(i).getMDTStatus();
		String mileage = carinfos.get(i).getMileage();
		String gps_time = carinfos.get(i).getRcv_time();		
		tv_car_id.setText(RegNum);
		tv_car_MSTStatus.setText(MSTStatus + " ");
		tv_car_Mileage.setText(getString(R.string.car_mileage) + mileage + " km");
		tv_car_Speed.setText(getString(R.string.car_speed) + Speed + " km/h");
		tv_car_GpsTime.setText(gps_time);
		mMapView.updateViewLayout(popView, geoLP);
		popView.setVisibility(View.VISIBLE);
	}
	
	private final ItemizedOverlay.OnFocusChangeListener onFocusChangeListener = new ItemizedOverlay.OnFocusChangeListener() {
		@SuppressWarnings("rawtypes")
		public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
			if (popView != null) {
				popView.setVisibility(View.GONE);
			}
			if (newFocus != null) {
				MapView.LayoutParams geoLP = (MapView.LayoutParams) popView.getLayoutParams();
				geoLP.point = newFocus.getPoint();
				TextView tv_car_id = (TextView) popView.findViewById(R.id.pop_car_id);
				TextView tv_car_MSTStatus = (TextView) popView.findViewById(R.id.pop_car_MSTStatus);
				TextView tv_car_Mileage = (TextView) popView.findViewById(R.id.pop_car_Mileage);
				TextView tv_car_Speed = (TextView) popView.findViewById(R.id.pop_car_Speed);
				TextView tv_car_GpsTime = (TextView) popView.findViewById(R.id.pop_car_GpsTime);
				
				TextView bt_menu_car = (TextView)popView.findViewById(R.id.bt_menu_car);
				bt_menu_car.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				bt_menu_car.getPaint().setAntiAlias(true);
				bt_menu_car.setOnClickListener(OCL);
				TextView bt_monitor_locus = (TextView)popView.findViewById(R.id.bt_monitor_locus);
				bt_monitor_locus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				bt_monitor_locus.getPaint().setAntiAlias(true);
				bt_monitor_locus.setOnClickListener(OCL);
				
				String[] str = newFocus.getSnippet().split(",,");
				System.out.println(newFocus.getSnippet());
				tv_car_id.setText(str[0]);
				tv_car_GpsTime.setText(str[1]);
				tv_car_MSTStatus.setText(str[2]);
				tv_car_Speed.setText(getString(R.string.car_speed) + str[3]);
				tv_car_Mileage.setText(getString(R.string.car_mileage) + str[4]);

				mMapView.updateViewLayout(popView, geoLP);
				popView.setVisibility(View.VISIBLE);
			}
		}
	};	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, R.string.allcar_config);
		menu.add(0, 2, 0, R.string.bt_updatePwd);
		return super.onCreateOptionsMenu(menu);
	}	
	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent intent = new Intent(AVTActivity.this, ConfigActivity.class);
			startActivity(intent);
			break;
		case 2:
			ChangePwd();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	};
	String LoginPws;
	String newpwd;	
	/**
	 * 修改密码对话框
	 */
	private void ChangePwd(){
		View view = LayoutInflater.from(AVTActivity.this).inflate(R.layout.changepwd, null);
		final EditText et_oldpwd = (EditText)view.findViewById(R.id.oldPassWord_ET);
		final EditText et_newpwd = (EditText)view.findViewById(R.id.newPassWord_ET);
		final EditText et_newpwdtwo = (EditText)view.findViewById(R.id.newPassWordTwo_ET);
		AlertDialog.Builder bulder = new AlertDialog.Builder(AVTActivity.this);
		bulder.setView(view);
		bulder.setTitle(R.string.changePwd_title);// 设置标题
		bulder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				LoginPws = et_oldpwd.getText().toString().trim();
				newpwd = et_newpwd.getText().toString().trim();
				String newpwdtoo = et_newpwdtwo.getText().toString().trim();
				if(LoginPws.equals("")||newpwd.equals("")||newpwdtoo.equals("")){
					Toast.makeText(AVTActivity.this, R.string.change_pwd_null, Toast.LENGTH_SHORT).show();
					return;
				}else{
					if(newpwd.equals(newpwdtoo)){
						Dialog = ProgressDialog.show(AVTActivity.this,getString(R.string.login),getString(R.string.change_pwd_now),true);
						String urlString = Url + "customer/user/password?auth_code=" + auth_code + "&number_type="+number_type;
						List<NameValuePair> params1 = new ArrayList<NameValuePair>();
						params1.add(new BasicNameValuePair("user_name",Config.account));
						params1.add(new BasicNameValuePair("old_password",GetSystem.getM5DEndo(LoginPws)));
						params1.add(new BasicNameValuePair("new_password",GetSystem.getM5DEndo(newpwd)));
						new Thread(new NetThread.postDataThread(handler, urlString, params1, UpdatePwd)).start();
					}else{
						Toast.makeText(AVTActivity.this, R.string.change_pwd_TwoNewPwd_false, Toast.LENGTH_SHORT).show();
						return;
					}
				} 
			}
		});
		bulder.setNegativeButton(android.R.string.cancel, null);
		bulder.show();
	}	
	/**
	 * 开启个人定位
	 */
	private void getLocation(){
        // 获取位置管理服务
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);        
        networkListner=new MyLocationListner();
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, networkListner);
		gpsListener=new MyLocationListner();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, gpsListener);
		System.out.println("--当前位置---");
	}
	private  class MyLocationListner implements LocationListener{
		public void onLocationChanged(Location location) {
			Log.v("GPSTEST", "Got New Location of provider:"+location.getProvider());
			if(currentLocation!=null){
				if(isBetterLocation(location, currentLocation)){
					Log.v("GPSTEST", "It's a better location");
					currentLocation=location;
					showLocation(location);
				}else{
					Log.v("GPSTEST", "Not very good!");
				}
			}else{
				System.out.println("It's first location");
				Log.v("GPSTEST", "It's first location");
				currentLocation=location;
				showLocation(location);
			}
			//移除基于LocationManager.NETWORK_PROVIDER的监听器
			if(LocationManager.NETWORK_PROVIDER.equals(location.getProvider())){
				locationManager.removeUpdates(this);
			}
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
	}
	float Accuracy = 0;
	Location currentLocation;
	private void showLocation(Location location){
		Log.v("GPSTEST","Latitude:"+location.getLatitude());
		Log.v("GPSTEST","Longitude:"+location.getLongitude());
		Log.v("GPSTEST","Accuracy:"+location.getAccuracy());
		double latitude = location.getLatitude();
        double longitude= location.getLongitude();
        Accuracy = location.getAccuracy();
        if(!IsLock){
        	p = new GeoPoint((int)latitude*1000000, (int)longitude*1000000); // 得到经纬度        	
        	ShowMeLocation(p);
        }
     }
	private static final int CHECK_INTERVAL = 1000 * 30;
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		} 
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > CHECK_INTERVAL;
		boolean isSignificantlyOlder = timeDelta < -CHECK_INTERVAL;
		boolean isNewer = timeDelta > 0; 
		// If it's been more than two minutes since the current location,
		// use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must
			// be worse
		} else if (isSignificantlyOlder) {
			return false;
		} 
		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200; 
		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider()); 
		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	} 
	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ISSEARCH = false; // 停止线程
		IsUpdateMain = false;
		ISSTARTBAR = false;
		System.gc();
	}
	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("onPause");
		isPause = true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("onResume");
		isPause = false;
	}	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			System.out.println("IsLock:" + IsLock);
			if(IsLock){
				ISSTARTBAR = false;
				//停止轨迹回访
				IsLock = false;
				ClearOverlay(pathOverlays);
				Log.d(TAG, "pathOverlays.size() = "+pathOverlays.size());
				//删除轨迹回访跑的图标
				mapOverLays.remove(moveLocusOverlay);
				popView.setVisibility(View.GONE);
				ShowAllCar();
				mMapController.animateTo(Point);
				ShowPop(item);
				return false;
			}else{
				AlertDialog.Builder bulder = new AlertDialog.Builder(AVTActivity.this);
				bulder.setTitle(R.string.Note);// 设置标题
				bulder.setMessage(R.string.exit_content);
				bulder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				bulder.setNegativeButton(android.R.string.cancel, null);
				bulder.show();
			}
			break;
		}
    	return super.onKeyDown(keyCode, event);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	public boolean onDown(MotionEvent e) {
		return false;
	}
	public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX,float velocityY) {
		try {
			if (me1.getX() - me2.getX() > 120 && Math.abs(velocityX) > 0) {
				// 设置View进入屏幕时使用的动画
				this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.left_in));
				// 设置View出屏幕时使用的动画
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.left_out));
				// 显示下一个View
				this.flipper.showNext();
				return true;
			} else if (me1.getX() - me2.getX() < -120 && Math.abs(velocityX) > 0) {
				this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.right_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.right_out));
				// 显示上一个View
				this.flipper.showPrevious();
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	public void onLongPress(MotionEvent e) {}
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return false;
	}
	public void onShowPress(MotionEvent e) {}
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
    private void init(){
    	//手势控件
        gestureScanner = new GestureDetector(this);
		flipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
		LayoutInflater mLayoutInflater = LayoutInflater.from(AVTActivity.this);		
		//搜索
		View searchView = mLayoutInflater.inflate(R.layout.search, null);
		flipper.addView(searchView);
		s_contacter = (Spinner)searchView.findViewById(R.id.s_contacter);
		tv_SearchStatistic = (TextView)searchView.findViewById(R.id.tv_SearchStatistic);
		iv_ListClear = (ImageView)searchView.findViewById(R.id.iv_ListClear);
		iv_ListClear.setOnClickListener(OCL);
		iv_Map = (ImageView)searchView.findViewById(R.id.iv_Map);
		iv_Map.setOnClickListener(OCL);
		lv_cars = (XListView)findViewById(R.id.lv_cars);
		lv_cars.setOnItemClickListener(OICL);
		lv_cars.setPullLoadEnable(false);
		lv_cars.setPullRefreshEnable(false);
		lv_cars.setXListViewListener(this);
		lv_cars.setOnItemClickListener(OICL);
		lv_cars.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				AVTActivity.this.gestureScanner.onTouchEvent(event);
				return false;
			}
		});
		ListAutoComplete = (AutoCompleteTextView) searchView.findViewById(R.id.et_ListSearch);
		ListAutoComplete.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//autocomolete有内容显示清空按钮
				if(s.length() > 0){
					iv_ListClear.setVisibility(View.VISIBLE);
				}else{
					iv_ListClear.setVisibility(View.GONE);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {	}
			public void afterTextChanged(Editable s) {}
		});
		//地图
		View mapView = mLayoutInflater.inflate(R.layout.map, null);
		flipper.addView(mapView);
		layout_bar = (LinearLayout) mapView.findViewById(R.id.Layout_bar);
		bar = (ProgressBar) mapView.findViewById(R.id.show_bar);
		//跳转到列表页面
		ImageView iv_Search = (ImageView)mapView.findViewById(R.id.iv_Search);
		iv_Search.setOnClickListener(OCL);
		iv_MapClear = (ImageView)mapView.findViewById(R.id.iv_MapClear);
		iv_MapClear.setOnClickListener(OCL);
		iv_Me = (ImageView)mapView.findViewById(R.id.iv_Me);
		iv_Me.setOnClickListener(OCL);
		bt_ZoomDown = (Button)mapView.findViewById(R.id.bt_ZoomDown);
		bt_ZoomDown.setOnClickListener(OCL);
		bt_ZoomUp = (Button)mapView.findViewById(R.id.bt_ZoomUp);
		bt_ZoomUp.setOnClickListener(OCL);
		MapAutoComplete = (AutoCompleteTextView) mapView.findViewById(R.id.et_MapSearch);
		MapAutoComplete.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() > 0){
					iv_MapClear.setVisibility(View.VISIBLE);
				}else{
					iv_MapClear.setVisibility(View.GONE);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {	}
			public void afterTextChanged(Editable s) {}
		});
		tv_map_change = (TextView)mapView.findViewById(R.id.tv_map_change);
		tv_address = (TextView)mapView.findViewById(R.id.tv_address);
		tv_map_change.setOnClickListener(OCL);
		iv_play = (ImageView)mapView.findViewById(R.id.iv_play);
		iv_play.setOnClickListener(OCL);
		iv_pause = (ImageView)mapView.findViewById(R.id.iv_pause);
		iv_pause.setOnClickListener(OCL);
		iv_stop = (ImageView)mapView.findViewById(R.id.iv_stop);
		iv_stop.setOnClickListener(OCL);
		sb_speed = (SeekBar)mapView.findViewById(R.id.sb_speed);
		sb_speed.setOnSeekBarChangeListener(OSBCL);
		sb_speed.setProgress(50);		
		//默认显示地图页面
		flipper.setDisplayedChild(1);		
        mMapView = (MapView) mapView.findViewById(R.id.MapView);
		mMapController = mMapView.getController();
		mMapController.setZoom(15);
		mMapView.setEnabled(true);
		mMapView.setClickable(true);
		mMapView.setSatellite(IsSatellite); //地图
		mapOverLays = mMapView.getOverlays();		
		getDate();
		getLocation();
	}    
    /**
	 * 初始化数据
	 */
	private void getDate() {
		Intent intent = getIntent();
		Url = intent.getStringExtra("Url");
		auth_code = intent.getStringExtra("auth_code");
		cust_id = intent.getStringExtra("cust_id");		
		LoginName = intent.getStringExtra("LoginName");
		tree_path = intent.getStringExtra("tree_path");
		number_type = intent.getStringExtra("number_type");
		userid = intent.getIntExtra("userid", 0);
		SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences,Context.MODE_PRIVATE);
		ShortTime = (preferences.getInt("ShortTime", 20)) * 1000;
		Text_size = preferences.getInt("Text_size", 22);		
		// 初始化点击标注显示方式
		popView = super.getLayoutInflater().inflate(R.layout.pop, null);
		mMapView.addView(popView, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,0,0,
				MapView.LayoutParams.BOTTOM_CENTER));
		popView.setVisibility(View.GONE);
		Dialog = ProgressDialog.show(AVTActivity.this,getString(R.string.serach_pd_title),getString(R.string.serach_pd_context), true);
		String url = Url + "customer/" + cust_id + "/customer?auth_code=" + auth_code + "&tree_path=" + tree_path + "&page_no=1&page_count=100";
		new Thread(new NetThread.GetDataThread(handler, url, GetContacter)).start();
		String poiUrl = Url + "customer/" + cust_id + "/poi?auth_code=" + auth_code + "&is_geo=0&page_no=1&page_count=100";
		Log.d(TAG, poiUrl);
		new Thread(new NetThread.GetDataThread(handler, poiUrl, GetPoi)).start();
	}
	/**
	 * 删除对应的图层
	 * @param overlays
	 */
	private void ClearOverlay(List<Overlay> overlays){
		for (Overlay overlay : overlays) {
			mapOverLays.remove(overlay);
		}
		overlays.clear();
		System.gc();
	}
	public void onRefresh() {}
	/**
	 * 上拉加载
	 */
	public void onLoadMore() {//上拉加载
		if(Car_Page_total == Car_Page_Number){
			Car_Page ++ ;
			String url = Url + "customer/" + cust_id + "/vehicle?auth_code=" + auth_code + "&tree_path=" + contacterDatas.get(contacter_item).getTree_path() + "&mode=all&business_type=2&page_no=" + Car_Page + "&page_count=" + Car_Page_Number;
			new Thread(new NetThread.GetDataThread(handler, url, GetContacterCar)).start();
		}		
	}
	/**
	 * 上拉，下拉加载完数据后恢复
	 */
	private void onLoad() {
		lv_cars.stopRefresh();
		lv_cars.stopLoadMore();
		lv_cars.setRefreshTime(GetSystem.GetNowTime());
	}
}