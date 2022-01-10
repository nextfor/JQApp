package com.jojo.jojozquizz.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetUtils {

	public static int getNetworkType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getActiveNetworkInfo() != null)
			return connectivityManager.getActiveNetworkInfo().getType();
		else
			return -10;
	}

	public static boolean isConnected(Context context) {
		int networkType = getNetworkType(context);
		switch (networkType) {
			case ConnectivityManager.TYPE_MOBILE:
			case ConnectivityManager.TYPE_WIFI:
				return true;
			default:
				return false;
		}
	}

}