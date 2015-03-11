package com.business.card.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class BusinessCard {

    private String id;
    private String userId;
    private String email;
    private String phone;
    private String address;

    public static BusinessCard parseBusinessCardFromJson(JSONObject json) {
        BusinessCard user = new BusinessCard();
        try {
            user.setId(json.getString("id"));
            user.setUserId(json.getString("user_id"));
            user.setEmail(json.getString("email"));
            user.setPhone(json.getString("phone"));
            user.setAddress(json.getString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
