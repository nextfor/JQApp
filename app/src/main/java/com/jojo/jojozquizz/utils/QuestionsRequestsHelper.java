package com.jojo.jojozquizz.utils;

import android.content.Context;

import com.jojo.jojozquizz.model.reponse.LastIdResponse;
import com.jojo.jojozquizz.model.reponse.QuestionResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionsRequestsHelper {

	public static void getLastId(Context context, String lang, ResponseListener listener) {
		Call<LastIdResponse> call = Client.getClient(context).getApi().getLastId(lang);
		call.enqueue(new Callback<LastIdResponse>() {
			@Override
			public void onResponse(Call<LastIdResponse> call, Response<LastIdResponse> response) {
				listener.onIdResponse(call, response);
			}

			@Override
			public void onFailure(Call<LastIdResponse> call, Throwable t) {
				listener.onIdFailure(call, t);
			}
		});
	}

	public static void getQuestion(Context context, String lang, int id, ResponseListener listener) {
		Call<QuestionResponse> call = Client.getClient(context).getApi().getQuestion(id);
		call.enqueue(new Callback<QuestionResponse>() {
			@Override
			public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
				listener.onQuestionResponse(call, response);
			}

			@Override
			public void onFailure(Call<QuestionResponse> call, Throwable t) {
				listener.onQuestionFailure(call, t);
			}
		});
	}

	public interface ResponseListener {
		void onIdResponse(Call<LastIdResponse> call, Response<LastIdResponse> response);

		void onIdFailure(Call<LastIdResponse> call, Throwable t);

		void onQuestionResponse(Call<QuestionResponse> call, Response<QuestionResponse> response);

		void onQuestionFailure(Call<QuestionResponse> call, Throwable t);
	}
}
