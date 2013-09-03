package com.wise.util;

public class TotalData {
	private String Toatal;
	private String AccOn;
	private String AccOff;
	public String getToatal() {
		return Toatal;
	}
	public void setToatal(String toatal) {
		Toatal = toatal;
	}
	public String getAccOn() {
		return AccOn;
	}
	public void setAccOn(String accOn) {
		AccOn = accOn;
	}
	public String getAccOff() {
		return AccOff;
	}
	public void setAccOff(String accOff) {
		AccOff = accOff;
	}
	@Override
	public String toString() {
		return "TotalData [Toatal=" + Toatal + ", AccOn=" + AccOn + ", AccOff="
				+ AccOff + "]";
	}
}
