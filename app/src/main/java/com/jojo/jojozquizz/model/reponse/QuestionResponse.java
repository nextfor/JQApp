package com.jojo.jojozquizz.model.reponse;

public class QuestionResponse {

	long questionId;
	String question;
	String choices;
	int category;
	int difficulty;

	public QuestionResponse(long questionId, String question, String choices, int category, int difficulty) {
		this.questionId = questionId;
		this.question = question;
		this.choices = choices;
		this.category = category;
		this.difficulty = difficulty;
	}

	public long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getChoices() {
		return choices;
	}

	public void setChoices(String choices) {
		this.choices = choices;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
}
