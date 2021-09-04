package com.jojo.jojozquizz.tools;

public class SecurityKey {

	private static SecurityKey instance = new SecurityKey();
	private String key = null;

	private SecurityKey() {
	}

	public static SecurityKey getInstance() {
		return instance;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String k) {
		this.key = k;
	}
}
