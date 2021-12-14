package com.jojo.jojozquizz.utils;


import com.jojo.jojozquizz.model.reponse.LastIdResponse;
import com.jojo.jojozquizz.model.reponse.QuestionResponse;
import com.jojo.jojozquizz.model.reponse.ServerKeyResponse;
import com.jojo.jojozquizz.model.reponse.VersionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface API {

	@GET("v1/jojozquizz/version/currentVersion")
	Call<VersionResponse> getCurrentVersion();

	@GET("v1/auth/serverKey")
	Call<ServerKeyResponse> getServerKey();

	@GET("v1/jojozquizz/questions/lastId/{lang}")
	Call<LastIdResponse> getLastId(@Path("lang") String lang);

	@GET("v1/jojozquizz/question/{id}")
	Call<QuestionResponse> getQuestion(@Path("id") long id);
}
