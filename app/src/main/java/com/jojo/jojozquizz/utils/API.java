package com.jojo.jojozquizz.utils;


import com.jojo.jojozquizz.model.reponse.BunchQuestionsResponse;
import com.jojo.jojozquizz.model.reponse.LastIdResponse;
import com.jojo.jojozquizz.model.reponse.QuestionResponse;
import com.jojo.jojozquizz.model.reponse.ServerKeyResponse;
import com.jojo.jojozquizz.model.reponse.VersionResponse;
import com.jojo.jojozquizz.model.requests.QuestionsRequest;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Path;

public interface API {

	@GET("v1/jojozquizz/version/currentVersion")
	Call<VersionResponse> getCurrentVersion();

	@GET("v1/auth/serverKey")
	Call<ServerKeyResponse> getServerKey();

	@GET("v1/jojozquizz/questions/lastId/{lang}")
	Call<LastIdResponse> getLastId(@Path("lang") String lang);

	@HTTP(method = "GET", path = "v1/jojozquizz/questions/question/{id}", hasBody = true)
	Call<QuestionResponse> getQuestion(@Path("id") long id, @Body HashMap<String, String> lang);

	@GET("v1/jojozquizz/questions/bunchOfQuestions")
	Call<BunchQuestionsResponse> getQuestions(@Body QuestionsRequest request);
}
