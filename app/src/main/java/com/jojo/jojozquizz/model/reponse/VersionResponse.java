package com.jojo.jojozquizz.model.reponse;

public class VersionResponse {

	int version;
	String versionString;
	String updatedAt;
	String minimumAndroidVersion;
	String pegi;

	public VersionResponse(int version, String versionString, String updatedAt, String minimumAndroidVersion, String pegi) {
		this.version = version;
		this.versionString = versionString;
		this.updatedAt = updatedAt;
		this.minimumAndroidVersion = minimumAndroidVersion;
		this.pegi = pegi;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getVersionString() {
		return versionString;
	}

	public void setVersionString(String versionString) {
		this.versionString = versionString;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getMinimumAndroidVersion() {
		return minimumAndroidVersion;
	}

	public void setMinimumAndroidVersion(String minimumAndroidVersion) {
		this.minimumAndroidVersion = minimumAndroidVersion;
	}

	public String getPegi() {
		return pegi;
	}

	public void setPegi(String pegi) {
		this.pegi = pegi;
	}
}