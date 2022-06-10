package com.chetna.ngo.models;

public class PostModel {
    private String id, user_id, user_name, working_area_id, details, location, text, photo, date, post_ids;

    public String getPost_ids() {
        return post_ids;
    }

    public void setPost_ids(String post_ids) {
        this.post_ids = post_ids;
    }

    private boolean isAddToReporting = false;

    public boolean isAddToReporting() {
        return isAddToReporting;
    }

    public void setAddToReporting(boolean addToReporting) {
        isAddToReporting = addToReporting;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getWorking_area_id() {
        return working_area_id;
    }

    public void setWorking_area_id(String working_area_id) {
        this.working_area_id = working_area_id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
