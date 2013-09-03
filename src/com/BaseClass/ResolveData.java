package com.BaseClass;

import org.json.JSONArray;

public class ResolveData{
	
	/**
	 * 获取车辆图标的4中状态
	 * @param Rcv_time
	 * @param jsonArray
	 * @param speed
	 * @return 1 离线；2 报警；3 行驶；4 静止 
	 */
	public static int getCarStatus(String Rcv_time,JSONArray jsonArray,int speed){
		if(AllStaticClass.GetTimeDiff(Rcv_time) > 10){
			return 1;
		}else if(getUniAlertsDesc(jsonArray).length() > 0){
			return 2;
		}else if(speed > 0){
			return 3;
		}else{
			return 4;
		}
	}
	/**
     * 判断当前状态
     * @param Gps_time gps时间
     * @param gps_flag flag
     * @param speed 速度
     * @param UniStatusDesc 状态
     * @param UniAlertsDesc 状态
     * @return
     */
	public static String getStatusDesc(String Rec_time, int gps_flag,int speed,String UniStatusDesc,String UniAlertsDesc){
    	String desc = "";
    	long time = GetSystem.GetTimeDiff(Rec_time);
    	if(time<10){//是否在线
    		if(gps_flag%2==0){
        		if(speed > 10){//速度判断
        			desc = "行驶," + UniStatusDesc + UniAlertsDesc + " " + speed + "公里/小时";
        		}else{
        			desc = "静止," + UniStatusDesc + UniAlertsDesc;
        		}
        	}else{
        		if(speed > 10){
        			desc = "盲区," + UniStatusDesc + UniAlertsDesc;
        		}else{
        			desc = "静止," + UniStatusDesc + UniAlertsDesc;
        		}
        	}
    	}else{
    		desc = "离线" + GetSystem.ShowOfflineTime(time);
    	}
    	if(desc.endsWith(",")){//格式化结果
    		desc = desc.substring(0, desc.length()-1);
    	}
    	return desc;
    }    
    static String STATUS_FORTIFY = "8193";
    static String STATUS_LOCK = "8194";
    static String STATUS_NETLOC = "8195";
    static String STATUS_SLEEP = "8197";
    public static String getUniStatusDesc(JSONArray jsonArray){
    	String str = "";
    	for(int i = 0 ; i < jsonArray.length() ; i++){
    		try {
    			String jsonString = jsonArray.getString(i);
    			if(jsonString.equals(STATUS_FORTIFY)){
    				str += "设防,";
    			}else if(jsonString.equals(STATUS_LOCK)){
    				str += "锁车,";
    			}else if(jsonString.equals(STATUS_FORTIFY)){
    				str += "基站定位,";
    			}else if(jsonString.equals(STATUS_FORTIFY)){
    				str += "省电状态,";
    			}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return str;
    }
    
    static String ALERT_SOS = "12289";
    static String ALERT_OVERSPEED = "12290";
    static String ALERT_VIRBRATE = "12291";
    static String ALERT_MOVE = "12292";
    static String ALERT_ALARM = "12293";
    static String ALERT_INVALIDRUN = "12294";
    static String ALERT_ENTERGEO = "12295";
    static String ALERT_EXITGEO = "12296";
    static String ALERT_CUTPOWER = "12297";
    static String ALERT_LOWPOWER = "12298";
    static String ALERT_GPSCUT = "12299";
    static String ALERT_OVERDRIVE = "12300";
    static String ALERT_INVALIDACC = "12301";
    static String ALERT_INVALIDDOOR = "12302";    
    public static String getUniAlertsDesc(JSONArray jsonArray){
    	String str = "";
    	for(int i = 0 ; i < jsonArray.length() ; i++){
    		try {
    			String jsonString = jsonArray.getString(i);
    			if(jsonString.equals(ALERT_SOS)){
    				str += "紧急报警,";
    			}else if(jsonString.equals(ALERT_OVERSPEED)){
    				str += "超速报警,";
    			}else if(jsonString.equals(ALERT_VIRBRATE)){
    				str += "震动报警,";
    			}else if(jsonString.equals(ALERT_MOVE)){
    				str += "位移报警,";
    			}else if(jsonString.equals(ALERT_ALARM)){
    				str += "防盗器报警,";
    			}else if(jsonString.equals(ALERT_INVALIDRUN)){
    				str += "非法行驶报警,";
    			}else if(jsonString.equals(ALERT_ENTERGEO)){
    				str += "进围栏报警,";
    			}else if(jsonString.equals(ALERT_EXITGEO)){
    				str += "出围栏报警,";
    			}else if(jsonString.equals(ALERT_CUTPOWER)){
    				str += "剪线报警,";
    			}else if(jsonString.equals(ALERT_LOWPOWER)){
    				str += "低电压报警,";
    			}else if(jsonString.equals(ALERT_GPSCUT)){
    				str += "GPS断线报警,";
    			}else if(jsonString.equals(ALERT_OVERDRIVE)){
    				str += "疲劳驾驶报警,";
    			}else if(jsonString.equals(ALERT_INVALIDACC)){
    				str += "非法点火报警,";
    			}else if(jsonString.equals(ALERT_INVALIDDOOR)){
    				str += "非法开门报警,";
    			}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return str;
    }
}