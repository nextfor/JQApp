package com.jojo.jojozquizz.utils;

import com.jojo.jojozquizz.model.reponse.BunchQuestionsResponse;
import com.jojo.jojozquizz.model.reponse.EventResponse;
import com.jojo.jojozquizz.model.reponse.LastIdResponse;
import com.jojo.jojozquizz.model.reponse.QuestionResponse;
import com.jojo.jojozquizz.model.reponse.ServerKeyResponse;
import com.jojo.jojozquizz.model.reponse.VersionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {

	@GET("v1/jojozquizz/version/currentVersion")
	Call<VersionResponse> getCurrentVersion();

	@GET("v1/auth/serverKey")
	Call<ServerKeyResponse> getServerKey();

	@GET("v1/jojozquizz/questions/lastId/{lang}")
	Call<LastIdResponse> getLastId(@Path("lang") String lang);

	@GET("v1/jojozquizz/questions/question/{id}")
	Call<QuestionResponse> getQuestion(@Path("id") long id, @Query("language") String lang);

	@GET("v1/jojozquizz/questions/bunchOfQuestions")
	Call<BunchQuestionsResponse> getQuestions(@Query("start") long start, @Query("end") long end, @Query("language") String language);

	@GET("v1/jojozquizz/questions/events/allEvents")
	Call<List<EventResponse>> getEvents();
}
