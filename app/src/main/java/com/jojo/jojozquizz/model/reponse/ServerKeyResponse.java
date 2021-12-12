package com.jojo.jojozquizz.model.reponse;

public class ServerKeyResponse {

	String token;

	public ServerKeyResponse(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
