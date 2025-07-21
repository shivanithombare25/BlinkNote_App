package com.example.firstdemoactivity;

import java.io.Serializable;

public class Note implements Serializable {
    private String title;
    private String description;
    private String date;
    private long createdTime;
    private long updatedTime;
    private boolean isTrashed = false;

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
        this.createdTime = System.currentTimeMillis();
        this.updatedTime = System.currentTimeMillis();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public boolean isTrashed() {
        return isTrashed;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedTime = System.currentTimeMillis();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedTime = System.currentTimeMillis();
    }

    public void setTrashed(boolean trashed) {
        isTrashed = trashed;
    }
}
