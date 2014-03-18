package com.myapp.util;

public class Task {
	private int id;
	private final String name;
	private String description;
	private String ssid;
	private String bssid;

	public Task(String name, String description, String ssid,
			String bssid) {
		this.name = name;
		this.description = description;
		this.ssid = ssid;
		this.bssid = bssid;
	}

	public Task(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getSsid() {
		return ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Task: " + name;
	}
}