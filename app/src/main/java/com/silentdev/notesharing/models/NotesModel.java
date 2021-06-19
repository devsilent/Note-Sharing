package com.silentdev.notesharing.models;

import com.google.firebase.Timestamp;

public class NotesModel {

    String title, body, userId, sharerId;
    Timestamp date_added, date_edited;
    Boolean shared;

    public NotesModel() {}

    public NotesModel(String title, String body, String userId, String sharerId, Timestamp date_added, Timestamp date_edited, Boolean shared) {
        this.title = title;
        this.body = body;
        this.userId = userId;
        this.sharerId = sharerId;
        this.date_added = date_added;
        this.date_edited = date_edited;
        this.shared = shared;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSharerId() {
        return sharerId;
    }

    public void setSharerId(String sharerId) {
        this.sharerId = sharerId;
    }

    public Timestamp getDate_added() {
        return date_added;
    }

    public void setDate_added(Timestamp date_added) {
        this.date_added = date_added;
    }

    public Timestamp getDate_edited() {
        return date_edited;
    }

    public void setDate_edited(Timestamp date_edited) {
        this.date_edited = date_edited;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }
}
