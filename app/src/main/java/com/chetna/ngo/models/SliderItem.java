package com.chetna.ngo.models;

public class SliderItem {
    private String name, image, id, text,project_id;

    public SliderItem(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public SliderItem(String name, String image, String id, String text,String project_id) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.text = text;
        this.project_id = project_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
