package com.wise.util;

public class PoiData {
	public String type;
	public String lon;
	public String lat;
	public String name;
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "PoiData [type=" + type + ", lon=" + lon + ", lat=" + lat
				+ ", name=" + name + "]";
	}
}
