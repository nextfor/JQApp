package com.jojo.jojozquizz.model.reponse;

public class ServerKeyResponse {

	String key;
	long timestamp;

	public ServerKeyResponse(String key, long timestamp) {
		this.key = key;
		this.timestamp = timestamp;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
