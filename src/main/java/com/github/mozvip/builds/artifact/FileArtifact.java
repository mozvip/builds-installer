package com.github.mozvip.builds.artifact;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZonedDateTime;

public class FileArtifact extends Artifact {

    private Path file;

    public FileArtifact(String name, ZonedDateTime dateTime, Path file) {
        super(name, dateTime);
        this.file = file;
    }

    @Override
    public Path toLocalPath() throws IOException {
        return file;
    }
}
