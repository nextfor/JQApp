package com.jojo.jojozquizz.utils;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
	private static Client instance = null;

	private API api;

	private Client(Context context) {
		Retrofit retrofit = new Retrofit.Builder()
//			.baseUrl(context.getResources().getString(BuildConfig.BUILD_TYPE.equals("debug") ? R.string.beta_api_domain : R.string.api_domain))
			.baseUrl("https://api.nextfor.fr/")
			.addConverterFactory(GsonConverterFactory.create())
			.build();

		this.api = retrofit.create(API.class);
	}


	public static Client getClient(Context context) {
		if (instance == null) {
			instance = new Client(context);
		}

		return instance;
	}

	public API getApi() {
		return api;
	}
}