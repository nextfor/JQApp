package com.jojo.jojozquizz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jojo.jojozquizz.model.reponse.QuestionResponse;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "questions")
public class Question implements Serializable {

	@PrimaryKey()
	@ColumnInfo(name = "id")
	private long id;

	@SerializedName(value = "question")
	@ColumnInfo(name = "question")
	private String question;

	@Ignore
	private List<String> choiceList;

	@SerializedName("choices")
	@ColumnInfo(name = "choices")
	private String choices;

	@Ignore
	private int answerIndex;

	@ColumnInfo(name = "categorie")
	private int category;

	@Ignore
	private String stringCategory;

	@Ignore
	private String stringDifficulty;

	@Ignore
	private String trueAnswer;

	@ColumnInfo(name = "difficulty")
	private int difficulty;

	public Question() {
	}

	public Question(int id, String question, String choices, int category, int difficulty) {
		this.id = id;
		this.question = question;
		this.choices = choices;
		this.answerIndex = 0;
		this.category = category;
		this.difficulty = difficulty;
	}

	public Question(int id, String question, List<String> choiceList, int category, int difficulty) {
		this.id = id;
		this.question = question;
		this.choices = choiceList.get(0) + "-/-" + choiceList.get(1) + "-/-" + choiceList.get(2) + "-/-" + choiceList.get(3);
		this.answerIndex = 0;
		this.category = category;
		this.difficulty = difficulty;
	}

	public Question(String question, List<String> choiceList, int category, int difficulty) {
		this.question = question;
		this.choices = choiceList.get(0) + "-/-" + choiceList.get(1) + "-/-" + choiceList.get(2) + "-/-" + choiceList.get(3);
		this.answerIndex = 0;
		this.category = category;
		this.difficulty = difficulty;
	}

	public Question(QuestionResponse response) {
		this.id = response.getQuestionId();
		this.question = response.getQuestion();
		this.choices = response.getChoices();
		this.category = response.getCategory();
		this.difficulty = response.getDifficulty();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getChoiceList() {
		return choiceList;
	}

	public void setChoiceList(List<String> choiceList) {
		this.choiceList = choiceList;
	}

	public String getChoices() {
		return choices;
	}

	public void setChoices(String choices) {
		this.choices = choices;
	}

	public int getAnswerIndex() {
		return answerIndex;
	}

	public void setAnswerIndex(int answerIndex) {
		this.answerIndex = answerIndex;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getStringCategory() {
		return stringCategory;
	}

	public void setStringCategory(String stringCategory) {
		this.stringCategory = stringCategory;
	}

	public String getStringDifficulty() {
		return stringDifficulty;
	}

	public void setStringDifficulty(String stringDifficulty) {
		this.stringDifficulty = stringDifficulty;
	}

	public String getTrueAnswer() {
		return trueAnswer;
	}

	public void setTrueAnswer(String trueAnswer) {
		this.trueAnswer = trueAnswer;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
}
