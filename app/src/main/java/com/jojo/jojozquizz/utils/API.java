package com.jojo.jojozquizz.utils;


import com.jojo.jojozquizz.model.reponse.BasicResponse;
import com.jojo.jojozquizz.model.reponse.VersionResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface API {

	@GET("v1/ping/")
	Call<BasicResponse> ping();

	@GET("v1/jojozquizz/version/currentVersion")
	Call<VersionResponse> getCurrentVersion();
}
