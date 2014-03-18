package com.myapp.util;



public class Wifi {
	private String ssid;
	private String bssid;
	public static final String KEY_BSSID = "BSSID";
	public static final String KEY_SSID = "SSID";
	public static final int CHOOSE_WIFI = 1;

	public Wifi(String ssid, String bssid) {
		this.ssid = ssid;
		this.bssid = bssid;
	}

	public String getSsid() {
		return ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	@Override
	public String toString() {
		return ("SSID: " + ssid + ", BSSID: " + bssid);
	}

	@Override
	public boolean equals(Object o) {
		Wifi toCompare = (Wifi) o;

		if (toCompare != null && this.ssid.equals(toCompare.getSsid())
				&& this.bssid.equals(toCompare.getBssid())) {
			return true;
		}
		else
			return false;
	}
}
