package com.jojo.jojozquizz.model.reponse;

import com.jojo.jojozquizz.model.Question;

import java.util.ArrayList;

public class BunchQuestionsResponse {

	int items;
	long timestamp;
	ArrayList<Question> questions;

	public BunchQuestionsResponse(int items, long timestamp, ArrayList<Question> questions) {
		this.items = items;
		this.timestamp = timestamp;
		this.questions = questions;
	}

	public int getItems() {
		return items;
	}

	public void setItems(int items) {
		this.items = items;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}
}
