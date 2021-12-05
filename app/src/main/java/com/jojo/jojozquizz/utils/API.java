package com.jojo.jojozquizz.utils;


import retrofit2.Call;
import retrofit2.http.GET;

public interface API {

	@GET("v1/ping/")
	Call<String> ping();
}
