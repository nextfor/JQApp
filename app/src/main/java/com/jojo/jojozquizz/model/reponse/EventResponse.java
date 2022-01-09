package com.jojo.jojozquizz.model.reponse;

import com.google.gson.annotations.SerializedName;
import com.jojo.jojozquizz.model.Partnership;
import com.jojo.jojozquizz.model.Question;

import java.util.Date;
import java.util.List;

public class EventResponse {
	private String name;
	private String startDate;
	private String endDate;
	private String image;
	private List<Question> questions;
	private Partnership partnership;

	public EventResponse(String name, String startDate, String endDate, String image, List<Question> questions, Partnership partnership) {
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.image = image;
		this.questions = questions;
		this.partnership = partnership;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestion(List<Question> questions) {
		this.questions = questions;
	}

	public Partnership getPartnership() {
		return partnership;
	}

	public void setPartnership(Partnership partnership) {
		this.partnership = partnership;
	}
}
