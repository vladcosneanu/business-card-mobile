package com.business.card.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String id;
    private String title;
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    public static User parseUserFromJson(JSONObject json) {
        User user = new User();
        try {
            user.setId(json.getString("id"));
            user.setTitle(json.getString("title"));
            user.setFirstName(json.getString("first_name"));
            user.setLastName(json.getString("last_name"));
            user.setUsername(json.getString("username"));
            user.setPassword(json.getString("password"));
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
