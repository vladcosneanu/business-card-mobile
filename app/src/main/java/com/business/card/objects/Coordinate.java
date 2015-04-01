package com.business.card.objects;

public class Coordinate {

	private float longitude;
	private float latitude;
	private boolean valid;
	private int accuracy;

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public int getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(int accurancy) {
		this.accuracy = accurancy;
	}

}
