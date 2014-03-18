package com.myapp.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.myapp.model.DataBaseHandler;
import com.myapp.util.Wifi;

public class WifiReceiver extends BroadcastReceiver{
	private WifiManager wifiManager;
	private static Wifi currentWifi = null;

	public WifiReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		wifiManager = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
		DataBaseHandler db = DataBaseHandler.getInstance(context);

		final String action = intent.getAction();

		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

			// Check if the connection event was on the wifi
			if (netInfo.getType() != ConnectivityManager.TYPE_WIFI)
				return;

			// Do checkin if connected and checkout if not
			if (netInfo.isConnected()) {
				// Get the actual wifi (ssid and bssid)
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				Wifi wifi = new Wifi(wifiInfo.getSSID().replace("\"", ""), wifiInfo.getBSSID());

				if (currentWifi == null) {
					currentWifi = wifi;
					db.addCheckIn(wifi);
				} else if (!wifi.equals(currentWifi)) {
					db.addCheckOut(currentWifi); // verify this case
					currentWifi = wifi;
					db.addCheckIn(wifi);
				}

			} else if (currentWifi != null){
				db.addCheckOut(currentWifi);
				currentWifi = null;
			}

			db.close();
		}
	}
}
