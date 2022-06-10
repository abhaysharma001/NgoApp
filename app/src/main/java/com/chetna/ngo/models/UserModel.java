package com.chetna.ngo.models;

public class UserModel {
    private String id,name,email,phone;
    private boolean is_added_to_this_project;

    public boolean isIs_added_to_this_project() {
        return is_added_to_this_project;
    }

    public void setIs_added_to_this_project(boolean is_added_to_this_project) {
        this.is_added_to_this_project = is_added_to_this_project;
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
}
