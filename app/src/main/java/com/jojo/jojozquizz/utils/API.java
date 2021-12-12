package com.jojo.jojozquizz.utils;


import com.jojo.jojozquizz.model.reponse.ServerKeyResponse;
import com.jojo.jojozquizz.model.reponse.VersionResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface API {

	@GET("v1/jojozquizz/version/currentVersion")
	Call<VersionResponse> getCurrentVersion();

	@GET("v1/auth/serverKey")
	Call<ServerKeyResponse> getServerKey();
}
