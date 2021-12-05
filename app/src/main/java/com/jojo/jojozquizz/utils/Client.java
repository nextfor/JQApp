package com.jojo.jojozquizz.utils;

import android.content.Context;

import com.jojo.jojozquizz.BuildConfig;
import com.jojo.jojozquizz.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
	private static Retrofit retrofit;

	public static Retrofit getClient(Context context) {
		if (retrofit == null) {
			retrofit = new Retrofit.Builder()
				.baseUrl(context.getResources().getString(BuildConfig.BUILD_TYPE.equals("debug") ? R.string.beta_api_domain : R.string.api_domain))
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		}
		return retrofit;
	}
}