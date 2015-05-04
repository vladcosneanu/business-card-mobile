package com.business.card.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Event implements Serializable {

    private static final long serialVersionUID = -6123889786534787077L;

    private String id;
    private String name;
    private String location;
    private String date;
    private String passcode;

    public static Event parseEventFromJson(JSONObject json) {
        Event event = new Event();
        try {
            event.setId(json.getString("id"));
            event.setName(json.getString("name"));
            event.setLocation(json.getString("location"));
            event.setDate(json.getString("date"));
            event.setPasscode(json.getString("passcode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return event;
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
