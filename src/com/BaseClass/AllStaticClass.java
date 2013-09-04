package com.BaseClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.avtunicom.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class AllStaticClass {
	static int px;
	public static Drawable FindPoiIcon(Context context, String icon){
		//postoffice
		String[] array_poi = {"bank","buildings","convenience_store","fast_food","fuel","hospital","house","mail_post","parking","school"};
		int[] array_Dra_poi = {R.drawable.bank,R.drawable.buildings,R.drawable.convenience_store,R.drawable.fast_food,R.drawable.fuel,R.drawable.hospital,R.drawable.house,R.drawable.mail_post,R.drawable.parking,R.drawable.school};
		for(int i = 0 ; i < array_poi.length ; i++){
			if(array_poi[i].equals(icon)){
				Drawable drawable = context.getResources().getDrawable(array_Dra_poi[i]);
				return drawable;
			}
		}
		Drawable drawable = context.getResources().getDrawable(R.drawable.other);
		return drawable;
	}
	public static BitmapDrawable DrawableBimpMap(Context context ,int CarStatus , String Rotate){
		Bitmap car_alert = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_alert);
		Bitmap car_out = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_out);
		Bitmap car_off = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_off);
		Bitmap car_on = BitmapFactory.decodeResource(context.getResources(),R.drawable.car_on);
		int rotate = Integer.valueOf(Rotate);
		if(CarStatus == 1){
			return DrawableBimp(rotate, car_out);
		}else if(CarStatus == 2){
			return DrawableBimp(rotate, car_alert);
		}else if(CarStatus == 3){
			return DrawableBimp(rotate, car_on);
		}else{
			return DrawableBimp(rotate, car_off);
		}
	}
	public static BitmapDrawable DrawableBimp(int rotate , Bitmap bitmapOrg){
			//获取这个图片的宽和高
			int width = bitmapOrg.getWidth(); 
			int height = bitmapOrg.getHeight(); 
			//定义预转换成的图片的宽度和高度
			int newWidth = width; 
			int newHeight = height; 
			//System.out.println("new:" + px + "old:" + width);
			//计算缩放率，新尺寸除原始尺寸
			float scaleWidth = ((float) newWidth) / width; 
			float scaleHeight = ((float) newHeight) / height; 
			// 创建操作图片用的matrix对象
			Matrix matrix = new Matrix(); 
			// 缩放图片动作
			matrix.postScale(scaleWidth, scaleHeight); 
			//旋转图片 动作
			matrix.postRotate(rotate, (float)width/2, (float)height/2);
			// 创建新的图片
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true); 
			//将上面创建的Bitmap转换成Drawable对象，使得其可以使用在ImageView, ImageButton中
			BitmapDrawable bmd = new BitmapDrawable(resizedBitmap); 
			return bmd;
	}
	/**
	 * 验证时间是否在范围内
	 * @param startTime
	 * @param StopTime
	 * @return
	 */
	public static boolean LimitTime(String startTime, String StopTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date begin = sdf.parse(startTime);
			java.util.Date end = sdf.parse(StopTime);
			long between = (end.getTime() - begin.getTime())/(1000*60);
			if(between > (60*24)){
				return false;
			}else if (between < 0) {
				return false;
			}else{
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 调整时间格式
	 * @param i
	 * @return
	 */
	public static String intToString(int i) {
		String str = null;
		if (i < 10) {
			str = "0" + i;
		} else {
			str = "" + i;
		}
		return str;
	}
	/**
	 * 经纬度转换
	 * @param str
	 * @return
	 */
	public static int StringToInt(String str) {
		Double point_doub = Double.parseDouble(str);
		return (int) (point_doub * 1000000);
	}
	/**
     * 返回离当前时间的时间差（分钟）
     * @param 计算离当前的时间差
     * @return 返回分钟
     */
    public static long GetTimeDiff(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date begin = sdf.parse(time);
			java.util.Date end = sdf.parse(sdf.format(new Date()));
			long between = (end.getTime() - begin.getTime())/(1000*60);
			return between;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	public static String geocodeAddr(Context context, String latitude, String longitude) { 
		String addr = ""; 
		String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+ latitude +"," + longitude + "&sensor=true";
		System.out.println(url);
		URL myURL = null; 
		URLConnection httpsConn = null; 
		try { 
			myURL = new URL(url); 
		} catch (MalformedURLException e) { 
			e.printStackTrace(); 
			return null; 
		} 
		try { 
			httpsConn = (URLConnection) myURL.openConnection(); 
			if (httpsConn != null) { 
				System.out.println("开始读取数据");
				InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8"); 
				BufferedReader br = new BufferedReader(insr,1024); 
				String data = ""; 
				String line = "";
				while((line = br.readLine())!= null){
					data += line ;
				}
				insr.close(); 
				return returnAddress(data);
			} 
		} catch (Exception e) { 
			e.printStackTrace(); 
			return null; 
		}
		return addr; 
	} 
	/**
	 * 返回地址
	 * @param str
	 * @return
	 */
	public static String returnAddress(String str){
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			JSONObject temp = (JSONObject)jsonArray.get(0);
			String address = temp.getString("formatted_address");
			String returnAddress;
			if(address.indexOf("邮政编码") > -1){
				returnAddress = address.substring(0, address.indexOf("邮政编码"));
			}else{
				returnAddress = address;
			}
			return returnAddress;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
}