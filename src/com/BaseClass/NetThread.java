package com.BaseClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetThread {
	static int timeout = 20000;
	
	public static class GetDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		/**
		 * Get获取数据
		 * @param handler
		 * @param url
		 * @param what
		 */
		public GetDataThread(Handler handler,String url,int what){
			this.handler = handler;
			this.url = url;
			this.what =what;
		}
		@Override
		public void run() {
			super.run();
			String result = "";
			try {
				System.out.println(url);
				URL myURL = new URL(url);
				URLConnection httpsConn = (URLConnection) myURL.openConnection();
				httpsConn.setConnectTimeout(20*1000);
				httpsConn.setReadTimeout(20*1000);
				InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
				BufferedReader br = new BufferedReader(insr, 1024);
				String data = "";
				String line = "";
				while ((line = br.readLine()) != null) {
					data += line;
				}
				insr.close();
				result = data;				
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				Message message = new Message();
				message.what = what;
				message.obj = result;
				handler.sendMessage(message);
			}
		}
	}
	
	public static class postDataThread extends Thread{
		Handler handler;
		String url;
		int what;
		List<NameValuePair> params;
		public postDataThread(Handler handler,String url,List<NameValuePair> params,int what){
			this.handler = handler;
			this.url = url;
			this.what = what;
			this.params = params;
		}
		@Override
		public void run() {
			super.run();
			HttpPost httpPost = new HttpPost(url);
			try {
				 httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				 HttpClient client = new DefaultHttpClient();
				 client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
				 client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
				 HttpResponse httpResponse = client.execute(httpPost);
				 if(httpResponse.getStatusLine().getStatusCode() == 200){
					 String strResult = EntityUtils.toString(httpResponse.getEntity());
					 Message message = new Message();
					 message.what = what;
					 message.obj = strResult;
					 handler.sendMessage(message);
				 }else{
					 Log.d("tag", "状态" +httpResponse.getStatusLine().getStatusCode());
				 }
			} catch (Exception e) {
				Message message = new Message();
				message.what = what;
				message.obj = "";
				handler.sendMessage(message);
				e.printStackTrace();
			}
		}
	}
	public static class GetLocation implements Runnable{
		String tLat;
		String tLon;
		Handler tHandler;
		int tWhere;

		Context mContext;
		/**
		 * 获取地址
		 * @param Lat
		 * @param Lon
		 * @param handler
		 */
		public GetLocation(String Lat, String Lon,Handler handler,int where,Context context) {
			tLat = Lat;
			tLon = Lon;
			tHandler = handler;
			tWhere = where;
			mContext = context;
		}

		public void run() {
			String Address = AllStaticClass.geocodeAddr(mContext,tLat, tLon);
			if(Address == null || Address.equals("")){
				System.out.println("没找到地址");
			}else{
				Message msg = new Message();
				msg.what = tWhere;
				msg.obj = Address;
				tHandler.sendMessage(msg);
			}
		}
	}
}
