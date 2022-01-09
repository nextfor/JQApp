package com.jojo.jojozquizz.model.reponse;

public class LastIdResponse {

	long questionId;

	public LastIdResponse(long questionId) {
		this.questionId = questionId;
	}

	public long getQuestionId() {
		return questionId;
	}

	public void setSuestionId(long questionId) {
		this.questionId = questionId;
	}
}
