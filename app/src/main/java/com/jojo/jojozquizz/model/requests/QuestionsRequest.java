package com.jojo.jojozquizz.model.requests;

public class QuestionsRequest extends QuestionRequest{

	long start;
	long end;
	String lang;

	public QuestionsRequest(long start, long end, String lang) {
		super(lang);
	}
}
