package com.myapp.timerecordingapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.myapp.util.Wifi;

public class SelectWifiActivity extends Activity{

	private ArrayAdapter<String> arrAdapter;
	private WifiManager wifiManager;
	private List<ScanResult> results;
	private ArrayList<String> ssidList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectwifi);

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		ssidList = new ArrayList<String>();
		arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ssidList);

		ListView wifiListView = (ListView) findViewById(R.id.listview);
		wifiListView.setAdapter(arrAdapter);
		wifiListView.setClickable(true);
		wifiListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				Intent resultIntent = new Intent();
				resultIntent.putExtra(Wifi.KEY_SSID, results.get(position).SSID);
				resultIntent.putExtra(Wifi.KEY_BSSID, results.get(position).BSSID);

				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});

		Button refreshButton = (Button) findViewById(R.id.refreshButton);
		refreshButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!wifiManager.isWifiEnabled()) {
					Toast.makeText(getApplicationContext(), "Your Wifi is disabled", Toast.LENGTH_LONG).show();
					return;
				}
				wifiManager.startScan();
				ssidList.clear();
				arrAdapter.clear();

				results = wifiManager.getScanResults();
				for (ScanResult wifi : results)
					ssidList.add(wifi.SSID);

				arrAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		results = wifiManager.getScanResults();
		ssidList.clear();
		for (ScanResult wifi : results)
			ssidList.add(wifi.SSID);

		arrAdapter.notifyDataSetChanged();
	}
}
