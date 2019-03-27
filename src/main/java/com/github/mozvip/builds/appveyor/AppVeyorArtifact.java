package com.github.mozvip.builds.appveyor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppVeyorArtifact {

    private ZonedDateTime created;
    private String fileName;
    private String name;
    private String file;
    private int size;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "AppVeyorArtifact{" +
                "created=" + created +
                ", fileName='" + fileName + '\'' +
                ", name='" + name + '\'' +
                ", file='" + file + '\'' +
                ", size=" + size +
                '}';
    }
}
