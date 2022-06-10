package com.chetna.ngo.models;

public class FrontLineWorkerListModel {
    private String id, user_id, user_name, verify_status, project_id, project_name, user_type, project_cordinator_id, status, post_count, working_area_id, working_area_name;

    public String getWorking_area_id() {
        return working_area_id;
    }

    public void setWorking_area_id(String working_area_id) {
        this.working_area_id = working_area_id;
    }

    public String getWorking_area_name() {
        return working_area_name;
    }

    public void setWorking_area_name(String working_area_name) {
        this.working_area_name = working_area_name;
    }

    public FrontLineWorkerListModel() {
    }

    public FrontLineWorkerListModel(String id, String user_id, String user_name, String verify_status, String project_id, String project_name, String user_type, String project_cordinator_id, String status, String post_count) {
        this.id = id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.verify_status = verify_status;
        this.project_id = project_id;
        this.project_name = project_name;
        this.user_type = user_type;
        this.project_cordinator_id = project_cordinator_id;
        this.status = status;
        this.post_count = post_count;
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

    public String getVerify_status() {
        return verify_status;
    }

    public void setVerify_status(String verify_status) {
        this.verify_status = verify_status;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getProject_cordinator_id() {
        return project_cordinator_id;
    }

    public void setProject_cordinator_id(String project_cordinator_id) {
        this.project_cordinator_id = project_cordinator_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPost_count() {
        return post_count;
    }

    public void setPost_count(String post_count) {
        this.post_count = post_count;
    }
}
