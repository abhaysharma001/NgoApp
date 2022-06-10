package com.chetna.ngo.models;

import java.io.File;

public class FilesModel {
    private String file_name;
    private File file;
    private String content_type;

    public FilesModel(String file_name, File file, String content_type) {
        this.file_name = file_name;
        this.file = file;
        this.content_type = content_type;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }
}

