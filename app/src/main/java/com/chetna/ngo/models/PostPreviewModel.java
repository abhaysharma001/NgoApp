package com.chetna.ngo.models;

import android.net.Uri;

import java.io.File;

public class PostPreviewModel {
    private String absPath;
    private Uri image;
    private File file;


    public PostPreviewModel(Uri image, File file, String absPath) {
        this.image = image;
        this.file = file;
        this.absPath = absPath;
    }


    public String getAbsPath() {
        return absPath;
    }

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
