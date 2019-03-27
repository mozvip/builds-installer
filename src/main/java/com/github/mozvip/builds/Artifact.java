package com.github.mozvip.builds;

import java.nio.file.Path;
import java.time.ZonedDateTime;

public class Artifact {

    private Path file;
    private ZonedDateTime dateTime;

    public Artifact(Path file, ZonedDateTime dateTime) {
        this.file = file;
        this.dateTime = dateTime;
    }

    public Path getFile() {
        return file;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "file=" + file +
                ", dateTime=" + dateTime +
                '}';
    }
}
