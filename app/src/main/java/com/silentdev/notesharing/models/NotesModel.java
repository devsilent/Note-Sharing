package com.silentdev.notesharing.models;

import com.google.firebase.Timestamp;

public class NotesModel {

    String title, body, userId, sharerId;
    Timestamp date_added, date_edited;
    Boolean shared;

    public NotesModel() {}

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getUserId() {
        return userId;
    }

    public String getSharerId() {
        return sharerId;
    }

    public Timestamp getDate_added() {
        return date_added;
    }

    public Timestamp getDate_edited() {
        return date_edited;
    }

    public Boolean getShared() {
        return shared;
    }
}
