package com.business.card.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class Conference {

    private String id;
    private String name;
    private String location;
    private String date;
    private String passcode;

    public static Conference parseConferenceFromJson(JSONObject json) {
        Conference conference = new Conference();
        try {
            conference.setId(json.getString("id"));
            conference.setName(json.getString("name"));
            conference.setLocation(json.getString("location"));
            conference.setDate(json.getString("date"));
            conference.setPasscode(json.getString("passcode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return conference;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }
}
