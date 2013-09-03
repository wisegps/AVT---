package com.BaseClass;

import org.json.JSONArray;

public class ResolveData{
	
	/**
	 * ��ȡ����ͼ���4��״̬
	 * @param Rcv_time
	 * @param jsonArray
	 * @param speed
	 * @return 1 ���ߣ�2 ������3 ��ʻ��4 ��ֹ 
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
     * �жϵ�ǰ״̬
     * @param Gps_time gpsʱ��
     * @param gps_flag flag
     * @param speed �ٶ�
     * @param UniStatusDesc ״̬
     * @param UniAlertsDesc ״̬
     * @return
     */
	public static String getStatusDesc(String Rec_time, int gps_flag,int speed,String UniStatusDesc,String UniAlertsDesc){
    	String desc = "";
    	long time = GetSystem.GetTimeDiff(Rec_time);
    	if(time<10){//�Ƿ�����
    		if(gps_flag%2==0){
        		if(speed > 10){//�ٶ��ж�
        			desc = "��ʻ," + UniStatusDesc + UniAlertsDesc + " " + speed + "����/Сʱ";
        		}else{
        			desc = "��ֹ," + UniStatusDesc + UniAlertsDesc;
        		}
        	}else{
        		if(speed > 10){
        			desc = "ä��," + UniStatusDesc + UniAlertsDesc;
        		}else{
        			desc = "��ֹ," + UniStatusDesc + UniAlertsDesc;
        		}
        	}
    	}else{
    		desc = "����" + GetSystem.ShowOfflineTime(time);
    	}
    	if(desc.endsWith(",")){//��ʽ�����
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
    				str += "���,";
    			}else if(jsonString.equals(STATUS_LOCK)){
    				str += "����,";
    			}else if(jsonString.equals(STATUS_FORTIFY)){
    				str += "��վ��λ,";
    			}else if(jsonString.equals(STATUS_FORTIFY)){
    				str += "ʡ��״̬,";
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
    				str += "��������,";
    			}else if(jsonString.equals(ALERT_OVERSPEED)){
    				str += "���ٱ���,";
    			}else if(jsonString.equals(ALERT_VIRBRATE)){
    				str += "�𶯱���,";
    			}else if(jsonString.equals(ALERT_MOVE)){
    				str += "λ�Ʊ���,";
    			}else if(jsonString.equals(ALERT_ALARM)){
    				str += "����������,";
    			}else if(jsonString.equals(ALERT_INVALIDRUN)){
    				str += "�Ƿ���ʻ����,";
    			}else if(jsonString.equals(ALERT_ENTERGEO)){
    				str += "��Χ������,";
    			}else if(jsonString.equals(ALERT_EXITGEO)){
    				str += "��Χ������,";
    			}else if(jsonString.equals(ALERT_CUTPOWER)){
    				str += "���߱���,";
    			}else if(jsonString.equals(ALERT_LOWPOWER)){
    				str += "�͵�ѹ����,";
    			}else if(jsonString.equals(ALERT_GPSCUT)){
    				str += "GPS���߱���,";
    			}else if(jsonString.equals(ALERT_OVERDRIVE)){
    				str += "ƣ�ͼ�ʻ����,";
    			}else if(jsonString.equals(ALERT_INVALIDACC)){
    				str += "�Ƿ���𱨾�,";
    			}else if(jsonString.equals(ALERT_INVALIDDOOR)){
    				str += "�Ƿ����ű���,";
    			}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return str;
    }
}