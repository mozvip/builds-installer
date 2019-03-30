package com.github.mozvip.builds.artifact;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZonedDateTime;

public abstract class Artifact {

    private String name;
    private ZonedDateTime dateTime;

    public Artifact(String name, ZonedDateTime dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }


    public abstract Path toLocalPath() throws IOException;
}
