package com.wise.util;

public class CarInfo{
	private String MDTStatus;   //������ʾ״̬
	private String ObjectID;  //������ʶ
	private String Rcv_time; //ʱ��
	private String Lon; //λ��
	private String Lat; //λ��
	private int Speed; //�ж�״̬
	private String Mileage; //���
	private String Direct; //����
	private String obj_name; //������
	private int CarStatus; //����״̬
	public String getMDTStatus() {
		return MDTStatus;
	}
	public void setMDTStatus(String mDTStatus) {
		MDTStatus = mDTStatus;
	}
	public String getObjectID() {
		return ObjectID;
	}
	public void setObjectID(String objectID) {
		ObjectID = objectID;
	}
	public String getRcv_time() {
		return Rcv_time;
	}
	public void setRcv_time(String rcv_time) {
		Rcv_time = rcv_time;
	}
	public String getLon() {
		return Lon;
	}
	public void setLon(String lon) {
		Lon = lon;
	}
	public String getLat() {
		return Lat;
	}
	public void setLat(String lat) {
		Lat = lat;
	}
	public int getSpeed() {
		return Speed;
	}
	public void setSpeed(int speed) {
		Speed = speed;
	}
	public String getMileage() {
		return Mileage;
	}
	public void setMileage(String mileage) {
		Mileage = mileage;
	}
	public String getDirect() {
		return Direct;
	}
	public void setDirect(String direct) {
		Direct = direct;
	}
	public String getObj_name() {
		return obj_name;
	}
	public void setObj_name(String obj_name) {
		this.obj_name = obj_name;
	}
	public int getCarStatus() {
		return CarStatus;
	}
	public void setCarStatus(int carStatus) {
		CarStatus = carStatus;
	}
	@Override
	public String toString() {
		return "CarInfo [MDTStatus=" + MDTStatus + ", ObjectID=" + ObjectID
				+ ", Rcv_time=" + Rcv_time + ", Lon=" + Lon + ", Lat=" + Lat
				+ ", Speed=" + Speed + ", Mileage=" + Mileage + ", Direct="
				+ Direct + ", obj_name=" + obj_name + ", CarStatus="
				+ CarStatus + "]";
	}			
}