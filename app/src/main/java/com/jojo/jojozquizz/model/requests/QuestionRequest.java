package com.jojo.jojozquizz.model.requests;

public class QuestionRequest {

	private String language;

	public QuestionRequest(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setLang(String language) {
		this.language = language;
	}
}
