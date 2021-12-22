package com.jojo.jojozquizz.utils;

import android.content.Context;

import com.jojo.jojozquizz.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
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

	public void addInterceptor(String key) {
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		httpClient.addInterceptor(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Request request = chain.request().newBuilder().addHeader("app-auth", key).build();
				return chain.proceed(request);
			}
		});
		if (BuildConfig.BUILD_TYPE == "debug") {
			httpClient.addInterceptor(interceptor);
		}
		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl("https://api.nextfor.fr/")
			.client(httpClient.build())
			.build();
		this.api = retrofit.create(API.class);
	}

	public API getApi() {
		return api;
	}
}